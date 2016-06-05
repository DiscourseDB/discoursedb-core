package edu.cmu.cs.lti.discoursedb.io.coursera.io;

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

import edu.cmu.cs.lti.discoursedb.io.coursera.model.Comment;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Forum;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Post;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Thread;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.User;

/**
 * Establishes a JDBC database connection to a coursera database and provides
 * methods to access Coursera data using the POJOs in the model package.
 * 
 * @author Haitian Gong
 *
 */

public class CourseraDB {
	
	private Connection con = null;
	private String host;
	private String db;
	private String user;
	private String pwd;
	
	/**
	 * Creates a databse access object for accessing coursera data from a MySQL database.
	 * 
	 * @param host host of the coursera db
	 * @param db coursera database
	 * @param usr username with read access to the coursera db
	 * @param pwd user password
	 */
	public CourseraDB(String host, String db, String usr, String pwd) {
		this.host = host;
		this.db = db;
		this.user = usr;
		this.pwd = pwd;
	}
	
	/**
	 * Returns ids of all entities of a given table in the designated coursera database. 
	 * An id in an idlist can be used to query one entity in the corresponding table at a time 
	 * in a separate PreparedStatement. 
	 * 
	 * @param table
	 *            the name of a table in coursera database
	 *            for example, "thread", "post" or "comment"
	 * @return a list of ids for all the entities in the provided table of coursera databse
	 * @throws SQLException
	 */
	
	public List<Integer> getIds(String table) throws SQLException {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		String sql = "";
		switch(table) {
		case "forum": 
			sql = "SELECT id from "+TableConstants.FORUM;
			break;
		case "thread": 
			sql = "SELECT id from "+TableConstants.THREAD;
			break;
		case "post": 
			sql = "SELECT id from "+TableConstants.POST;
			break;
		case "comment": 
			sql = "SELECT id from "+TableConstants.COMMENTS;
			break;
		case "user": 
			sql = "SELECT id from "+TableConstants.USER;
			break;
		default: System.out.println("wrong");
		}
		try (PreparedStatement stmt = getConnection().prepareStatement(sql)) {
			ids = (ArrayList<Integer>) SQL.seq(stmt, Unchecked.function(rs -> rs.getInt("id"))).collect(Collectors.toList());
		}
		return ids;
	}
	
	/**
	 * Returns a single User/Forum/Thread/Post/Comment object.
	 * 
	 * @param table 
	 *           the name of a table in coursera database
	 *           for example, "thread", "post" or "comment"
	 * @param id 
	 *           the id of an entity in a table 
	 *           for example, id of a forum, thread or post     
	 * @return a POJO object that is null if the id doesn't exist in the table.
	 * @throws SQLException
	 */
	
	public Object getDbEntity(String table, long id) throws SQLException {
		Object o = null;
		if(table.equals("forum")) {
			Optional<Forum> forumEntity = null;
			try (Connection c = getConnection()) {
				String sql = "SELECT * from "+TableConstants.FORUM+" where id=?";
				try (PreparedStatement stmt = c.prepareStatement(sql)) {
					stmt.setLong(1, id); 
					forumEntity = SQL.seq(stmt, Unchecked.function(rs -> new Forum(
							rs.getLong("id"),
							rs.getLong("parent_id"),
							rs.getString("name"),
							rs.getLong("open_time")
			            ))).findFirst();
				}
			}
			if(forumEntity.isPresent()) 
				return forumEntity.get();
			else
				return o;
		}
		else if(table.equals("post")) {
			Optional<Post> postEntity = null;
			try (Connection c = getConnection()) {
				String sql = "SELECT * from "+TableConstants.POST+" where id=?";
				try (PreparedStatement stmt = c.prepareStatement(sql)) {
					stmt.setLong(1, id); 
					postEntity = SQL.seq(stmt, Unchecked.function(rs -> new Post(
							rs.getLong("id"),
							rs.getLong("thread_id"),
							rs.getLong("user_id"),
							rs.getLong("post_time"),
							rs.getInt("deleted"),
							rs.getLong("votes"),
							rs.getString("post_text"),
							rs.getString("user_agent"),
							rs.getString("text_type"),
							rs.getInt("original")
			            ))).findFirst();
				}
			}
			if(postEntity.isPresent()) 
				return postEntity.get();
			else
				return o;
		}
		else if(table.equals("thread")) {
			Optional<Thread> threadEntity = null;
			try (Connection c = getConnection()) {
				String sql = "SELECT * from "+TableConstants.THREAD+" where id=?";
				try (PreparedStatement stmt = c.prepareStatement(sql)) {
					stmt.setLong(1, id); 
					threadEntity = SQL.seq(stmt, Unchecked.function(rs -> new Thread(
							rs.getLong("id"),
							rs.getLong("forum_id"),
							rs.getLong("user_id"),
							rs.getLong("posted_time"),
							rs.getLong("last_updated_time"),
							rs.getLong("last_updated_user"),
							rs.getInt("deleted"),
							rs.getLong("votes"),
							rs.getString("title")
			            ))).findFirst();
				}
			}
			if(threadEntity.isPresent()) 
				return threadEntity.get();
			else
				return o;
		}
		else if(table.equals("comment")) {
			Optional<Comment> commentEntity = null;
			try (Connection c = getConnection()) {
				String sql = "SELECT * from "+TableConstants.COMMENTS+" where id=?";
				try (PreparedStatement stmt = c.prepareStatement(sql)) {
					stmt.setLong(1, id); 
					commentEntity = SQL.seq(stmt, Unchecked.function(rs -> new Comment(
							rs.getLong("id"),
							rs.getLong("thread_id"),
							rs.getLong("post_id"),
							rs.getLong("user_id"),
							rs.getLong("votes"),
							rs.getString("comment_text"),
							rs.getInt("deleted"),
							rs.getLong("post_time")
			            ))).findFirst();
				}
			}
			if(commentEntity.isPresent()) 
				return commentEntity.get();
			else
				return o;
		}
		else if(table.equals("user")) {
			Optional<User> userEntity = null;
			try (Connection c = getConnection()) {
				String sql = "SELECT * from "+TableConstants.USER+" where id=?";
				try (PreparedStatement stmt = c.prepareStatement(sql)) {
					stmt.setLong(1, id); 
					userEntity = SQL.seq(stmt, Unchecked.function(rs -> new User(
							rs.getLong("coursera_id"),
							rs.getString("session_user_id"),
							rs.getString("forum_user_id")
			            ))).findFirst();
				}
			}
			if(userEntity.isPresent()) 
				return userEntity.get();
			else
				return o;
		}
		else {
			return o = null;
		}
	}
	
	private Connection getConnection() throws SQLException {
		if (con == null || con.isClosed()) {
			return DriverManager.getConnection("jdbc:mysql://" + this.host + ":" + 3306 + "/" + this.db+ "?useUnicode=true&characterEncoding=UTF-8&characterSetResults=UTF-8&useSSL=false",this.user,this.pwd);
		} else {
			return con;
		}
	}

}
