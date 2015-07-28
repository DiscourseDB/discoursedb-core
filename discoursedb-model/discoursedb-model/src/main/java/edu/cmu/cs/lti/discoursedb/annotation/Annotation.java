package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.system.DiscourseDB;

public class Annotation implements Serializable{

	private static final long serialVersionUID = 6699029374236146557L;
	
	private long id;
	
	private int beginOffset;
	
	private int endOffset;
	
	private String coveredText;
	
	private Timestamp startTime;
	
	private Timestamp endTime;	
	
	private AnnotationType type;	
	
	private DiscourseDB discoursedb;
	
	private Set<Feature> features = new HashSet<Feature>();
	
	public Annotation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getBeginOffset() {
		return beginOffset;
	}

	public void setBeginOffset(int beginOffset) {
		this.beginOffset = beginOffset;
	}

	public int getEndOffset() {
		return endOffset;
	}

	public void setEndOffset(int endOffset) {
		this.endOffset = endOffset;
	}

	public String getCoveredText() {
		return coveredText;
	}

	public void setCoveredText(String coveredText) {
		this.coveredText = coveredText;
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

	public AnnotationType getType() {
		return type;
	}

	public void setType(AnnotationType type) {
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
