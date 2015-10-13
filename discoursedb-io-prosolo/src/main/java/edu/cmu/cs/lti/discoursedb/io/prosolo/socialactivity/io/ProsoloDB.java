package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.io;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.jooq.lambda.SQL;
import org.jooq.lambda.Unchecked;

import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloNode;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloPost;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.ProsoloUser;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model.SocialActivity;

public class ProsoloDB {
	
	private Connection con = null;
	private String host;
	private String db;
	private String user;
	private String pwd;
	
	
	/**
	 * Creates a databse access object for accessing prosolo data from a MySQL database.
	 * 
	 * @param host host of the prosolo db
	 * @param db prosolo database
	 * @param user username with read access to the prosolo db
	 * @param pwd user password
	 */
	public ProsoloDB(String host, String db, String user, String pwd) {
		super();
		this.host = host;
		this.db = db;
		this.user = user;
		this.pwd = pwd;
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
	public List<Long> getIdsForDtype(String dtype) throws SQLException {
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
	public List<Long> getIdsForDtypeAndAction(String dtype, String action) throws SQLException {
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
	public Optional<SocialActivity> getSocialActivity(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		Optional<SocialActivity> activity = null;
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
						rs.getString("service_type"),
						rs.getString("user_type"),
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
		            ))).findFirst();
			}
		}
		return activity;
	}

	
	/**
	 * Returns a single ProsoloNode
	 * 
	 * @param id
	 *            the id of the post node ibject 
	 * @return the prosolo node object
	 * @throws SQLException
	 */
	public Optional<ProsoloNode> getProsoloNode(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		Optional<ProsoloNode> post = null;
		try (Connection c = getConnection()) {
			String sql = "SELECT * from "+TableConstants.POST+" where id=?";
			try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				post = SQL.seq(stmt, Unchecked.function(rs -> new ProsoloNode(						
						rs.getString("dtype"),
						rs.getLong("id"),
						rs.getTimestamp("created"),
						rs.getBoolean("deleted"),
						rs.getString("dc_description"),
						rs.getString("title"),
						rs.getString("visibility"),
						rs.getTimestamp("deadline"),
						rs.getBoolean("free_to_join"),
						rs.getBoolean("archived"),
						rs.getTimestamp("completed_date"),
						rs.getInt("progress"),
						rs.getBoolean("progress_activity_dependent"),
						rs.getInt("duration"),
						rs.getString("type"),
						rs.getInt("validity_period"),
						rs.getBoolean("completed"),
						rs.getTimestamp("completed_day"),
						rs.getTimestamp("date_finished"),
						rs.getTimestamp("date_started"),
						rs.getString("assignment_link"),
						rs.getString("assignment_title"),
						rs.getBoolean("completed"),
						rs.getTimestamp("date_completed"),
						rs.getLong("ta_position"),
						rs.getBoolean("mandatory"),
						rs.getInt("max_files_number"),
						rs.getBoolean("visible_to_everyone"),
						rs.getLong("maker"),
						rs.getLong("course_enrollment"),
						rs.getLong("learning_goal"),
						rs.getLong("competence"),
						rs.getLong("parent_goal"),
						rs.getLong("activity"),
						rs.getLong("parent_competence"),
						rs.getLong("rich_content")
						))).findFirst();
			}
		}
		return post;
	}
	/**
	 * Returns a single Post object
	 * 
	 * @param id
	 *            the id of the post object
	 * @return the prosolo post object
	 * @throws SQLException
	 */
	public Optional<ProsoloPost> getProsoloPost(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		Optional<ProsoloPost> post = null;
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
		            ))).findFirst();
			}
		}
		return post;
	}
	
	/**
	 * Returns a single ProsoloUser object
	 * 
	 * @param id
	 *            the id of the prosolo user
	 * @return a list of ids for social activities of the provided dtype
	 * @throws SQLException
	 */
	public Optional<ProsoloUser> getProsoloUser(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		Optional<ProsoloUser> pUser = null;
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
		            ))).findFirst();
			}
		}
		return pUser;
	}
	
	/**
	 * Returns the edX username for a given ProSolo user id.
	 * 
	 * @param id the prosolo user id
	 * @return an Optional containing the edX username of the prosolo user with the provided id, if a mapping exists
	 * @throws SQLException
	 */
	public Optional<String> mapProsoloUserIdToedXUsername(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		Optional<String> edXid=null;
		try (Connection c = getConnection()) {
			String sql = "SELECT validated_id from "+TableConstants.OPENIDACCOUNT+" where user=? and validated_id is not null";
			try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				edXid = SQL.seq(stmt, Unchecked.function(rs -> rs.getString("validated_id"))).findFirst();
			}
		}		
		if(!edXid.isPresent()){
			return edXid;
		}else{
			return Optional.of(edXid.get().substring(edXid.get().lastIndexOf("/")+1));							
		}
	}

	/**
	 * Returns the email address for a given ProSolo user identified by the user id
	 * 
	 * @param id the prosolo user id
	 * @return an Optional containing the email address of the prosolo user with the provided id, if it exists
	 * @throws SQLException
	 */
	public Optional<String> getEmailForProsoloUser(Long id) throws SQLException {
		if(id==null){
			return Optional.empty();
		}
		Optional<String> email=null;
		try (Connection c = getConnection()) {
			String sql = "SELECT address from "+TableConstants.EMAIL+" as e, "+TableConstants.USER+" as u where u.id=? and u.email=e.id and e.address is not null";
		try (PreparedStatement stmt = c.prepareStatement(sql)) {
				stmt.setLong(1, id); 
				email = SQL.seq(stmt, Unchecked.function(rs -> rs.getString("address"))).findFirst();
			}
		}
		return email;
	}

		
	private Connection getConnection() throws SQLException {
		if (con == null || con.isClosed()) {
			return DriverManager.getConnection("jdbc:mysql://" + this.host + ":3306/" + this.db, this.user, this.pwd);
		} else {
			return con;
		}
	}

	public void closeConnection() throws SQLException {
		if (con != null && !con.isClosed()) {
			con.close();
		}
	}
}
