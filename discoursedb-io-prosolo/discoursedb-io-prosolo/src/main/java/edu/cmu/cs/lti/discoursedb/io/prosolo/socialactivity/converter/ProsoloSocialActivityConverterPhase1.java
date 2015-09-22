package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.SQL;
import org.jooq.lambda.Unchecked;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloPost;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloUser;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.SocialActivity;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.util.TableConstants;

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
	
	/*
	 * Discourse parameters that this database represents.  
	 */

	private String discourseName;

	
	/*
	 * Credentials for ProSolo database connection 
	 */

	private Connection con = null;
	private String host;
	private String db;
	private String user;
	private String pwd;

	/*
	 * Entity-Repositories for DiscourseDB connection.
	 */

	@Autowired
	private DiscourseService discourseService;
	
	@Autowired
	private ContentRepository contentRepository;
	
	@Autowired
	private ContributionService contributionService;

	@Autowired
	private DiscoursePartService discoursePartService;
	
	@Override
	public void run(String... args) throws Exception {

		if (args.length != 5) {
			logger.error("Missing database credentials <DiscourseName> <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>");
			System.exit(1);
		}

		this.discourseName=args[0];
		this.host = args[1];
		this.db = args[2];
		this.user = args[3];
		this.pwd = args[4];

		logger.info("Start mapping to DiscourseDB...");
		try {
			map();
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			closeConnection();

		}
		logger.info("...mapping complete");

		///////////////////////////////////////////////
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
		//TODO mapNodeSocialAcitvities
		//TODO apXSocialActivities
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
		for (Long l : getIdsForDtypeAndAction("PostSocialActivity", "Post")) {
			SocialActivity curPostActivity = getSocialActivity(l).get(); 
			ProsoloPost curProsoloPost = getProsoloPost(curPostActivity.getPost_object()).get();
			
			
			// ---------- DiscourseParts -----------
			logger.trace("Process DiscourseParts");			
			// Prosolo has different types of social activities that
			// represent different spaces in the platform. They are translated to different DiscourseParts
			
			DiscoursePart postSocialActivityContainer = discoursePartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.PROSOLO_POST_SOCIAL_ACTIVITY);
			postSocialActivityContainer.setName(this.discourseName+"_POST"); //no specific title here, create  generic title 			
		
			// ---------- Init User -----------
			logger.trace("Process User entity");
			ProsoloUser curProsoloUser = getProsoloUser(curPostActivity.getMaker()).get();
			User curUser = null; 
			if(curProsoloUser==null){
				logger.error("Could not find user information for creator of social activity "+curPostActivity.getId()+" in prosolo db.");
			}else{
				//TODO try to identify whether user exists already in the context of this discourse
			}
			
			logger.trace("Process contribution and content");
			// ---------- Init Content -----------
			Content curContent = new Content();
			curContent.setAuthor(curUser);
			curContent.setCreationTime(curProsoloPost.getCreated());
			curContent.setText(curProsoloPost.getContent());
			curContent.setSourceId(curProsoloPost.getId()+""); //content id should refer to the Post object
			curContent = contentRepository.save(curContent);

			
			// ---------- Init Contribution -----------
			Contribution curContrib = contributionService.createTypedContribution(ContributionTypes.POST);
			curContrib.setCurrentRevision(curContent);
			curContrib.setFirstRevision(curContent);
			curContrib.setStartTime(curProsoloPost.getCreated());
			curContrib.setSourceId(curProsoloPost.getId()+"");
			curContrib.setUpvotes(curPostActivity.getLike_count());			
			curContrib=contributionService.save(curContrib);
		}
		
		
		//Process the PostShare subtype and create contribution interactions
		for (Long l : getIdsForDtypeAndAction("PostSocialActivity", "PostShare")) {
			SocialActivity curShareActivity = getSocialActivity(l).get();
			ProsoloPost pPost = getProsoloPost(curShareActivity.getPost_object()).get();
//			SocialActivity pPost.getReshare_of()
			//user_target indicates the person it was shared with
		}		
		//TODO DiscourseRelation: Reply		
		//TODO DiscourseRelation: Descendant		
		
	}
	
	/**
	 * Maps posts (dtype=TwitterPost) to DiscourseDB 
	 * 
	 * @throws SQLException In case of a database access error
	 */
	private void mapTwitterPosts() throws SQLException{
		
	}

	
	/**
	 * Returns all ids for social activities of the given dtype. This idlist can
	 * then be used to query one social activity at a time in a separate
	 * PreparedStatement. This might be preferred to streaming a whole resultset
	 * (even though this would have a higher performance), because no other
	 * statements can be executed before the (streaming) query isn't closed.
	 * 
	 * @param dtype
	 *            the dtype for the social activity
	 * @return a list of ids for social activities of the provided dtype
	 * @throws SQLException
	 */
	@SuppressWarnings("unused")
	private List<Long> getIdsForDtype(String dtype) throws SQLException {
		if(dtype==null){
			return new ArrayList<Long>();
		}
		List<Long> idList = null;

		String sql = "SELECT id from "+TableConstants.SOCIALACTIVITY+" where dtype=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, dtype);
			idList = SQL.seq(stmt, Unchecked.function(rs -> rs.getLong("id"))).collect(Collectors.toList());
		}
		return idList;
	}
	
	/**
	 * Returns all ids for social activities of the given dtype. This idlist can
	 * then be used to query one social activity at a time in a separate
	 * PreparedStatement. This might be preferred to streaming a whole resultset
	 * (even though this would have a higher performance), because no other
	 * statements can be executed before the (streaming) query isn't closed.
	 * 
	 * @param dtype
	 *            the dtype for the social activity
	 * @param action
	 *            the action for this activity (e.g. TwitterPost, Post)
	 * @return a list of ids for social activities of the provided dtype
	 * @throws SQLException
	 */
	private List<Long> getIdsForDtypeAndAction(String dtype, String action) throws SQLException {
		if(dtype==null||action==null){
			return new ArrayList<Long>();
		}
		List<Long> idList = null;
		String sql = "SELECT id from "+TableConstants.SOCIALACTIVITY+" where dtype=? and action=?";
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			stmt.setString(1, dtype);
			stmt.setString(2, action);
			idList = SQL.seq(stmt, Unchecked.function(rs -> rs.getLong("id"))).collect(Collectors.toList());
		}
		return idList;
	}

	/**
	 * Returns a single SocialActivity object
	 * 
	 * @param id
	 *            the id of the social activity object
	 * @return a list of ids for social activities of the provided dtype
	 * @throws SQLException
	 */
	private Optional<SocialActivity> getSocialActivity(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		SocialActivity activity = null;
		try (Connection c = getConnection()) {
			String sql = "SELECT * from "+TableConstants.SOCIALACTIVITY+" where id=?";
			try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				activity = SQL.seq(stmt, Unchecked.function(rs -> new SocialActivity(
						rs.getString("dtype"),
						rs.getLong("id"),
						rs.getTimestamp("created"),
						rs.getString("deleted"),
						rs.getString("dc_description"),
						rs.getString("title"),
						rs.getString("action"),
						rs.getLong("bookmark_count"),
						rs.getString("comments_disabled"),
						rs.getInt("dislike_count"),
						rs.getTimestamp("last_action"),
						rs.getInt("like_count"),
						rs.getInt("share_count"),
						rs.getString("text"),
						rs.getString("visibility"),
						rs.getString("avatar_url"),
						rs.getString("name"),
						rs.getString("nickname"),
						rs.getString("post_link"),
						rs.getString("profile_url"),
						rs.getInt("service_type"),
						rs.getInt("user_type"),
						rs.getLong("actor"),
						rs.getLong("maker"),
						rs.getLong("reason"),
						rs.getLong("rich_content"),
						rs.getLong("goal_target"),
						rs.getLong("post_object"),
						rs.getLong("user_target"),
						rs.getLong("node_object"),
						rs.getLong("user_object"),
						rs.getLong("node_target"),
						rs.getLong("node"),
						rs.getLong("social_activity"),
						rs.getLong("enrollment_object"),
						rs.getLong("course_object"),
						rs.getLong("course_enrollment_object")					
		            ))).findAny().get();
			}
		}
		return Optional.of(activity);
	}

	/**
	 * Returns a single SocialActivity object
	 * 
	 * @param id
	 *            the id of the post object
	 * @return the prosolo post object
	 * @throws SQLException
	 */
	private Optional<ProsoloPost> getProsoloPost(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		ProsoloPost post = null;
		try (Connection c = getConnection()) {
			String sql = "SELECT * from "+TableConstants.POST+" where id=?";
			try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				post = SQL.seq(stmt, Unchecked.function(rs -> new ProsoloPost(
						rs.getLong("id"),
						rs.getTimestamp("created"),
						rs.getBoolean("deleted"),
						rs.getString("dc_description"),
						rs.getString("title"),
						rs.getString("content"),
						rs.getString("link"),
						rs.getString("visibility"),
						rs.getBoolean("connect_with_status"),
						rs.getLong("maker"),
						rs.getLong("reshare_of"),
						rs.getLong("rich_content"),
						rs.getLong("goal"),
						rs.getString("post_link")
		            ))).findAny().get();
			}
		}
		return Optional.of(post);
	}
	
	/**
	 * Returns a single ProsoloUser object
	 * 
	 * @param id
	 *            the id of the prosolo user
	 * @return a list of ids for social activities of the provided dtype
	 * @throws SQLException
	 */
	private Optional<ProsoloUser> getProsoloUser(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		ProsoloUser pUser = null;
		try (Connection c = getConnection()) {
			String sql = "SELECT * from "+TableConstants.USER+" where id=?";
			try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				pUser = SQL.seq(stmt, Unchecked.function(rs -> new ProsoloUser(
						rs.getLong("id"),
						rs.getTimestamp("created"),
						rs.getString("deleted"),
						rs.getString("dc_description"),
						rs.getString("title"),
						rs.getString("avatar_url"),
						rs.getString("lastname"),
						rs.getDouble("latitude"),
						rs.getString("location_name"),
						rs.getDouble("longitude"),
						rs.getString("name"),
						rs.getString("password"),
						rs.getInt("password_length"),
						rs.getString("position"),
						rs.getString("profile_url"),
						rs.getString("system"),
						rs.getString("user_type"),
						rs.getString("email"),
						rs.getString("user_user_organization")
		            ))).findAny().get();
			}
		}
		return Optional.of(pUser);
	}
	
	/**
	 * Returns the edX username for a given ProSolo user id.
	 * 
	 * @param id the prosolo user id
	 * @return an Optional containing the edX username of the prosolo user with the provided id, if a mapping exists
	 * @throws SQLException
	 */
	private Optional<String> mapProsoloUserIdToedXUsername(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		String edXid=null;
		try (Connection c = getConnection()) {
			String sql = "SELECT validated_id from "+TableConstants.OPENIDACCOUNT+" where id=?";
			try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				edXid = SQL.seq(stmt, Unchecked.function(rs -> rs.getString("validated_id")
		            )).findAny().get();
			}
		}		
		return Optional.of(edXid.substring(edXid.lastIndexOf("/")+1));				
	}
	
		
	private Connection getConnection() throws SQLException {
		if (con == null || con.isClosed()) {
			return DriverManager.getConnection("jdbc:mysql://" + this.host + ":3306/" + this.db, this.user, this.pwd);
		} else {
			return con;
		}
	}

	private void closeConnection() throws SQLException {
		if (con != null && !con.isClosed()) {
			con.close();
		}
	}
}