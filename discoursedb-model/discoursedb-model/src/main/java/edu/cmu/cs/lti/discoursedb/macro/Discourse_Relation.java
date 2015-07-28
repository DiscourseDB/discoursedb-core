package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class Discourse_Relation implements Serializable {

	private static final long serialVersionUID = -2533440012916999219L;

	private long id;
	
	private Contribution source;
	
	private Contribution target;
	
	private Timestamp start_time;
	
	private Timestamp end_time;
	
	private Discourse_Relation_Type type;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public Discourse_Relation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Contribution getSource() {
		return source;
	}

	public void setSource(Contribution source) {
		this.source = source;
	}

	public Contribution getTarget() {
		return target;
	}

	public void setTarget(Contribution target) {
		this.target = target;
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

	public Discourse_Relation_Type getType() {
		return type;
	}

	public void setType(Discourse_Relation_Type type) {
		this.type = type;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
}
