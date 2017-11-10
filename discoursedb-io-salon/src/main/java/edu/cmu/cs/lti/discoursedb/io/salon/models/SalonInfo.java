package edu.cmu.cs.lti.discoursedb.io.salon.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;

import org.jooq.lambda.SQL;
import org.jooq.lambda.Unchecked;

public class SalonInfo {
	String name = "";
	int salonID = 0;
	int ownerID = 0;
	String mode = "";
	String description = "";
	Date created;
	String salonType = "";
	static public SalonInfo getSalonInfo(int salonID, SalonDB salonDB) throws SQLException {
		String sql = "SELECT * from salons where salon_id=?";
		SalonInfo salon = new SalonInfo();
		try (PreparedStatement stmt = salonDB.prep(sql)) {
			stmt.setLong(1, salonID); 
			
			SQL.seq(stmt,  Unchecked.function(rs -> {
				salon.name = rs.getString("salon_name");
				salon.salonID = salonID;
				salon.ownerID = rs.getInt("owner_id");
				salon.mode = rs.getString("salon_mode");
				salon.description = rs.getString("salon_description");
				salon.created = rs.getTimestamp("salon_created");
				salon.salonType = rs.getString("salon_type");
				return salon;
			  }
			));
		} catch (Exception e) {
			throw e;
		}
		return salon;
	}
}