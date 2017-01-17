package edu.cmu.cs.lti.discoursedb.io.salon.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jooq.lambda.SQL;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;

public class SalonAnnotation {
	public long userId = 0;
	public long annotationId = 0;
	public long documentId = 0;
	public String commentText = "";
	public String area = "";
	public Long positivity;
	public Date time;
	public long replyTo = 0;
	public String status;
	public Date modifiedTime;
	public Boolean isAnonymous;
	public Boolean isPrompt;
	public Long paragraphId;
	public Long startChar;
	public Long endChar;
	public String annotationText;
	static public List<SalonAnnotation> getSalonAnnotation(long salon_doc, SalonDB salonDB) throws SQLException {
		String sql = "select * from annotation left join annotationParagraph on "
				+ "annotation.annotation_id = annotationParagraph.annotation_id where document_id=?";
		try (PreparedStatement stmt = salonDB.prep(sql)) {
			stmt.setLong(1, salon_doc); 
			
			List<SalonAnnotation> annos = new ArrayList<SalonAnnotation>();
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				SalonAnnotation anno = new SalonAnnotation();
				anno.userId = rs.getLong("user_id");
				anno.annotationId = rs.getLong("annotation_id");
				anno.documentId = rs.getLong("document_id");
				anno.area = rs.getString("comment_area");
				anno.commentText = rs.getString("comment_text");
				anno.positivity = rs.getLong("comment_positivity");
				anno.time = rs.getDate("comment_time");
				anno.replyTo = rs.getLong("reply_to");
				anno.status = rs.getString("status");
				anno.modifiedTime = rs.getDate("modified_time");
				anno.isAnonymous = rs.getBoolean("isAnonymous");
				anno.isPrompt = rs.getBoolean("isPrompt");
				anno.paragraphId = rs.getLong("paragraph_id");
				anno.startChar = rs.getLong("start_char");
				anno.endChar = rs.getLong("end_char");
				anno.annotationText = rs.getString("annotation_text");
				annos.add(anno);
			  }
			return annos;
		}
	}

}