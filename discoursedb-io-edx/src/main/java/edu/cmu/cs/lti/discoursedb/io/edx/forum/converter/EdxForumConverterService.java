package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

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
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.EdxSourceMapping;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.UserInfo;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class EdxForumConverterService{

	private static final String EDX_COMMENT_TYPE = "Comment";
	
	private final @NonNull DiscourseService discourseService;
	private final @NonNull UserService userService;
	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursePartService;

	/**
	 * Maps a post to DiscourseDB entities.
	 * 
	 * @param p the post object to map to DiscourseDB
	 * @param dataSetName the name of the dataset the post was extracted from
	 */
	public void mapEntities(Post p, String dataSetName) {				
		Assert.notNull(p,"Cannot map relations for post. Post data was null.");
		Assert.hasText(dataSetName,"Cannot map post. DataSetName not specified.");

		if(contributionService.findOneByDataSource(p.getId(),EdxSourceMapping.POST_ID_TO_CONTRIBUTION,dataSetName).isPresent()){
			log.warn("Post " + p.getId()+" already in database. Skipping Post");
			return;
		}
	
		log.trace("Mapping post " + p.getId());
		
		log.trace("Init Discourse entity");
		String courseid = p.getCourseId();
		Discourse curDiscourse = discourseService.createOrGetDiscourse(courseid);

		log.trace("Init DiscoursePart entity");
		// in edX, we consider the whole forum to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,courseid+"_FORUM",DiscoursePartTypes.FORUM);
		
		log.trace("Init User entity");
		User curUser  = userService.createOrGetUser(curDiscourse,p.getAuthorUsername());
		dataSourceService.addSource(curUser, new DataSourceInstance(p.getAuthorId(),EdxSourceMapping.AUTHOR_ID_TO_USER,DataSourceTypes.EDX, dataSetName));

		// ---------- Create Contribution and Content -----------
		//Check if contribution exists already. This could only happen if we import the same dump multiple times.
		contributionService.findOneByDataSource(p.getId(),EdxSourceMapping.POST_ID_TO_CONTRIBUTION, dataSetName).orElseGet(()->
			{
				ContributionTypes mappedType = p.getType().equals(EDX_COMMENT_TYPE)?ContributionTypes.POST:ContributionTypes.THREAD_STARTER;
	
				log.trace("Create Content entity");
				Content curContent = contentService.createContent();
				curContent.setText(p.getBody());
				curContent.setStartTime(p.getCreatedAt());
				curContent.setAuthor(curUser);
				
				log.trace("Create Contribution entity");
				Contribution curContribution = contributionService.createTypedContribution(mappedType);
				curContribution.setCurrentRevision(curContent);
				curContribution.setFirstRevision(curContent);
				curContribution.setStartTime(p.getCreatedAt());
				curContribution.setUpvotes(p.getUpvoteCount());
				dataSourceService.addSource(curContribution, new DataSourceInstance(p.getId(),EdxSourceMapping.POST_ID_TO_CONTRIBUTION,DataSourceTypes.EDX,dataSetName));
	
				//Add contribution to DiscoursePart
				discoursePartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
				return curContribution; //only necessary because orElseGet requires a return
			}
		);
				
		log.trace("Post mapping completed.");
	}
	
	
	
	/**
	 * Creates DiscourseRelations between the current post and existing posts already imported in an earlier Phase
	 * 
	 * @param p
	 *            the post object to map to DiscourseDB
	 */
	public void mapRelations(Post p, String dataSetName) {		
		Assert.notNull(p,"Cannot map relations for post. Post data was null.");
		Assert.hasText(dataSetName,"Cannot map post. DataSetName not specified.");

		log.trace("Mapping relations for post " + p.getId());
	
		//check if a contribution for the given Post already exists in DiscourseDB (imported in Phase1)
		contributionService.findOneByDataSource(p.getId(),EdxSourceMapping.POST_ID_TO_CONTRIBUTION,dataSetName).ifPresent(
				curContribution -> {
					//If current contribution is not a thread starter then create a DiscourseRelation of DESCENDANT type that connects it with the thread starter 
					if(p.getCommentThreadId()!=null){
						contributionService.findOneByDataSource(p.getCommentThreadId(),EdxSourceMapping.POST_ID_TO_CONTRIBUTION,dataSetName).ifPresent(
								threadStarter -> contributionService.createDiscourseRelation(threadStarter, curContribution, DiscourseRelationTypes.DESCENDANT));				
					}

					//If post is a reply to another post, then create a DiscourseRelation that connects it with its immediate parent			
					if(p.getParentId()!=null){
						contributionService.findOneByDataSource(p.getParentId(),EdxSourceMapping.POST_ID_TO_CONTRIBUTION,dataSetName).ifPresent(
								parent -> contributionService.createDiscourseRelation(parent , curContribution, DiscourseRelationTypes.REPLY));				
					}
				}
		);
		
		log.trace("Post relation mapping completed.");
	}
	
	/**
	 * Adds additional user information to existing DiscourseDB users.
	 * 
	 * @param u
	 *            the UserInfo object to map to DiscourseDB
	 */
	public void mapUser(UserInfo u) {
		Assert.notNull(u,"Cannot map user. UserInfo was null.");
		
		log.trace("Mapping UserInfo for user" + u.getUsername());
	
		userService.findUserBySourceIdAndUsername(u.getId()+"", u.getUsername()).ifPresent(curUser -> 
			{
				if(curUser.getEmail()==null||curUser.getEmail().isEmpty()){
					curUser.setEmail(u.getEmail());
				}
	
				curUser = userService.setRealname(curUser, u.getFirst_name(), u.getLast_name());
						
				if(curUser.getCountry()==null||curUser.getCountry().isEmpty()){
					curUser.setCountry(u.getCountry());
				}						
			}
		);
		
		log.trace("UserInfo mapping completed for user" + u.getUsername());
	}

}