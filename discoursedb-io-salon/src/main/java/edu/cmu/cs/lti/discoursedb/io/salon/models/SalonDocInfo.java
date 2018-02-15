package edu.cmu.cs.lti.discoursedb.io.salon.models;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.jooq.lambda.SQL;
import org.jooq.lambda.Seq;
import org.jooq.lambda.Unchecked;

public class SalonDocInfo {
	public long docID = 0;
	public String type = "";
	public long authorID = 0;
	public String authorName = "";
	public String title = "";
	public String description = "";
	public String body = "";
	public Date uploadDate;
	public Date viewDate;
	public String password = "";
	public String url = "";
	public String label1 = "";
	public String label2 = "";
	public String cat = "";
	public String status = "";
	public String slider = "";
	public long folder = 0;
	public String mode = "";
	static public List<SalonDocInfo> getSalonDocInfo(int salonID, SalonDB salonDB) throws SQLException {
		String sql = "SELECT * from salondocs join documents on doc_id = document_id where salon_id=?";
		try (PreparedStatement stmt = salonDB.prep(sql)) {
			stmt.setLong(1, salonID); 
			
			Seq<SalonDocInfo> docs =  SQL.seq(stmt,  Unchecked.function(rs -> {
				SalonDocInfo salondoc = new SalonDocInfo();
				salondoc.docID = rs.getLong("document_id");
				salondoc.type = rs.getString("document_type");
				salondoc.authorID = rs.getInt("document_author_id");
				salondoc.authorName = rs.getString("document_author");
				salondoc.title = rs.getString("document_title");
				salondoc.description = rs.getString("document_description");
				salondoc.body = rs.getString("document_body");
				salondoc.uploadDate = rs.getTimestamp("document_upload_date");
				salondoc.viewDate = rs.getTimestamp("document_view_date");
				salondoc.url = rs.getString("document_url");
				salondoc.label1 = rs.getString("document_label1");
				salondoc.label2 = rs.getString("document_label2");
				salondoc.cat = rs.getString("document_cat");
				salondoc.status = rs.getString("document_status");
				salondoc.slider = rs.getString("document_slider");
				salondoc.folder = rs.getLong("document_folder");
				salondoc.mode = rs.getString("document_mode");
				return salondoc;
			  }
			));
			return docs.toList();
		}
	}
}