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
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
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
//	@Autowired private DiscourseRelationService discourseRelationService;
	
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
		// in edX, we consider the whole forum to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,DiscoursePartTypes.PROSOLO_BLOG);
		
		logger.trace("Init User entity");
		//TODO map to edX user instead of creating new user
		User curUser  = userService.createOrGetUser(curDiscourse,p.getAuthor());
		dataSourceService.addSource(curUser, new DataSourceInstance(p.getAuthor(),ProsoloBlogSourceMapping.AUTHOR_NAME_TO_USER,DataSourceTypes.PROSOLO_BLOG,dataSetName));									

		// ---------- Create Contribution and Content -----------
		//Check if contribution exists already. This could only happen if we import the same dump multiple times.
		Optional<Contribution> existingContribution = contributionService.findOneByDataSource(p.getId(),ProsoloBlogSourceMapping.BLOG_ID_TO_CONTRIBUTION,dataSetName);
		Contribution curContribution=null;
		if(!existingContribution.isPresent()){		
			logger.trace("Create Content entity");
			Content curContent = contentService.createContent();
			curContent.setText(p.getText());
			curContent.setStartTime(parseDate(p.getCreated()));
			curContent.setAuthor(curUser);
			dataSourceService.addSource(curContent, new DataSourceInstance(p.getId(),ProsoloBlogSourceMapping.BLOG_ID_TO_CONTENT,DataSourceTypes.PROSOLO_BLOG,dataSetName));
			
			logger.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(ContributionTypes.POST);
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			curContribution.setStartTime(parseDate(p.getCreated()));
			dataSourceService.addSource(curContribution, new DataSourceInstance(p.getId(),ProsoloBlogSourceMapping.BLOG_ID_TO_CONTRIBUTION,DataSourceTypes.PROSOLO_BLOG,dataSetName));

			//Add contribution to DiscoursePart
			discoursePartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
			
			//TODO recursively map comments
		}
	}

	
	/**
	 * Parses a date using a set of date patterns. Returns null if String couldn't be parsed
	 * 
	 * @param dateString date as String
	 * @return date from String or null if String is not parseable
	 */
	private Date parseDate(String dateString){
		String[] datePatterns = new String[]{"yyyy-MM-dd'T'HH:mm a","yyyy-MM-dd'T'","yyyy-MM-dd'T'HH:mm:ssXXX"};
		try{
			return DateUtils.parseDate(dateString,datePatterns);
		}catch(Exception e){
			return null;
		}
	}
	
}
