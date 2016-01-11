package edu.cmu.cs.lti.discoursedb.io.bazaar.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartInteractionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.bazaar.model.BazaarSourceMapping;
import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Message;
import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Room;

@Service
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
public class BazaarConverterService {
	
	@Autowired
	private DataSourceService dataSourceService;
	@Autowired
	private DiscourseService discourseService;
	@Autowired
	private UserService userService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private ContributionService contributionService;
	@Autowired
	private DiscoursePartService discoursepartService;
	
	private static final Logger logger = LogManager.getLogger(BazaarConverterService.class);
	
	public void mapMessage(Message m, String dataSetName, String discourseName, HashMap<String, String> map) throws ParseException {
		if (contributionService.findOneByDataSource(m.getId(), BazaarSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).isPresent()) {
			logger.warn("Message " + m.getId() + " already in database. Skipping Message");
			return;
		}
		
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		User curUser = userService.createOrGetUser(curDiscourse, m.getUsername());
		dataSourceService.addSource(curUser,
				new DataSourceInstance(m.getUsername(), BazaarSourceMapping.FROM_USER_ID_STR_TO_USER,DataSourceTypes.BAZAAR, dataSetName));
		
		Optional<Contribution> existingContribution = 
				contributionService.findOneByDataSource(
						m.getId(),BazaarSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);
		
		if (!existingContribution.isPresent()) {
			ContributionTypes mappedType = null;
			if(m.getType().equals("text"))
				mappedType = ContributionTypes.POST;
			else if(m.getType().equals("image"))
				mappedType = ContributionTypes.BAZAAR_IMAGE;
			else if(m.getType().equals("private"))
				mappedType = ContributionTypes.BAZAAR_PRIVATE;
				
			//add content entity to database
			logger.trace("Create Content entity");
			Content curContent = contentService.createContent();
			curContent.setText(m.getContent());
			if(m.getCreated_time()!=null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				//System.out.println(m.getCreated_time());
				java.util.Date date = sdf.parse(m.getCreated_time());
				//System.out.println(sdf.format(date));
				curContent.setStartTime(date);
				curContent.setEndTime(date);
			}
			curContent.setAuthor(curUser);
			dataSourceService.addSource(
					curContent, new DataSourceInstance(
							m.getId(), BazaarSourceMapping.ID_STR_TO_CONTENT, DataSourceTypes.BAZAAR, dataSetName));
			
			//add contribution entity to database
			logger.trace("Create Contribution entity");
			Contribution curContribution = contributionService.createTypedContribution(mappedType);
			curContribution.setCurrentRevision(curContent);
			if(m.getCreated_time()!=null) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				java.util.Date date = sdf.parse(m.getCreated_time());
				java.sql.Date sdate = new java.sql.Date(date.getTime());
				curContribution.setStartTime(sdate);
				curContribution.setEndTime(sdate);
			}
			curContribution.setFirstRevision(curContent);
			dataSourceService.addSource(curContribution,
					new DataSourceInstance(
							m.getId(), BazaarSourceMapping.ID_STR_TO_CONTRIBUTION, DataSourceTypes.BAZAAR, dataSetName));
			
			//map relation between contribution and discoursepart
			DiscoursePart curDiscoursePart = 
					discoursepartService.createOrGetTypedDiscoursePart(
							curDiscourse, map.get(m.getRoomid()), DiscoursePartTypes.BAZAAR_ROOM);
			discoursepartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
			
		}
		
	}
	
	public void mapRoom(Room r, String dataSetName, String discourseName) throws ParseException {

		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		
		
		//add discoursepartType entity to database
		DiscoursePart curDiscoursePart = 
				discoursepartService.createOrGetTypedDiscoursePart(
						curDiscourse, r.getName(), DiscoursePartTypes.BAZAAR_ROOM);
		if(r.getCreated_time()!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date = sdf.parse(r.getCreated_time());
			curDiscoursePart.setStartTime(date);
		}
		if(r.getModified_time()!=null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date date = sdf.parse(r.getModified_time());
			curDiscoursePart.setEndTime(date);
		}
		//curDiscoursePart.setName(r.getName());
		
	}
	
	public void mapInteraction(Message m, String discourseName, String dataSetName, HashMap<String, String> map) {
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		User curUser = userService.createOrGetUser(curDiscourse, m.getUsername());
		dataSourceService.addSource(curUser,
				new DataSourceInstance(m.getUsername(), BazaarSourceMapping.FROM_USER_ID_STR_TO_USER,DataSourceTypes.BAZAAR, dataSetName));
		DiscoursePart curDiscoursePart = 
				discoursepartService.createOrGetTypedDiscoursePart(
						curDiscourse, map.get(m.getRoomid()), DiscoursePartTypes.BAZAAR_ROOM);
		if(m.getContent().equals("join"))
			userService.createDiscoursePartInteraction(curUser, curDiscoursePart, DiscoursePartInteractionTypes.JOIN);
		if(m.getContent().equals("ready"))
			userService.createDiscoursePartInteraction(curUser, curDiscoursePart, DiscoursePartInteractionTypes.READY);
		if(m.getContent().equals("leave"))
			userService.createDiscoursePartInteraction(curUser, curDiscoursePart, DiscoursePartInteractionTypes.LEAVE);
		if(m.getContent().equals("global unready"))
			userService.createDiscoursePartInteraction(curUser, curDiscoursePart, DiscoursePartInteractionTypes.UNREADY);
		
	}
	
}
