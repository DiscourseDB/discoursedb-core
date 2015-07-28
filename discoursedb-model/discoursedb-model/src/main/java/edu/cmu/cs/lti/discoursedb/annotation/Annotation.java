package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.system.DiscourseDB;

public class Annotation implements Serializable{

	private static final long serialVersionUID = 6699029374236146557L;
	
	private long id;
	
	private int begin_offset;
	
	private int end_offset;
	
	private String covered_text;
	
	private Timestamp start_time;
	
	private Timestamp end_time;	
	
	private Annotation_Type type;	
	
	private DiscourseDB discoursedb;
	
	private Set<Feature> features = new HashSet<Feature>();
	
	public Annotation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBegin_offset() {
		return begin_offset;
	}

	public void setBegin_offset(int begin_offset) {
		this.begin_offset = begin_offset;
	}

	public int getEnd_offset() {
		return end_offset;
	}

	public void setEnd_offset(int end_offset) {
		this.end_offset = end_offset;
	}

	public String getCovered_text() {
		return covered_text;
	}

	public void setCovered_text(String covered_text) {
		this.covered_text = covered_text;
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

	public Annotation_Type getType() {
		return type;
	}

	public void setType(Annotation_Type type) {
		this.type = type;
	}

	public DiscourseDB getDiscoursedb() {
		return discoursedb;
	}

	public void setDiscoursedb(DiscourseDB discoursedb) {
		this.discoursedb = discoursedb;
	}

	public Set<Feature> getFeatures() {
		return features;
	}

	public void setFeatures(Set<Feature> features) {
		this.features = features;
	}
	
	
	
}
