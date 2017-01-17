package edu.cmu.cs.lti.discoursedb.io.salon.models;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.lambda.SQL;
import org.jooq.lambda.Unchecked;


public class SalonUser {
	private static final Logger logger = LogManager.getLogger(SalonUser.class);
	public long userId;
	public String name;
	public long accessLevel;
	public String email;
	public boolean active;
	static public SalonUser getSalonUser(long userId, SalonDB salonDB) throws SQLException {
		//String sql = "SELECT * from user where userid=?";
		SalonUser user = new SalonUser();
		
		user.name = "User" + Long.toString(userId);
		user.userId = userId;
		user.email="";
		user.active = false;
		user.accessLevel = 0;
		return user;
		
		// Disable retrieval for now, to make this a little more anonymous 
		
		/*
		try (PreparedStatement stmt = salonDB.prep(sql)) {
			stmt.setLong(1, userId); 
			ResultSet rs = stmt.executeQuery();
			rs.next();	
			user.name = rs.getString("name");
			user.userId = rs.getLong("userid");
			user.accessLevel = rs.getLong("access_level");
			user.email = rs.getString("email");
			user.active = rs.getBoolean("active");
		} catch(SQLException sqle) {
			user.name = "User" + Long.toString(userId);
			user.userId = userId;
			user.email="";
			user.active = false;
			user.accessLevel = 0;
		} 
		return user;
		*/
	}
}
