package edu.cmu.cs.lti.discoursedb.io.salon.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.jooq.lambda.SQL;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;

public class SalonResponse {
	public long responseID;
	public long questionID;
	public String responseText = "";
	public long responsePositivity;
	public long userID;
	public Date responseMade;
	public Date responseModified;
	public String responseTitle;
	static public List<SalonResponse> getSalonResponses(long questionID, SalonDB salonDB) throws SQLException {
		String sql = "SELECT * from response where question_id=?";
		try (PreparedStatement stmt = salonDB.prep(sql)) {
			stmt.setLong(1, questionID); 
			
			Seq<SalonResponse> docs =  SQL.seq(stmt,  Unchecked.function(rs -> {
				SalonResponse salonr = new SalonResponse();
				salonr.responseID = rs.getLong("response_id");
				salonr.questionID = rs.getLong("question_id");
				salonr.responsePositivity = rs.getLong("response_positivity");
				salonr.responseText = rs.getString("response_text");
				salonr.responseTitle = rs.getString("response_title");
				try{
					salonr.responseMade = rs.getTimestamp("response_made");
				} catch (Exception e) {
					salonr.responseMade = null;
				}
				try {
					salonr.responseModified = rs.getTimestamp("response_modified");
				} catch (Exception e) {
					salonr.responseModified = salonr.responseMade;
				}
				salonr.userID = rs.getLong("user_id");
				return salonr;
			  }
			));
			return docs.toList();
		}
	}
}
