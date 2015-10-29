package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

import java.util.Date;
import java.util.Optional;

import org.apache.commons.lang.time.DateUtils;
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
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseRelationService;
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
	@Autowired private DiscourseRelationService discourseRelationService;
	
	/**
	 * Maps a prosolo blog post to DiscourseDB.
	 * 
	 * @param p the blog post object to map to DiscourseDB
	 * @param discourseName the name of the discourse the post was part of
	 * @param dataSetName the name of the dataset the post was extracted from
	 */
	public void mapPost(ProsoloBlogPost p, String discourseName, String dataSetName) {				
		if(contributionService.findOneByDataSource(p.getId(),ProsoloBlogSourceMapping.BLOG_ID_TO_CONTRIBUTION, dataSetName).isPresent()){
			logger.warn("Post " + p.getId()+" already in database. Skipping Post");
			return;
		}
		logger.trace("Mapping blog post " + p.getId());
		
		logger.trace("Init Discourse entity");
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);

		logger.trace("Init DiscoursePart entity");
		// in edX, we consider the whole blog space to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,DiscoursePartTypes.PROSOLO_BLOG);
		
		logger.trace("Init User entity");
		//TODO map to edX user instead of creating new user
		//if no user name is available, we cannot create a user instance
		String authorName = p.getAuthor();
		User curUser=null;
		if(authorName!=null&&!authorName.isEmpty()){
			curUser  = userService.createOrGetUser(curDiscourse,authorName);
			dataSourceService.addSource(curUser, new DataSourceInstance(p.getAuthor(),ProsoloBlogSourceMapping.AUTHOR_NAME_TO_USER,DataSourceTypes.PROSOLO_BLOG,dataSetName));												
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
				mapComment(subComment, curContribution, curDiscourse, dataSetName);
			}
		}
	}

	public void mapComment(ProsoloBlogComment c, Contribution parent, Discourse curDiscourse, String dataSetName) {				
		logger.trace("Init DiscoursePart entity");
		// in edX, we consider the whole forum to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,DiscoursePartTypes.PROSOLO_BLOG);
		
		logger.trace("Init User entity for comment");
		//TODO map to edX user instead of creating new user
		//if no user name is available, we cannot create a user instance
		String authorName = c.getAuthor();
		User curUser=null;
		if(authorName!=null&&!authorName.isEmpty()){
			curUser  = userService.createOrGetUser(curDiscourse,authorName);
			dataSourceService.addSource(curUser, new DataSourceInstance(authorName,ProsoloBlogSourceMapping.AUTHOR_NAME_TO_USER,DataSourceTypes.PROSOLO_BLOG,dataSetName));												
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
		if(parent.getType().getType().equals(ContributionTypes.THREAD_STARTER.name())){
			//if the parent is the blog post (THREAD-STARTER), we actually have a "comment" 
			discourseRelationService.createDiscourseRelation(parent, curContribution, DiscourseRelationTypes.COMMENT);			
		}else{
			//if the parent is the a comment, we consider the comment to be a reply to the comment  
			discourseRelationService.createDiscourseRelation(parent, curContribution, DiscourseRelationTypes.REPLY);						
		}
		
		//recursively map the comments to the comments (replies)		
		for(ProsoloBlogComment subComment:c.getComments()){
			mapComment(subComment, curContribution, curDiscourse, dataSetName);
		}
	
	}
	
	/**
	 * Parses a date using a set of date patterns. Returns null if String couldn't be parsed
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
