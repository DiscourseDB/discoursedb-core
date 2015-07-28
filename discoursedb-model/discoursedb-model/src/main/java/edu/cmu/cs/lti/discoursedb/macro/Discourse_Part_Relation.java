package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class Discourse_Part_Relation implements Serializable {

	private static final long serialVersionUID = 1914547709687781470L;

	private long id;
	
	private Discourse_Part source;
	
	private Discourse_Part target;
	
	private Timestamp start_time;
	
	private Timestamp end_time;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	private Discourse_Part_Relation_Type type;
	
	public Discourse_Part_Relation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Discourse_Part getSource() {
		return source;
	}

	public void setSource(Discourse_Part source) {
		this.source = source;
	}

	public Discourse_Part getTarget() {
		return target;
	}

	public void setTarget(Discourse_Part target) {
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

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	public Discourse_Part_Relation_Type getType() {
		return type;
	}

	public void setType(Discourse_Part_Relation_Type type) {
		this.type = type;
	}
	
}
