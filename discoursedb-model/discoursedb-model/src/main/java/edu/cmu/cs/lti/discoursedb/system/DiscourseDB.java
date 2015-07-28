package edu.cmu.cs.lti.discoursedb.system;

import java.io.Serializable;
import java.sql.Timestamp;

public class DiscourseDB implements Serializable{

	private static final long serialVersionUID = 3740314651476462251L;

	private long id;
	
	private String schema_version;
	
	private Timestamp start_time;
	
	private Timestamp end_time;
	
	public DiscourseDB(){}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getSchema_version() {
		return schema_version;
	}
	public void setSchema_version(String schema_version) {
		this.schema_version = schema_version;
	}
	public Timestamp getStart_time() {
		return start_time;
	}
	public void setStart_time(Timestamp start_time) {
		this.start_time = start_time;
	}
	public Timestamp getEnd_time() {
		return end_time;
	}
	public void setEnd_time(Timestamp end_time) {
		this.end_time = end_time;
	}
	
}
