package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

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
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.UserInfo;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class EdxForumConverterService{

	private static final Logger logger = LogManager.getLogger(EdxForumConverterService.class);
	private static final String EDX_COMMENT_TYPE = "Comment";
	
	@Autowired private DiscourseService discourseService;
	@Autowired private UserService userService;
	@Autowired private DataSourceService dataSourceService;
	@Autowired private ContentService contentService;
	@Autowired private ContributionService contributionService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private DiscourseRelationService discourseRelationService;

	/**
	 * Maps a post to DiscourseDB entities.
	 * 
	 * @param p the post object to map to DiscourseDB
	 * @param dataSetName the name of the dataset the post was extracted from
	 */
	public void mapEntities(Post p, String dataSetName) {				
		if(contributionService.findOneByDataSource(p.getId(),dataSetName).isPresent()){
			logger.warn("Post " + p.getId()+" already in database. Skipping Post");
			return;
		}
	
		logger.trace("Mapping post " + p.getId());
		
		logger.trace("Init Discourse entity");
		String courseid = p.getCourseId();
		Discourse curDiscourse = discourseService.createOrGetDiscourse(courseid);

		logger.trace("Init DiscoursePart entity");
		// in edX, we consider the whole forum to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,courseid+"_FORUM",DiscoursePartTypes.FORUM);
		
		logger.trace("Init User entity");
		User curUser  = userService.createOrGetUser(curDiscourse,p.getAuthorUsername());
		dataSourceService.addSource(curUser, new DataSourceInstance(p.getAuthorId(),"authorId",DataSourceTypes.EDX, dataSetName));

		// ---------- Create Contribution and Content -----------
		//Check if contribution exists already. This could only happen if we import the same dump multiple times.
		Optional<Contribution> existingContribution = contributionService.findOneByDataSource(p.getId(),dataSetName);
		Contribution curContribution=null;
		if(!existingContribution.isPresent()){		
			ContributionTypes mappedType = p.getType().equals(EDX_COMMENT_TYPE)?ContributionTypes.POST:ContributionTypes.THREAD_STARTER;

			logger.trace("Create Content entity");
			Content curContent = contentService.createContent();
			curContent.setText(p.getBody());
			curContent.setStartTime(p.getCreatedAt());
			curContent.setAuthor(curUser);
			
			logger.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(mappedType);
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			curContribution.setStartTime(p.getCreatedAt());
			curContribution.setUpvotes(p.getUpvoteCount());
			dataSourceService.addSource(curContribution, new DataSourceInstance(p.getId(),"id",DataSourceTypes.EDX,dataSetName));

			//Add contribution to DiscoursePart
			discoursePartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
		}

				
		logger.trace("Post mapping completed.");
	}
	
	
	
	/**
	 * Creates DiscourseRelations between the current post and existing posts already imported in an earlier Phase
	 * 
	 * @param p
	 *            the post object to map to DiscourseDB
	 */
	public void mapRelations(Post p, String dataSetName) {		
		logger.trace("Mapping relations for post " + p.getId());
	
		//check if a contribution for the given Post already exists in DiscourseDB (imported in Phase1)
		Optional<Contribution> existingContribution = contributionService.findOneByDataSource(p.getId(),"id",dataSetName);
		if(existingContribution.isPresent()){
			Contribution curContribution=existingContribution.get();
			
			//If post is not a thread starter then create a DiscourseRelation of DESCENDANT type 
			//that connects it with the thread starter 
			Optional<Contribution> existingParentContributon = contributionService.findOneByDataSource(p.getCommentThreadId(),"id",dataSetName);
			if (existingParentContributon.isPresent()) {
				discourseRelationService.createDiscourseRelation(existingParentContributon.get(), curContribution, DiscourseRelationTypes.DESCENDANT);
			}

			//If post is a reply to another post, then create a DiscourseRelation that connects it with its immediate parent
			Optional<Contribution> existingPredecessorContributon = contributionService.findOneByDataSource(p.getParentId(),"id",dataSetName);
			if (existingPredecessorContributon.isPresent()) {
				discourseRelationService.createDiscourseRelation(existingPredecessorContributon.get(), curContribution, DiscourseRelationTypes.REPLY);			
			}					
		}else{
			logger.warn("No Contribution for Post "+p.getId()+" found in DiscourseDB. It should have been imported in Phase1.");
		}
		
		logger.trace("Post relation mapping completed.");
	}
	
	/**
	 * Adds additional user information to existing DiscourseDB users.
	 * 
	 * @param u
	 *            the UserInfo object to map to DiscourseDB
	 */
	public void mapUser(UserInfo u) {
		logger.trace("Mapping UserInfo for user" + u.getUsername());
	
		Optional<User> existingUser = userService.findUserBySourceIdAndUsername(u.getId()+"", u.getUsername());
		if(!existingUser.isPresent()){
			return;
		}
		User curUser=existingUser.get();
		if(curUser.getEmail()==null||curUser.getEmail().isEmpty()){
			curUser.setEmail(u.getEmail());
		}

		curUser = userService.setRealname(curUser, u.getFirst_name(), u.getLast_name());
				
		if(curUser.getCountry()==null||curUser.getCountry().isEmpty()){
			curUser.setCountry(u.getCountry());
		}
		logger.trace("UserInfo mapping completed for user" + u.getUsername());
	}

}