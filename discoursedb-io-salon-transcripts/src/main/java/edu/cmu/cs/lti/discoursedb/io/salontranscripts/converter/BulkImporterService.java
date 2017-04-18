/*******************************************************************************
 * Copyright (C)  2015 - 2017  Carnegie Mellon University
 * Author: Oliver Ferschke, Chris Bogart
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionContext;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseToDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.CustomCrudRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseToDiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.SalonTrConverterService;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.models.BulkImportOrder;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.models.SalonTranscript;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * Service for mapping data retrieved from salon classroom transcripts to DiscourseDB
 * 
 * @author Chris Bogart
 *
 */
@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class BulkImporterService {
	private static final Logger logger = LogManager.getLogger(BulkImporterService.class);

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursePartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull AnnotationService annoService;
	private final @NonNull DiscoursePartContributionRepository discoursePartContributionRepo;
	private final @NonNull DiscourseRelationRepository discourseRelationRepo;
	private final @NonNull DiscoursePartRepository discoursePartRepo;
	private final @NonNull DiscoursePartRelationRepository discoursePartRelationRepo;
	private final @NonNull DiscourseToDiscoursePartRepository discourseToDiscoursePartRepo;
	
	private Map<String,Object> dbobjects = new HashMap<String,Object>();
	private Map<String,Long> dbkeys = new HashMap<String,Long>();
	private List<BulkImportOrder> forLater = new ArrayList<BulkImportOrder>();


	
	private Object findPrevious(String table, String idstring) {
		String key = table + "**" + idstring;
		if (dbobjects.containsKey(key)) {
			return dbobjects.get(key);
		}
		else if (dbkeys.containsKey(key)) {
			switch (table) {
			case "discourse":
				discourseService.findOne( dbkeys.get(key)).map(d -> {
					dbobjects.put(key, d);
					return d;});
				break;
			case "discourse_part":
				discoursePartService.findOne( dbkeys.get(key)).map(d -> {
					dbobjects.put(key, d);
					return d;});
				break;	
			case "contribution":
				contributionService.findOne( dbkeys.get(key)).map(d -> {
					dbobjects.put(key, d);
					return d;});
				break;	
			case "content":
				contentService.findOne( dbkeys.get(key)).map(d -> {
					dbobjects.put(key, d);
					return d;});
				break;	
			case "user":
				userService.findOne( dbkeys.get(key)).map(d -> {
					dbobjects.put(key, d);
					return d;});
				break;	
			case "discourse_relation":
				discourseRelationRepo.findOne( dbkeys.get(key)).map(d -> {
					dbobjects.put(key, d);
					return d;});
				break;	
			case "discourse_part_relation":
				discoursePartRelationRepo.findOne( dbkeys.get(key)).map(d -> {
					dbobjects.put(key, d);
					return d;});
				break;	
			case "contribution_partof_discourse_part":
				discoursePartContributionRepo.findOne( dbkeys.get(key)).map(d -> {
					dbobjects.put(key, d);
					return d;});
				break;	
			}
		}
		return null;
	}
	private boolean exists(String key) {
		return dbobjects.containsKey(key) || dbkeys.containsKey(key);
	}
	
	private void save(String key, String table, Object item, String idstring, Long index) {
		dbobjects.put(key, item);
		dbkeys.put(key, index);
	}
	private void clear_detached() {
		dbobjects = new HashMap<String,Object>();
	}
	
	public class MissingParameterException extends Exception {
		public MissingParameterException(String message) {
			super(message);
		}
	}
	
	private void check(BulkImportOrder o, String key) throws MissingParameterException {
		if (!o.has(key)) {
			throw new MissingParameterException(o.getKey() + " must have " + key);
		}
	}
	
	static SimpleDateFormat sdfmt= new SimpleDateFormat();
		
	public void fixup(String dataset, DataSourceTypes dsType) throws MissingParameterException {
		int forLaterCount = forLater.size();
		if (forLaterCount > 0) {
			logger.info("Fixup: handling " + forLaterCount + " postponed records, such as " + forLater.get(0).getKey());
			while (forLater.size() > 0) {
				List<BulkImportOrder> doNow = forLater;
				forLater = new ArrayList<BulkImportOrder>();
				innerBatchImport(doNow, dataset, dsType);
				if (forLater.size() == doNow.size()) {
					logger.info("There are circular references among " + forLater.size() + " remaining records such as ", forLater.get(0).getKey());
					forLater.clear();
				}
			}
		}
		// For all contributions
		//    Order contents and set prev/next links among them
		//    set contribution first and current revisions to first and last contents
		//    set start/end time on contributions to start of fist content and end of last content
	}
	
	public void batchImport(List<BulkImportOrder> orders, String dataset, DataSourceTypes dsType) throws MissingParameterException {
		clear_detached();
		innerBatchImport(orders, dataset, dsType);
		fixup(dataset, dsType);
	}
	public void innerBatchImport(List<BulkImportOrder> orders, String dataset, DataSourceTypes dsType) throws MissingParameterException {
				for (BulkImportOrder o : orders) {
			if (exists(o.getKey())) { 
				//logger.info("Ignoring line: repetition of " + o.getTable() + o.getSourceItemId());
				continue;
			}
			try {
			switch (o.getTable()) {
			
			case "discourse": 
				check(o,"name"); 
				Discourse d = new Discourse();
				d.setName(o.getParam("name"));
				discourseService.save(d);
				save(o.getKey(), "discourse", d, o.getSourceItemId(), d.getId());
				break;
			case "discourse_part": 
				// id, type, [start, end], name, discourse
				check(o,"type"); check(o,"name"); check(o,"fk_discourse");
				Discourse di = (Discourse)findPrevious("discourse", o.getParam("fk_discourse"));
				if (di == null) {
					forLater.add(o);
				} else {
					
					DiscoursePart dPart=new DiscoursePart();
					dPart.setType(o.getParam("type"));
					dPart.setName(o.getParam("name"));
					dPart = discoursePartRepo.save(dPart);
					
					DiscourseToDiscoursePart discourseToDiscoursePart = new DiscourseToDiscoursePart();			
					discourseToDiscoursePart.setDiscourse(di);
					discourseToDiscoursePart.setDiscoursePart(dPart);
					discourseToDiscoursePartRepo.save(discourseToDiscoursePart);		
					
					//DiscoursePart dp = discoursePartService.createTypedDiscoursePart(di,DiscoursePartTypes.valueOf(o.getParam("type")) );
					//dp.setName(o.getParam("name"));
					dataSourceService.addSource(dPart, new DataSourceInstance(
							o.getSourceItemId(), o.getSourceDescriptor(), dsType, dataset));
					save(o.getKey(), "discourse_part", dPart, o.getSourceItemId(), dPart.getId());
				}
				break;
			case "user": 
				// end_time | start_time | country | email | ip   | language | location | realname | username
				check(o,"username"); 
				User u = new User();
				u.setUsername(o.getParam("username"));
				if (o.has("email")) { u.setEmail(o.getParam("email")); }
				if (o.has("country")) { u.setEmail(o.getParam("country")); }
				if (o.has("language")) { u.setEmail(o.getParam("language")); }
				if (o.has("realname")) { u.setEmail(o.getParam("realname")); }
				dataSourceService.addSource(u, new DataSourceInstance(
						o.getSourceItemId(), o.getSourceDescriptor(), dsType, dataset));
				userService.save(u);
				save(o.getKey(), "user", u, o.getSourceItemId(), u.getId());
				
				break;
			case "contribution": 
				// id_contribution | entity_created      | entity_modified     | entity_version | type             | end_time | start_time        
				//| downvotes | upvotes | fk_annotation | fk_data_sources | fk_current_revision | fk_first_revision
				check(o,"type"); check(o,"start_time"); check(o, "fk_current_revision");
				
				Content oldc = (Content)findPrevious("content", o.getParam("fk_current_revision"));
				if (oldc == null) {
					forLater.add(o);
				} else {
					Contribution c = new Contribution();
					c.setStartTime(sdfmt.parse(o.getParam("start_time")));
					c.setType(o.getParam("type"));
					c.setCurrentRevision(oldc);
					if (o.has("end_time")) { c.setEndTime(sdfmt.parse(o.getParam("end_time")));	}				
					else { c.setEndTime(sdfmt.parse(o.getParam("start_time"))); }
					dataSourceService.addSource(c, new DataSourceInstance(
							o.getSourceItemId(), o.getSourceDescriptor(), dsType, dataset));
					contributionService.save(c);
					save(o.getKey(), "contribution", c, o.getSourceItemId(), c.getId());
				}
				break;
			case "content": 
				// id_content | entity_created      | entity_modified     | entity_version | end_time | start_time          | data | text                                                                                                                                                                                                                                                                                                       
				// | title | fk_annotation | fk_data_sources | fk_user_id | fk_next_revision | fk_previous_revision
				check(o,"text"); check(o,"fk_user_id");  check(o,"start_time");
				
				User uc = (User)findPrevious("user", o.getParam("fk_user_id"));
				if (uc == null) {
					forLater.add(o);
				} else {
					Content c1 = new Content();
					c1.setAuthor(uc);
					c1.setStartTime(sdfmt.parse(o.getParam("start_time")));
					if (o.has("end_time")) { c1.setEndTime(sdfmt.parse(o.getParam("end_time")));}	
					else { c1.setEndTime(sdfmt.parse(o.getParam("start_time"))); }
					if (o.has("title")) { c1.setTitle(o.getParam("title"));	 }	
					if (o.has("data")) { c1.setTitle(o.getParam("data"));}	
					c1.setText(o.getParam("text"));
					dataSourceService.addSource(c1, new DataSourceInstance(
							o.getSourceItemId(), o.getSourceDescriptor(), dsType, dataset));
					contentService.save(c1);
					save(o.getKey(), "content", c1, o.getSourceItemId(), c1.getId());
				}
				break;
			case "discourse_relation": 
				// id_content | entity_created      | entity_modified     | entity_version | end_time | start_time          | data | text                                                                                                                                                                                                                                                                                                       
				// | title | fk_annotation | fk_data_sources | fk_user_id | fk_next_revision | fk_previous_revision
				check(o,"type"); check(o,"fk_source");  check(o,"fk_target");
				
				Contribution csrc = (Contribution)findPrevious("contribution", o.getParam("fk_source"));
				Contribution ctrg = (Contribution)findPrevious("contribution", o.getParam("fk_target"));
				if (csrc == null || ctrg == null) {
					forLater.add(o);
				} else {
					DiscourseRelation dr = new DiscourseRelation();
					dr.setSource(csrc);
					dr.setTarget(ctrg);
					dr.setType(o.getParam("type"));
					discourseRelationRepo.save(dr);
					save(o.getKey(), "discourse_relation", dr, o.getSourceItemId(), dr.getId());
				}
				break;
			case "discourse_part_relation": 
				// id_content | entity_created      | entity_modified     | entity_version | end_time | start_time          | data | text                                                                                                                                                                                                                                                                                                       
				// | title | fk_annotation | fk_data_sources | fk_user_id | fk_next_revision | fk_previous_revision
				check(o,"type"); check(o,"fk_source");  check(o,"fk_target");
				
				DiscoursePart dsrc = (DiscoursePart)findPrevious("discourse_part", o.getParam("fk_source"));
				DiscoursePart dtrg = (DiscoursePart)findPrevious("discourse_part", o.getParam("fk_target"));
				if (dsrc == null || dtrg == null) {
					forLater.add(o);
				} else {
					DiscoursePartRelation dpr = new DiscoursePartRelation();
					dpr.setSource(dsrc);
					dpr.setTarget(dtrg);
					dpr.setType(o.getParam("type"));
					discoursePartRelationRepo.save(dpr);
					save(o.getKey(), "discourse_part_relation", dpr, o.getSourceItemId(), dpr.getId());
				}
				break;
			case "contribution_partof_discourse_part": 
				// id_content | entity_created      | entity_modified     | entity_version | end_time | start_time          | data | text                                                                                                                                                                                                                                                                                                       
				// | title | fk_annotation | fk_data_sources | fk_user_id | fk_next_revision | fk_previous_revision
				check(o,"fk_contribution");  check(o,"fk_discourse_part");
				
				Contribution ccont = (Contribution)findPrevious("contribution", o.getParam("fk_contribution"));
				DiscoursePart dptrg = (DiscoursePart)findPrevious("discourse_part", o.getParam("fk_discourse_part"));
				if (ccont == null || dptrg == null) {
					forLater.add(o);
				} else {
					DiscoursePartContribution newDPContrib = new DiscoursePartContribution();
					newDPContrib.setContribution(ccont);
					newDPContrib.setDiscoursePart(dptrg);
					newDPContrib.setStartTime(ccont.getStartTime());
					if (o.has("type")) { newDPContrib.setType(o.getParam("type")); }
					discoursePartContributionRepo.save(newDPContrib);
					
					save(o.getKey(), "contribution_partof_discourse_part", newDPContrib, o.getSourceItemId(), newDPContrib.getId());
				}
				break;
			}
		} catch (ParseException pe) {
			logger.info("Parsing exception in "  + o.getTable() + o.getSourceItemId());
		} 
		}
	}
}
