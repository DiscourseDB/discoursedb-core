package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

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
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseRelationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model.ProsoloBlogPost;

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
		if(contributionService.findOneByDataSource(p.getId(),dataSetName).isPresent()){
			logger.warn("Post " + p.getId()+" already in database. Skipping Post");
			return;
		}
		logger.trace("Mapping post " + p.getId());
		
		logger.trace("Init Discourse entity");
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);

		logger.trace("Init DiscoursePart entity");
		// in edX, we consider the whole forum to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,DiscoursePartTypes.PROSOLO_BLOG);
		
		logger.trace("Init User entity");
		User curUser  = userService.createOrGetUser(curDiscourse,p.getAuthor());
		dataSourceService.addSource(curUser, new DataSourceInstance(p.getAuthor(),"author",DataSourceTypes.PROSOLO_BLOG,dataSetName));									

		// ---------- Create Contribution and Content -----------
		//Check if contribution exists already. This could only happen if we import the same dump multiple times.
		Optional<Contribution> existingContribution = contributionService.findOneByDataSource(p.getId(),dataSetName);
		Contribution curContribution=null;
		if(!existingContribution.isPresent()){		
			logger.trace("Create Content entity");
			Content curContent = contentService.createContent();
			curContent.setText(p.getText());
			//curContent.setStartTime(p.getCreatedAt());
			curContent.setAuthor(curUser);
			
			logger.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(ContributionTypes.POST);
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			//curContribution.setStartTime(p.getCreatedAt());
			dataSourceService.addSource(curContribution, new DataSourceInstance(p.getId(),"id",DataSourceTypes.PROSOLO_BLOG,dataSetName));

			//Add contribution to DiscoursePart
			discoursePartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
		}

		
		
	}
	
}
