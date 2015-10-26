package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import java.sql.SQLException;
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
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.model.user.UserRelation;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseRelationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionInteractionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.UserRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.io.ProsoloDB;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloFollowedEntity;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloNode;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloPost;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloUser;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.SocialActivity;

/**
 * This converter service maps Prosolo social activities from a prosolo database
 * to DiscourseDB. The connection to the prosolo database is established by the
 * calling class, which has to pass a database access object to the methods in
 * this service class. Mapping methhods are executed transactionally.
 * 
 * @author Oliver Ferschke
 */
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class ProsoloConverterService {

	private static final Logger logger = LogManager.getLogger(ProsoloConverterService.class);
	
	@Autowired private DiscourseService discourseService;	
	@Autowired private UserService userService;
	@Autowired private DataSourceService dataSourceService;
	@Autowired private ContentService contentService;
	@Autowired private ContributionService contributionService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private DiscourseRelationService discourseRelationService;
	
	/**
	 * Maps social activities of the given type and the given action to DiscourseDB.
	 * 
	 * @param dtype the type of the SocialActivity
	 * @param action the create/add action
	 * @throws SQLException
	 */
	public void mapSocialActivity(String dtype, String action, Long curSocialActivityId, ProsoloDB prosolo, String discourseName, String dataSetName) throws SQLException{
			//We assume here that a single ProSolo database refers to a single course (i.e. a single Discourse)
			//The course details are passed on as a parameter to this converter and are not read from the prosolo database
			Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
	
			//check if the current social activity has already been imported at any point in time. if so, skip and proceed with the next
			if(dataSourceService.dataSourceExists(curSocialActivityId+"","social_activity.id",dataSetName)){
				logger.warn("Social activity with id "+curSocialActivityId+" ("+dtype+", "+action+") already in database. Skipping...");
				return;	
			}
			
			//get data from prosolo database
			//the social activity is the anchor point for the mapping
			SocialActivity curSocialActivity = prosolo.getSocialActivity(curSocialActivityId).get();
			
			// each social activity translates to a separate DiscoursePart			
			DiscoursePart postSocialActivityContainer = discoursePartService.createOrGetTypedDiscoursePart(discourse, lookUpDiscoursePartType(dtype));		

			// User information
			User curUser = null;
			if(dtype.equals("TwitterPostSocialActivity")){
				//We have no prosolo user information, but twitter user account information
				//The source id will therefore link to the social activity and not to a user entry
				curUser=userService.createOrGetUser(discourse, curSocialActivity.getNickname(), curSocialActivityId+"", "social_activity.id", DataSourceTypes.PROSOLO, dataSetName);
				curUser.setRealname(curSocialActivity.getName());				
			}else{				
				Optional<ProsoloUser> existingProsoloUser = prosolo.getProsoloUser(curSocialActivity.getMaker());
				if(existingProsoloUser.isPresent()){
					curUser = createUpdateOrGetUser(existingProsoloUser.get(),prosolo, discourseName,dataSetName,DataSourceTypes.PROSOLO);
					//curUser might end up being null for some PostSocialActivities of the TwitterPost type that don't have user info				
				}
			}			
			
			/*
			 * All contributions are related to a social activity 
			 * but they might also have to link back to either 
			 * a post or a node (depending on their type. 
			 * We now determine which one of the two it is.
			 */
			String typeSpecificSourceId = null;
			String typeSpecificSourceDescription = null;
			String typeSpecificDType=dtype;
			if(dtype.equals("NodeSocialActivity")){
				Optional<ProsoloNode> existingNode = prosolo.getProsoloNode(curSocialActivity.getNode_object());				
				if(existingNode.isPresent()){
					ProsoloNode curNode=existingNode.get();
					typeSpecificSourceId= curNode.getId()+"";
					typeSpecificSourceDescription= "node.id";
					typeSpecificDType=curNode.getDtype();
				}
			}else if(dtype.equals("PostSocialActivity")){
				Optional<ProsoloPost> existingProsoloPost = prosolo.getProsoloPost(curSocialActivity.getPost_object());
				if(existingProsoloPost.isPresent()){
					ProsoloPost curProsoloPost = existingProsoloPost.get();
					typeSpecificSourceId= curProsoloPost.getId()+"";
					typeSpecificSourceDescription= "post.id";
					typeSpecificDType=curProsoloPost.getDtype();
				}
			}						
			
			//Create DiscourseDB content and contribution entity and add the social activity as a source.
			//In case the contribution is also associated with a node or post, add this as an additional source
			Content curContent = contentService.createContent();
			if(curUser!=null){curContent.setAuthor(curUser);} //might be null for some Tweets
			curContent.setStartTime(curSocialActivity.getCreated());
			curContent.setText(curSocialActivity.getText());			
			dataSourceService.addSource(curContent, new DataSourceInstance(curSocialActivityId+"","social_activity.id",DataSourceTypes.PROSOLO, dataSetName));
			
			Contribution curContrib = contributionService.createTypedContribution(lookUpContributionType(typeSpecificDType));
			curContrib.setCurrentRevision(curContent);
			curContrib.setFirstRevision(curContent);
			curContrib.setStartTime(curSocialActivity.getCreated());
			curContrib.setUpvotes(curSocialActivity.getLike_count());			
			curContrib.setDownvotes(curSocialActivity.getDislike_count());			
			dataSourceService.addSource(curContrib, new DataSourceInstance(curSocialActivityId+"","social_activity.id",DataSourceTypes.PROSOLO,dataSetName));

			//add the type specific source types for nodes and posts mentioned above
			if(typeSpecificSourceId!=null){
				dataSourceService.addSource(curContent, new DataSourceInstance(typeSpecificSourceId, typeSpecificSourceDescription, DataSourceTypes.PROSOLO, dataSetName));				
				dataSourceService.addSource(curContrib, new DataSourceInstance(typeSpecificSourceId, typeSpecificSourceDescription, DataSourceTypes.PROSOLO, dataSetName));
			}
			
			//add contribution to DiscoursePart
			discoursePartService.addContributionToDiscoursePart(curContrib, postSocialActivityContainer);			
			
			
			/*
			 * Some social_activities don't just create a contribution, but they relate to 
			 * existing contributions. These relations are created below.
			 */
			
			//if the action is AddNote, the the previously created contribution is a Note related to an existing Node.
			//We now establish this relation
			if(action.equalsIgnoreCase("AddNote")){
				Optional<ProsoloNode> existingNode = prosolo.getProsoloNode(curSocialActivity.getGoal_target());				
				if(existingNode.isPresent()){
					ProsoloNode node = existingNode.get();					
					//check if a contribution for the given node exists
					Optional<Contribution> nodeContrib = contributionService.findOneByDataSource(node.getId()+"", "node.id", dataSetName);					
					if(nodeContrib.isPresent()){
						discourseRelationService.createDiscourseRelation(nodeContrib.get(), curContrib, DiscourseRelationTypes.COMMENT);
					}
				}				
			}
			
			//Establish relations between NodeComment and the node that it comments on
			if(dtype.equals("NodeComment")&&action.equalsIgnoreCase("Comment")){
				Optional<ProsoloNode> existingParenteNode = prosolo.getProsoloNode(curSocialActivity.getNode());				
				if(existingParenteNode.isPresent()){
					ProsoloNode node = existingParenteNode.get();					
					//check if a contribution for the given node exists
					Optional<Contribution> nodeContrib = contributionService.findOneByDataSource(node.getId()+"", "node.id", dataSetName);					
					if(nodeContrib.isPresent()){
						discourseRelationService.createDiscourseRelation(nodeContrib.get(), curContrib, DiscourseRelationTypes.COMMENT);
					}
				}				
			}

			//Establish relations between SocialActivityComment and the social activity that it comments on
			if(dtype.equals("SocialActivityComment")&&action.equalsIgnoreCase("Comment")){
				Optional<SocialActivity> existingSocialActivity = prosolo.getSocialActivity(curSocialActivity.getSocial_activity());				
				if(existingSocialActivity.isPresent()){
					SocialActivity parentActivity = existingSocialActivity.get();					
					//check if a contribution for the given social activity exists
					Optional<Contribution> parentContrib = contributionService.findOneByDataSource(parentActivity.getId()+"", "social_activity.id", dataSetName);					
					if(parentContrib.isPresent()){
						discourseRelationService.createDiscourseRelation(parentContrib.get(), curContrib, DiscourseRelationTypes.COMMENT);
					}
				}				
			}

			//get the entity was a "reshare" of another post
			if(action.equalsIgnoreCase("PostShare")){
				Optional<ProsoloPost> existingPost = prosolo.getProsoloPost(curSocialActivity.getPost_object());				
				if(existingPost.isPresent()){
					ProsoloPost sharingPost = existingPost.get();					
					if(sharingPost.getReshare_of()!=null){
						Optional<ProsoloPost> existingSharedPost = prosolo.getProsoloPost(sharingPost.getReshare_of());
						if(existingSharedPost.isPresent()){
							ProsoloPost sharedPost = existingSharedPost.get();
							//look up the contribution for the shared entity in DiscourseDB
							Optional<Contribution> sharedContribution = contributionService.findOneByDataSource(sharedPost.getId()+"", "post.id",dataSetName);
							if(sharedContribution.isPresent()){					
								//we represent the sharing activity as a relation between the sharing user and the shared contribution
								userService.createContributionInteraction(curUser, sharedContribution.get(), ContributionInteractionTypes.SHARE);					
								//we create a DiscourseRelation between the sharing and the shared contribution
								discourseRelationService.createDiscourseRelation(sharedContribution.get(), curContrib, DiscourseRelationTypes.RESHARE);
							}										
						}
					}
				}
			}			
		
	}

	
	/**
	 * Creates follow relationships from Prosolo followed_entities.
	 * 
	 * @param dtype the type of the followed_entity, i.e. "FollowedResourceEntity" or "FollowedUserEntity"
	 * @throws SQLException
	 */
	public void mapFollowedEntity(String dtype, Long curFollowedEntityId, ProsoloDB prosolo, String discourseName, String dataSetName) throws SQLException{	
			//check if the current followed_entity has already been imported at any point in time. if so, skip and proceed with the next
			if(dataSourceService.dataSourceExists(curFollowedEntityId+"","social_activity.id",dataSetName)){
				logger.warn("Followed entity with id "+curFollowedEntityId+" ("+dtype+") already in database. Skipping...");
				return;	
			}
			
			ProsoloFollowedEntity curFollowedEntity = prosolo.getProsoloFollowedEntity(curFollowedEntityId).get();
			User followingUser = createUpdateOrGetUser(prosolo.getProsoloUser(curFollowedEntity.getUser()).get(),prosolo, discourseName,dataSetName,DataSourceTypes.PROSOLO);			
			
			if(dtype.equals("FollowedResourceEntity")){
				//following a node				
				Optional<ProsoloNode> existingNode = prosolo.getProsoloNode(curFollowedEntity.getFollowed_node());				
				if(existingNode.isPresent()){
					ProsoloNode node = existingNode.get();					
					//check if a contribution for the given node exists
					Optional<Contribution> nodeContrib = contributionService.findOneByDataSource(node.getId()+"", "node.id", dataSetName);					
					if(nodeContrib.isPresent()){
						//create interaction that indicates the following relation
						ContributionInteraction followNode = userService.createContributionInteraction(followingUser, nodeContrib.get(), ContributionInteractionTypes.FOLLOW);
						followNode.setStartTime(curFollowedEntity.getStarted_following());
					}
				}							
			}else if(dtype.equals("FollowedUserEntity")){
				//following a user
				Optional<ProsoloUser> existingProsoloUser = prosolo.getProsoloUser(curFollowedEntity.getFollowed_user());				
				if(existingProsoloUser.isPresent()){
					User followedUser = createUpdateOrGetUser(existingProsoloUser.get(),prosolo, discourseName,dataSetName,DataSourceTypes.PROSOLO);								
					UserRelation followUser = userService.createUserRelation(followingUser, followedUser, UserRelationTypes.FOLLOW);
					followUser.setStartTime(curFollowedEntity.getStarted_following());
				}				
			}else{
				logger.warn("Unsupported dtype: "+dtype);
				return;
			}
	}
			
			
			
	
	/**
	 * Creates a new DiscourseDB user based on the information in the ProsoloUser object if it doesn't exist
	 * or updates the contents of an existing DiscourseDB user.
	 * If a user has previously been updated with a prosoloUser, the User is simply returned.
	 * 
	 * Thus, the method can also been used to retrieve DiscourseDB Users using ProsoloUser objects.
	 * 
	 * @param prosoloUser the prosolo user to add to discoursedb 
	 * @return the DiscourseDB user based on or updated with the prosolo user
	 * @throws SQLException In case of a database access error
	 */
	public User createUpdateOrGetUser(ProsoloUser prosoloUser, ProsoloDB prosolo, String discourseName, String dataSetName, DataSourceTypes dataSourceType) throws SQLException{
		User curUser = null; 
		if(prosoloUser==null){
			logger.error("Could not find user information for prosolo user in prosolo database");
		}else{				
			//Check if we previously ran the addOrUpdate already. If so, just return the User
			Optional<User> existingUser = userService.findUserByDiscourseAndSourceIdAndDataSet(discourseService.createOrGetDiscourse(discourseName), prosoloUser.getId()+"", dataSetName);
			if(existingUser.isPresent()){
				return existingUser.get();						
			}
			
			//CHECK IF USER WITH SAME edX username exists in the current Discourse context
			Optional<String> edXUserName = prosolo.mapProsoloUserIdToedXUsername(prosoloUser.getId());			
			if(edXUserName.isPresent()){
				curUser=userService.createOrGetUser(discourseService.createOrGetDiscourse(discourseName), edXUserName.get());
				dataSourceService.addSource(curUser, new DataSourceInstance(prosoloUser.getId()+"","user.id",dataSourceType,dataSetName));
			}else{
				curUser=userService.createOrGetUser(discourseService.createOrGetDiscourse(discourseName),"", prosoloUser.getId()+"","user.id",dataSourceType,dataSetName);
			}

			//update the real name of the user if necessary
			curUser=userService.setRealname(curUser, prosoloUser.getName(), prosoloUser.getLastname());
			
			//Update email address if not set in db (TODO allow multiple email addresses?)
			if(curUser.getEmail()==null||curUser.getEmail().isEmpty()){
				Optional<String> prosoloMail = prosolo.getEmailForProsoloUser(prosoloUser.getId());
				if(prosoloMail.isPresent()){
					curUser.setEmail(prosoloMail.get());
				}
			}
			
			//Update location if not yet set in db
			if(curUser.getLocation()==null||curUser.getLocation().isEmpty()){
				curUser.setLocation(prosoloUser.getLocation_name());					
			}

			//add new data source
			dataSourceService.addSource(curUser, new DataSourceInstance(prosoloUser.getId()+"","user.id",dataSourceType,dataSetName));
		}
		return curUser;		
	}
	
	
	/**
	 * Maps SocialActivity dtype to an appropriate DiscourseDB ContributionType
	 * 
	 * @param dtype the SocialActivity type
	 * @return an appropriate ContributionType for the given SocialActivity entity
	 */
	private ContributionTypes lookUpContributionType(String type){
		switch(type){
			case "GoalNoteSocialActivity": return ContributionTypes.GOAL_NOTE; 
			case "LearningGoal": return ContributionTypes.LEARNING_GOAL; 
			case "TargetLearningGoal": return ContributionTypes.TARGET_LEARNING_GOAL;  
			case "Competence": return ContributionTypes.COMPETENCE; 
			case "TargetCompetence": return ContributionTypes.TARGET_COMPETENCE;  
			case "ResourceActivity": return ContributionTypes.RESOURCE_ACTIVITY;
			case "TargetActivity": return ContributionTypes.TARGET_ACTIVITY; 
			case "UploadAssignmentActivity": return ContributionTypes.UPLOAD_ASSIGNMENT_ACTIVITY;
			case "NodeComment": return ContributionTypes.NODE_COMMENT; 
			case "Post": return ContributionTypes.POST; 
			case "GoalNote": return ContributionTypes.GOAL_NOTE; 
			case "TwitterPost": return ContributionTypes.TWEET; 
			case "SocialActivityComment": return ContributionTypes.SOCIAL_ACTIVITY_COMMENT; 
			default: throw new IllegalArgumentException("No ContributionType mapping for dtype "+type); 
		}
	}

	/**
	 * Maps SocialActivity dtype to an appropriate DiscourseDB DiscoursePartType.
	 * 
	 * Currently, all types map to PROSOLO_SOCIAL_ACTIVITY
	 * 
	 * @param dtype the SocialActivity type
	 * @return an appropriate DiscoursePartType for the given SocialActivity entity
	 */
	private DiscoursePartTypes lookUpDiscoursePartType(String dtype){
		switch(dtype){
			case "GoalNoteSocialActivity": return DiscoursePartTypes.PROSOLO_SOCIAL_ACTIVITY;
			case "NodeSocialActivity": return DiscoursePartTypes.PROSOLO_SOCIAL_ACTIVITY;
			case "NodeComment": return DiscoursePartTypes.PROSOLO_SOCIAL_ACTIVITY;
			case "PostSocialActivity": return DiscoursePartTypes.PROSOLO_SOCIAL_ACTIVITY;			
			case "TwitterPostSocialActivity": return DiscoursePartTypes.PROSOLO_SOCIAL_ACTIVITY;			
			case "SocialActivityComment": return DiscoursePartTypes.PROSOLO_SOCIAL_ACTIVITY;			
			default: throw new IllegalArgumentException("No DiscoursePartType mapping for dtype "+dtype);
		}
	}

}