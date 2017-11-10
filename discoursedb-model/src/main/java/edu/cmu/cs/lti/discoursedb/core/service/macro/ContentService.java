/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
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
package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class ContentService {

	private final @NonNull ContentRepository contentRepo;
	private final @NonNull DataSourceService dataSourceService;
	
	public Content createContent(){
		return contentRepo.save(new Content());
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<Content> findAll(List<Long> ids){
		Assert.notNull(ids, "List of content ids cannot be null.");
		Assert.notEmpty(ids, "List of content ids cannot be empty.");
		return contentRepo.findByIdIn(ids);
	}
	
	public Content save(Content content){
		Assert.notNull(content, "Content cannot be null.");
		return contentRepo.save(content);
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<Content> findOne(Long id){
		Assert.notNull(id, "Content id cannot be null.");
		Assert.isTrue(id>0, "Content id has to be a positive number.");
		return contentRepo.findOne(id);
	}
	
	public void setNextRevision(Long id, Long nextRevId){
		Assert.notNull(id, "Content id cannot be null.");
		Assert.isTrue(id>0, "Content id has to be a positive number.");
		Assert.notNull(nextRevId, "Next revision id cannot be null.");
		Assert.isTrue(nextRevId>0, "Next revision id has to be a positive number.");
		Assert.isTrue(id!=nextRevId, "Next revision cannot equal the current revision.");		
		contentRepo.setNextRevisionId(id, nextRevId);
	}
	
	public void setPreviousRevision(Long id, Long previousRevId){
		Assert.notNull(id, "Content id cannot be null.");
		Assert.isTrue(id>0, "Content id has to be a positive number.");
		Assert.notNull(previousRevId, "Previous revision id cannot be null.");
		Assert.isTrue(previousRevId>0, "Previous revision id has to be a positive number.");
		Assert.isTrue(id!=previousRevId, "Previous revision cannot equal the current revision.");		
		contentRepo.setPreviousRevisionId(id, previousRevId);
	}

	public Optional<Content> findOneByDataSourceId(String entitySourceId) {
		return contentRepo.findOneByDataSourceId(entitySourceId);
	}
	
	public Content createOrGetContentByDataSource(Discourse discourse, String entitySourceId, 
			String entitySourceDescriptor, DataSourceTypes sourceType, String datasetName) {
		Assert.notNull(discourse, "Discourse cannot be null.");
		Assert.hasText (entitySourceId, "");		
		
		Optional<Content> oc = contentRepo.findOneByDataSourceId(entitySourceId);
		Content c = null;
		if (oc.isPresent()) {
			c = oc.get();
		} else {
			c = createContent();
			DataSourceInstance ds = new DataSourceInstance(entitySourceId, entitySourceDescriptor, sourceType, datasetName);
			dataSourceService.addSource(c, ds);
		}
		
		return c;
	}


}