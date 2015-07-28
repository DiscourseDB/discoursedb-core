package edu.cmu.cs.lti.discoursedb.system;

import java.io.Serializable;
import java.sql.Timestamp;

public class DiscourseDB implements Serializable{

	private static final long serialVersionUID = 3740314651476462251L;

	private long id;
	
	private String schemaVersion;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	public DiscourseDB(){}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSchemaVersion() {
		return schemaVersion;
	}
	public void setSchemaVersion(String schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	
}
