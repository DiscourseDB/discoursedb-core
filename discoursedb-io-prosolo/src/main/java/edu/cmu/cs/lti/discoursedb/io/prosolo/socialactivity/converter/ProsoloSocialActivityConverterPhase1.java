package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import java.sql.SQLException;
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
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.io.ProsoloDB;
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
 * <DiscourseName> <DiscourseDescriptor><prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Transactional
@Order(1)
public class ProsoloSocialActivityConverterPhase1 implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(ProsoloSocialActivityConverterPhase1.class);
	
	private String discourseName;
	private DataSourceTypes dataSourceType;
	private String dataSetName;
	
	//Access to the prosolo database
	private ProsoloDB prosolo = null;

	@Autowired
	private DiscourseService discourseService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private DataSourceService dataSourceService;

	@Autowired
	private ContentService contentService;
	
	@Autowired
	private ContributionService contributionService;

	@Autowired
	private DiscoursePartService discoursePartService;
	
	@Override
	public void run(String... args) throws Exception {

		if (args.length != 7) {
			logger.error("Incorrect number of parameters. USAGE: <DiscourseName> <DataSourceType> <DataSetName> <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>");
			System.exit(1);
		}

		//Parse command line parameters		
		this.discourseName=args[0];
		try{
			this.dataSourceType = DataSourceTypes.valueOf(args[1]);
		}catch(Exception e){
			StringBuilder types = new StringBuilder();
			for(DataSourceTypes type : DataSourceTypes.values()){
				if(types.length()==0){types.append(",");}
				types.append(type.name());
			}
			logger.error("Invalid DataSourceType: "+args[1]+". Valid values: "+types.toString());
			logger.error("");
			System.exit(1);
		}
		this.dataSetName=args[2];

		prosolo = new ProsoloDB(args[3],args[4],args[5],args[6]);

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
	 * Calls the mapping routines for the different social actitiy types in ProSolo.
	 * Each social activity is handled by a separate method.
	 *  
	 * @throws SQLException In case of a database access error
	 */
	private void map() throws SQLException {
		mapPosts();
		mapTwitterPosts();
		mapNodeSocialActivities();
	}

	
	/**
	 * Maps posts (dtype=post) to DiscourseDB 
	 * 
	 * @throws SQLException In case of a database access error
	 */
	private void mapPosts() throws SQLException{
		
		//We assume here that a single ProSolo database refers to a single course.
		//The course details are passed on as a parameter to this converter and are not read from the prosolo database
		Discourse discourse = discourseService.createOrGetDiscourse(this.discourseName);
		
		//START WITH PostSocialActivity entities of the subtype Post
		for (Long l : prosolo.getIdsForDtypeAndAction("PostSocialActivity", "Post")) {
			SocialActivity curPostActivity = prosolo.getSocialActivity(l).get(); 
			ProsoloPost curProsoloPost = prosolo.getProsoloPost(curPostActivity.getPost_object()).get();			
			
			// ---------- DiscourseParts -----------
			logger.trace("Process DiscourseParts");			
			// Prosolo has different types of social activities that
			// represent different spaces in the platform. They are translated to different DiscourseParts			
			DiscoursePart postSocialActivityContainer = discoursePartService.createOrGetTypedDiscoursePart(discourse, this.discourseName+"_POST", DiscoursePartTypes.PROSOLO_POST_SOCIAL_ACTIVITY);		
			
			// ---------- Init User -----------
			logger.trace("Process User entity");
			ProsoloUser curProsoloUser = prosolo.getProsoloUser(curPostActivity.getMaker()).get();
			User curUser = null; 
			if(curProsoloUser==null){
				logger.error("Could not find user information for creator of social activity "+curPostActivity.getId()+" in prosolo db.");
			}else{				
				//CHECK IF USER WITH SAME edX username exists in the current Discourse context
				Optional<String> edXUserName = prosolo.mapProsoloUserIdToedXUsername(curProsoloUser.getId());
				if(edXUserName.isPresent()){
					curUser=userService.createOrGetUser(discourse, edXUserName.get());
					dataSourceService.addSource(curUser, new DataSourceInstance(curProsoloUser.getId()+"",dataSourceType,dataSetName));
				}else{
					curUser=userService.createOrGetUser(discourse,"", curProsoloUser.getId()+"",dataSourceType,dataSetName);
				}

				//update the real name of the user if necessary
				curUser=userService.setRealname(curUser, curProsoloUser.getName(), curProsoloUser.getLastname());
				
				//Update email address if not set in db
				//TODO allow multiple email addresses?
				if(curUser.getEmail()==null||curUser.getEmail().isEmpty()){
					Optional<String> prosoloMail = prosolo.getEmailForProsoloUser(curProsoloUser.getId());
					if(prosoloMail.isPresent()){
						curUser.setEmail(prosoloMail.get());
					}
				}				
			}
			
			
			logger.trace("Process contribution and content");
			// ---------- Init Content -----------
			Content curContent = contentService.createContent();
			curContent.setAuthor(curUser);
			curContent.setStartTime(curProsoloPost.getCreated());
			curContent.setText(curProsoloPost.getContent());			
			dataSourceService.addSource(curContent, new DataSourceInstance(curProsoloPost.getId()+"",dataSourceType,dataSetName));
			
			
			// ---------- Init Contribution -----------
			Contribution curContrib = contributionService.createTypedContribution(ContributionTypes.POST);
			curContrib.setCurrentRevision(curContent);
			curContrib.setFirstRevision(curContent);
			curContrib.setStartTime(curProsoloPost.getCreated());
			curContrib.setUpvotes(curPostActivity.getLike_count());			
			dataSourceService.addSource(curContrib, new DataSourceInstance(curProsoloPost.getId()+"",dataSourceType,dataSetName));
		
			//add contribution to DiscoursePart
			discoursePartService.addContributionToDiscoursePart(curContrib, postSocialActivityContainer);		
		}
		
		
//		//Process the PostShare subtype and create contribution interactions
//		for (Long l : getIdsForDtypeAndAction("PostSocialActivity", "PostShare")) {
//			SocialActivity curShareActivity = getSocialActivity(l).get();
//			ProsoloPost pPost = getProsoloPost(curShareActivity.getPost_object()).get();
////			SocialActivity pPost.getReshare_of()
//			//user_target indicates the person it was shared with
//		}		
//		//TODO DiscourseRelation: Reply		
//		//TODO DiscourseRelation: Descendant		
		
	}
	
	/**
	 * Maps posts (dtype=TwitterPost) to DiscourseDB 
	 * 
	 * @throws SQLException In case of a database access error
	 */
	private void mapTwitterPosts() throws SQLException{
		
	}
	/**
	 * Maps posts (dtype=TwitterPost) to DiscourseDB 
	 * 
	 * @throws SQLException In case of a database access error
	 */
	private void mapNodeSocialActivities() throws SQLException{
		
	}
		

}