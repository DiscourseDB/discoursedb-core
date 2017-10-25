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
package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

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
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model.ProsoloBlogComment;
import edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model.ProsoloBlogPost;
import edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model.ProsoloBlogSourceMapping;

/**
 * This Service class maps blog entries to DiscourseDB
 * 
 * @author Oliver Ferschke
 */
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class BlogConverterService {
	private static final Logger logger = LogManager.getLogger(BlogConverterService.class);	
		
	@Autowired private DiscourseService discourseService;
	@Autowired private UserService userService;
	@Autowired private DataSourceService dataSourceService;
	@Autowired private ContentService contentService;
	@Autowired private ContributionService contributionService;
	@Autowired private DiscoursePartService discoursePartService;
	
	/**
	 * Maps a prosolo blog post to DiscourseDB.
	 * 
	 * @param p the blog post object to map to DiscourseDB
	 * @param discourseName the name of the discourse the post was part of
	 * @param dataSetName the name of the dataset the post was extracted from
	 * @param blogToEdxMap a mapping from blog author names to edx username (possibly empty)
	 */
	public void mapPost(ProsoloBlogPost p, String discourseName, String dataSetName, Map<String, String> blogToEdxMap) {				
		Assert.notNull(p,"Cannot map post. Post data was null.");
		Assert.hasText(discourseName,"Cannot map post. Discourse name not specified.");
		Assert.hasText(dataSetName,"Cannot map post. DataSetName not specified.");

		
		if(contributionService.findOneByDataSource(p.getId(),ProsoloBlogSourceMapping.BLOG_ID_TO_CONTRIBUTION, dataSetName).isPresent()){
			logger.warn("Post " + p.getId()+" already in database. Skipping Post");
			return;
		}
		logger.trace("Mapping blog post " + p.getId());
		
		logger.trace("Init Discourse entity");
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName, dataSetName);

		logger.trace("Init DiscoursePart entity");
		// in edX, we consider the whole blog space to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,curDiscourse.getName()+"_"+DiscoursePartTypes.PROSOLO_BLOG.name(),DiscoursePartTypes.PROSOLO_BLOG);
		
		logger.trace("Init User entity");
		//if no user name is available, we cannot create a user instance
		//if we can map a user using the provided user name map, we can retrieve the existing user from the db 
		String edXauthor=blogToEdxMap.get(p.getAuthor());
		String authorName = edXauthor==null?p.getAuthor():edXauthor;
		User curUser=null;
		if(authorName!=null&&!authorName.isEmpty()){
			curUser  = userService.createOrGetUser(curDiscourse,authorName);
			if(edXauthor==null){
				//we only add this dataset as a datasource if we didn't map it to an edX name
				dataSourceService.addSource(curUser, new DataSourceInstance(p.getAuthor(),ProsoloBlogSourceMapping.AUTHOR_NAME_TO_USER,DataSourceTypes.PROSOLO_BLOG,dataSetName));																
			}
		}

		// ---------- Create Contribution and Content -----------
		//Check if contribution exists already. This could only happen if we import the same dump multiple times.
		Optional<Contribution> existingContribution = contributionService.findOneByDataSource(p.getId(),ProsoloBlogSourceMapping.BLOG_ID_TO_CONTRIBUTION,dataSetName);
		Contribution curContribution=null;
		if(!existingContribution.isPresent()){		
			logger.trace("Create Content entity");
			Content curContent = contentService.createContent();
			curContent.setText(p.getText());
			curContent.setStartTime(parseDate(p.getCreated()));
			if(curUser!=null){curContent.setAuthor(curUser);};
			dataSourceService.addSource(curContent, new DataSourceInstance(p.getId(),ProsoloBlogSourceMapping.BLOG_ID_TO_CONTENT,DataSourceTypes.PROSOLO_BLOG,dataSetName));
			
			logger.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(ContributionTypes.THREAD_STARTER);
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			curContribution.setStartTime(parseDate(p.getCreated()));
			dataSourceService.addSource(curContribution, new DataSourceInstance(p.getId(),ProsoloBlogSourceMapping.BLOG_ID_TO_CONTRIBUTION,DataSourceTypes.PROSOLO_BLOG,dataSetName));

			//Add contribution to DiscoursePart
			discoursePartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
			
			//map the comments to the blog posts		
			for (ProsoloBlogComment subComment : p.getComments()) {
				mapComment(subComment, curContribution, curDiscourse, dataSetName, blogToEdxMap);
			}
		}
	}

	/**
	 * @param c a comment to a prosolo blog
	 * @param parent the parent of the comment (either the article or another comment)
	 * @param curDiscourse the discourse
	 * @param dataSetName the dataset name
	 * @param blogToEdxMap a mapping from blog author names to edx username (possibly empty)
	 */
	public void mapComment(ProsoloBlogComment c, Contribution parent, Discourse curDiscourse, String dataSetName, Map<String,String> blogToEdxMap) {				
		Assert.notNull(c,"Cannot map comment. Comment data was null.");
		Assert.notNull(parent,"Cannot map comment. Parent contribution was null.");
		Assert.notNull(curDiscourse,"Cannot map comment. Discourse data was null.");
		Assert.hasText(dataSetName,"Cannot map post. DataSetName not specified.");

		logger.trace("Init DiscoursePart entity");
		// in edX, we consider the whole forum to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,curDiscourse.getName()+"_"+DiscoursePartTypes.PROSOLO_BLOG.name(),DiscoursePartTypes.PROSOLO_BLOG);
		
		logger.trace("Init User entity for comment");
		//if no user name is available, we cannot create a user instance
		//if we can map a user using the provided user name map, we can retrieve the existing user from the db 
		String edXauthor=blogToEdxMap.get(c.getAuthor());
		String authorName = edXauthor==null?c.getAuthor():edXauthor;
		User curUser=null;
		if(authorName!=null&&!authorName.isEmpty()){
			curUser  = userService.createOrGetUser(curDiscourse,authorName);
			if(edXauthor==null){
				//we only add this dataset as a datasource if we didn't map it to an edX name
				dataSourceService.addSource(curUser, new DataSourceInstance(c.getAuthor(),ProsoloBlogSourceMapping.AUTHOR_NAME_TO_USER,DataSourceTypes.PROSOLO_BLOG,dataSetName));
			}
		}
		
		// ---------- Create Contribution and Content -----------
		logger.trace("Create Content entity for comment");
		Content curContent = contentService.createContent();
		curContent.setText(c.getContent());
		curContent.setStartTime(parseDate(c.getDatetime()));
		if(curUser!=null){curContent.setAuthor(curUser);};
			
		logger.trace("Create Contribution entity for comment");
		Contribution curContribution = contributionService.createTypedContribution(ContributionTypes.POST);
		curContribution.setCurrentRevision(curContent);
		curContribution.setFirstRevision(curContent);
		curContribution.setStartTime(parseDate(c.getDatetime()));
		//Add contribution to DiscoursePart
		discoursePartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
		
		logger.trace("Create discourse relation between parent contribution and comment");
		if(parent.getType().equals(ContributionTypes.THREAD_STARTER.name())){
			//if the parent is the blog post (THREAD-STARTER), we actually have a "comment" 
			contributionService.createDiscourseRelation(parent, curContribution, DiscourseRelationTypes.COMMENT);			
		}else{
			//if the parent is the a comment, we consider the comment to be a reply to the comment  
			contributionService.createDiscourseRelation(parent, curContribution, DiscourseRelationTypes.REPLY);						
		}
		
		//recursively map the comments to the comments (replies)		
		for(ProsoloBlogComment subComment:c.getComments()){
			mapComment(subComment, curContribution, curDiscourse, dataSetName, blogToEdxMap);
		}
	
	}
	
	/**
	 * Parses a date using a set of date patterns. Returns null if String couldn't be parsed.
	 * 
	 * @param dateString date as String
	 * @return date from String or null if String is not parseable
	 */
	private Date parseDate(String dateString){
		if(dateString==null||dateString.isEmpty()){
			return null;
		}
		String[] datePatterns = new String[] { 
				"yyyy-MM-dd'T'HH:mm a", 
				"yyyy-MM-dd'T'", 
				"yyyy-MM-dd'T'HH:mm:ssXXX",
				"yyyy-MM-dd'T'HH:mm", 
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", 
				"yyyy-MM-dd'T'HH:mm:ss",
				"yyyy-MM-dd'T'HH:mm:ssXX" };
		try{
			return DateUtils.parseDate(dateString,datePatterns);
		}catch(Exception e){
			logger.warn("Could not parse date: "+dateString);
			return null;
		}
	}
	
}
