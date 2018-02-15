package edu.cmu.cs.lti.discoursedb.io.salon.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.jooq.lambda.SQL;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;

public class SalonQuestion {
	public long questionID;
	public long assignmentID;
	public String questionText = "";
	public long documentID;
	public String questionTitle = "";
	public Date createdDate;
	public long userID;
	static public List<SalonQuestion> getSalonQuestions(long docID, SalonDB salonDB) throws SQLException {
		String sql = "SELECT * from question where document_id = ?";
		try (PreparedStatement stmt = salonDB.prep(sql)) {
			stmt.setLong(1,  docID);
			
			Seq<SalonQuestion> docs =  SQL.seq(stmt,  Unchecked.function(rs -> {
				SalonQuestion salonq = new SalonQuestion();
				salonq.questionID = rs.getLong("question_id");
				salonq.assignmentID = rs.getLong("assignment_id");
				salonq.questionText = rs.getString("question_text");
				salonq.documentID = rs.getLong("document_id");
				salonq.questionTitle = rs.getString("question_title");
				salonq.createdDate = rs.getTimestamp("created_date");
				salonq.userID = rs.getLong("user_id");
				return salonq;
			  }
			));
			return docs.toList();
		}
	}
}
