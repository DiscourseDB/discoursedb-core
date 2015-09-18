package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseToDiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
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

	
	/*
	 * Discourse parameters that this database represents.  
	 */

	private String discourseName;
	private String discourseDescriptor;

	
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
	private DiscourseRepository discourseRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContentRepository contentRepository;
	@Autowired
	private ContributionRepository contributionRepository;
	@Autowired
	private DiscoursePartRepository discoursePartRepository;
	@Autowired
	private ContributionTypeRepository contributionTypeRepository;
	@Autowired
	private DiscourseToDiscoursePartRepository discourseToDiscoursePartRepository;
	@Autowired
	private DiscoursePartTypeRepository discoursePartTypeRepository;
	@Autowired
	private DiscoursePartContributionRepository discoursePartContributionRepository;
	
	@Override
	public void run(String... args) throws Exception {

		if (args.length != 6) {
			logger.error("Missing database credentials <DiscourseName> <DiscourseDescriptor> <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>");
			System.exit(1);
		}

		this.discourseName=args[0];
		this.discourseDescriptor=args[1];
		this.host = args[2];
		this.db = args[3];
		this.user = args[4];
		this.pwd = args[5];

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
	 * Maps prosolo data to DiscourseDB.
	 *  
	 * @throws SQLException
	 */
	private void map() throws SQLException {
		mapPostSocialActivityPosts();
		//TODO mapNodeSocialAcitvities
		//TODO apXSocialActivities
	}

	
	/**
	 * Maps social activities of the type "PostSocialActivity" and the 
	 * subtype Post to DiscourseDB
	 * 
	 * @throws SQLException
	 */
	private void mapPostSocialActivityPosts() throws SQLException{
		List<Long> ids = getIdsForDtypeAndAction("PostSocialActivity", "Post");
		
		//We assume here that a single ProSolo database refers to a single course.
		//The course details are passed on as a parameter to this converter and are not read from the prosolo database
		Discourse discourse = createOrGetDiscourse(this.discourseName, this.discourseDescriptor);
		
		for (Long l : ids) {
			SocialActivity curActivity = getSocialActivity(l);
			ProsoloUser curProsoloUser = getProsoloUser(curActivity.getMaker());
			
			//Get the "course credentials" this activity is connected with from the prosolo database
			//This will be mapped to a DiscoursePart in DiscourseDB 
			//TODO implement
			
			//Create the contribution and contribution content for the activity
			// ---------- Init User -----------
			logger.trace("Init User entity");
			
						
		}
	}
	
	private Discourse createOrGetDiscourse(String name, String descriptor){
		Optional<Discourse> curOptDiscourse = discourseRepository.findOneByNameAndDescriptor(name, descriptor);
		Discourse curDiscourse;
		if (curOptDiscourse.isPresent()) {
			curDiscourse=curOptDiscourse.get();
		}else{
			curDiscourse = new Discourse(name, descriptor);
			curDiscourse=discourseRepository.save(curDiscourse);
		}
		return curDiscourse;
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
	private List<Long> getIdsForDtype(String dtype) throws SQLException {
		List<Long> idList = null;

		String sql = "SELECT id from social_activity where dtype=?";
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
		List<Long> idList = null;

		String sql = "SELECT id from social_activity where dtype=? and action=?";
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
	private SocialActivity getSocialActivity(Long id) throws SQLException {
		SocialActivity activity = null;
		try (Connection c = getConnection()) {
			String sql = "SELECT * from social_activity where id=?";
			try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				activity = SQL.seq(stmt, Unchecked.function(rs -> new SocialActivity(
						rs.getString("dtype"),
						rs.getLong("id"),
						rs.getString("created"),
						rs.getString("deleted"),
						rs.getString("dc_description"),
						rs.getString("title"),
						rs.getString("action"),
						rs.getLong("bookmark_count"),
						rs.getString("comments_disabled"),
						rs.getInt("dislike_count"),
						rs.getString("last_action"),
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
		return activity;
	}

	/**
	 * Returns a single ProsoloUser object
	 * 
	 * @param id
	 *            the id of the prosolo user
	 * @return a list of ids for social activities of the provided dtype
	 * @throws SQLException
	 */
	private ProsoloUser getProsoloUser(Long id) throws SQLException {
		ProsoloUser pUser = null;
		try (Connection c = getConnection()) {
			String sql = "SELECT * from user where id=?";
			try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				pUser = SQL.seq(stmt, Unchecked.function(rs -> new ProsoloUser(
						rs.getLong("id"),
						rs.getString("created"),
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
		return pUser;
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
	
	/*
	 * EXAMPLE FOR STREAMING A LARGE RESULT SET
	 * 
	 * PreparedStatement pst = con.prepareStatement("SELECT * FROM user"
	 * ,java.sql.ResultSet.TYPE_FORWARD_ONLY,java.sql.ResultSet.CONCUR_READ_ONLY
	 * ); pst.setFetchSize(Integer.MIN_VALUE); // pst.setString(1, author);
	 * 
	 * ResultSet res = pst.executeQuery(); try{ while (res.next()) {
	 * System.out.println(res.getString("lastname")); } }finally{ res.close();
	 * pst.close(); }
	 */
}