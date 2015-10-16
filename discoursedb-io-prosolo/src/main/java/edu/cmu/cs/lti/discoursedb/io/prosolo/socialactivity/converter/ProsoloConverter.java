package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
import edu.cmu.cs.lti.discoursedb.core.service.user.UserInteractionService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionInteractionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.io.ProsoloDB;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloNode;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloPost;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloUser;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.SocialActivity;

/**
 * This converter loads data from a prosolo database and maps it to DiscourseDB.
 * The DiscourseDB configuration is defined in the dicoursedb-model project and
 * Spring/Hibernate are taking care of connections.
 * 
 * The connection to the prosolo database is more lightweight and uses a JDBC
 * connection. The configuration parameters for this connection are passed to
 * the converter as launch parameters in the following order
 * 
 * <DiscourseName> <DataSetName> <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Transactional
@Order(1)
public class ProsoloConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(ProsoloConverter.class);
	
	/**
	 * List of all social activity "actions" that result in the creation of a DiscourseDB Contribution
	 */
	private final List<String> CONTRIB_CREATING_ACTIONS = Arrays.asList(new String[]{"Create","Post","AddNote","Comment","PostShare","TwitterPost"});
	
	private String discourseName;
	private DataSourceTypes dataSourceType;
	private String dataSetName;
	
	private ProsoloDB prosolo = null;

	@Autowired private DiscourseService discourseService;	
	@Autowired private UserService userService;
	@Autowired private DataSourceService dataSourceService;
	@Autowired private ContentService contentService;
	@Autowired private ContributionService contributionService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private DiscourseRelationService discourseRelationService;
	@Autowired private UserInteractionService userInteractionService;
	
	@Override 
	public void run(String... args) throws Exception {

		if (args.length != 6) {
			logger.error("Incorrect number of parameters. USAGE: <DiscourseName> <DataSetName> <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>");
			System.exit(1);
		}
		
		//Parse command line parameters		
		this.discourseName=args[0];			
		this.dataSourceType = DataSourceTypes.PROSOLO;
		this.dataSetName=args[1];

		prosolo = new ProsoloDB(args[2],args[3],args[4],args[5]);

		logger.info("Start mapping to DiscourseDB...");
		try {
			map();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			prosolo.closeConnection();
		}
		logger.info("...mapping complete");
	}

	/**
	 * Calls the mapping routines for the different social activity types in ProSolo.
	 * If the provided dataset has previously been imported already, the process will not proceed importing anything to avoid duplicates.
	 *  
	 * @throws SQLException In case of a database access error
	 */
	private void map() throws SQLException {
		if(dataSourceService.dataSourceExists(dataSetName)){
			logger.warn("Dataset "+dataSetName+" has previously already been imported. Terminating...");			
			return;
		}
		//start with all the creates
		mapSocialActivities("NodeSocialActivity", "Create");
		mapSocialActivities("PostSocialActivity", "Post");
		
		//then proceed with the sharing and commenting
		mapSocialActivities("GoalNoteSocialActivity", "AddNote"); 
		mapSocialActivities("NodeComment", "Comment"); 
		mapSocialActivities("PostSocialActivity","PostShare");	
	}
	

	
	/**
	 * Maps social activities of the given type and the given action to DiscourseDB.
	 * 
	 * @param dtype the type of the SocialActivity
	 * @param action the create/add action
	 * @throws SQLException
	 */
	private void mapSocialActivities(String dtype, String action) throws SQLException{
		if(!CONTRIB_CREATING_ACTIONS.contains(action)){
			logger.warn("Action "+action+" (SocialActivity type "+dtype+") does not create DiscourseDB contributions and is thus not covered by this method.");
			return;
		}
		
		
		//We assume here that a single ProSolo database refers to a single course (i.e. a single Discourse)
		//The course details are passed on as a parameter to this converter and are not read from the prosolo database
		Discourse discourse = discourseService.createOrGetDiscourse(this.discourseName);
		
		for (Long l : prosolo.getIdsForDtypeAndAction(dtype, action)) {
			logger.trace("Processing "+dtype+" ("+action+") id:"+l);			

			//get data from prosolo database
			//the social activity is the anchor point for the mapping
			SocialActivity curSocialActivity = prosolo.getSocialActivity(l).get(); 
			
			// each social activity translates to a separate DiscoursePart			
			DiscoursePart postSocialActivityContainer = discoursePartService.createOrGetTypedDiscoursePart(discourse, lookUpDiscoursePartType(dtype));		

			// User information
			ProsoloUser curProsoloUser = prosolo.getProsoloUser(curSocialActivity.getMaker()).get();
			User curUser = addOrUpdateUser(curProsoloUser);
			
			
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
			curContent.setAuthor(curUser);
			curContent.setStartTime(curSocialActivity.getCreated());
			curContent.setText(curSocialActivity.getText());			
			dataSourceService.addSource(curContent, new DataSourceInstance(curSocialActivity.getId()+"","social_activity.id",dataSourceType, dataSetName));
			
			Contribution curContrib = contributionService.createTypedContribution(lookUpContributionType(typeSpecificDType));
			curContrib.setCurrentRevision(curContent);
			curContrib.setFirstRevision(curContent);
			curContrib.setStartTime(curSocialActivity.getCreated());
			curContrib.setUpvotes(curSocialActivity.getLike_count());			
			curContrib.setDownvotes(curSocialActivity.getDislike_count());			
			dataSourceService.addSource(curContrib, new DataSourceInstance(curSocialActivity.getId()+"","social_activity.id",dataSourceType,dataSetName));

			if(typeSpecificSourceId!=null){
				dataSourceService.addSource(curContent, new DataSourceInstance(typeSpecificSourceId, typeSpecificSourceDescription, dataSourceType, dataSetName));				
				dataSourceService.addSource(curContrib, new DataSourceInstance(typeSpecificSourceId, typeSpecificSourceDescription, dataSourceType, dataSetName));
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
			
			//if the action is Comment, the the previously created contribution is a Comment related to an existing Node.
			//We now establish this relation
			if(action.equalsIgnoreCase("Comment")){
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
								userInteractionService.createTypedContributionIteraction(curUser, sharedContribution.get(), ContributionInteractionTypes.SHARE);					
								//we create a DiscourseRelation between the sharing and the shared contribution
								discourseRelationService.createDiscourseRelation(sharedContribution.get(), curContrib, DiscourseRelationTypes.RESHARE);
							}										
						}
					}
				}
			}			
		}
	}

	
	/**
	 * Creates a new DiscourseDB user based on the information in the ProsoloUser object if it doesn't exist
	 * or updates the contents of an existing DiscourseDB user.
	 * 
	 * @param prosoloUser the prosolo user to add to discoursedb 
	 * @return the DiscourseDB user based on or updated with the prosolo user
	 * @throws SQLException In case of a database access error
	 */
	private User addOrUpdateUser(ProsoloUser prosoloUser) throws SQLException{
		User curUser = null; 
		if(prosoloUser==null){
			logger.error("Could not find user information for prosolo user in prosolo database");
		}else{				
			//CHECK IF USER WITH SAME edX username exists in the current Discourse context
			Optional<String> edXUserName = prosolo.mapProsoloUserIdToedXUsername(prosoloUser.getId());
			if(edXUserName.isPresent()){
				curUser=userService.createOrGetUser(discourseService.createOrGetDiscourse(this.discourseName), edXUserName.get());
				dataSourceService.addSource(curUser, new DataSourceInstance(prosoloUser.getId()+"","user.id",dataSourceType,dataSetName));
			}else{
				curUser=userService.createOrGetUser(discourseService.createOrGetDiscourse(this.discourseName),"", prosoloUser.getId()+"","user.id",dataSourceType,dataSetName);
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
			default: throw new IllegalArgumentException("No ContributionType mapping for dtype "+type); 
		}
	}

	/**
	 * Maps SocialActivity dtype to an appropriate DiscourseDB DiscoursePartType
	 * 
	 * @param dtype the SocialActivity type
	 * @return an appropriate DiscoursePartType for the given SocialActivity entity
	 */
	private DiscoursePartTypes lookUpDiscoursePartType(String dtype){
		switch(dtype){
			case "GoalNoteSocialActivity": return DiscoursePartTypes.PROSOLO_GOAL_NOTE_SOCIAL_ACTIVITY;
			case "NodeSocialActivity": return DiscoursePartTypes.PROSOLO_NODE_SOCIAL_ACTIVITY;
			case "NodeComment": return DiscoursePartTypes.PROSOLO_NODE_SOCIAL_ACTIVITY;
			case "PostSocialActivity": return DiscoursePartTypes.PROSOLO_POST_SOCIAL_ACTIVITY;			
			default: throw new IllegalArgumentException("No DiscoursePartType mapping for dtype "+dtype);
		}
	}

}