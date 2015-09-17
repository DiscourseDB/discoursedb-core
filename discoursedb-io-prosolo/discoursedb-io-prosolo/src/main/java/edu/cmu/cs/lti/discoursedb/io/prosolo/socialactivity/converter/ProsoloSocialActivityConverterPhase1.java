package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.SQL;
import org.jooq.lambda.Unchecked;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

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
 * <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Transactional
@Order(1)
public class ProsoloSocialActivityConverterPhase1 implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(ProsoloSocialActivityConverterPhase1.class);

	private Connection con = null;
	private String host;
	private String db;
	private String user;
	private String pwd;

	@Override
	public void run(String... args) throws Exception {

		if (args.length != 4) {
			logger.error("Missing database credentials <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>");
			System.exit(1);
		}

		this.host = args[0];
		this.db = args[1];
		this.user = args[2];
		this.pwd = args[3];

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
	 * Within this method, only statements need to be properly closed. The
	 * connection is managed by the the calling run method and will be closed
	 * eventually.
	 * 
	 * @throws SQLException
	 */
	private void map() throws SQLException {
		mapPostSocialActivities();
		//TODO mapNodeSocialAcitvities
		//TODO apXSocialActivities
	}

	
	private void mapPostSocialActivities() throws SQLException{
		List<Long> ids = getIdsForDtype("PostSocialActivity");
		for (Long l : ids) {
			SocialActivity curACtivity = getSocialActivity(l);
			
			//TODO do something with the activity
			//at this point, we are free to make new queries using any properties in the activity object			
		}
	}
	
	/**
	 * Returns all ids for social activities of the given dtype. This idlist can
	 * then be used to query one social activity at a time. This might be
	 * preferred to streaming a whole resultset (even though this would have a
	 * higher performance), because no other statements can be executed before
	 * the (streaming) query isn't closed.
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