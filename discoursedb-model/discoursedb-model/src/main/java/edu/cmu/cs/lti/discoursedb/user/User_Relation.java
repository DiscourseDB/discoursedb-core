package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class User_Relation implements Serializable {

	private static final long serialVersionUID = -5267036520925282560L;

	private long id;
	
	private User_Relation_Type type;
	
	private Timestamp start_time;
	
	private Timestamp end_time;
	
	private User source;
	
	private User target;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public User_Relation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User_Relation_Type getType() {
		return type;
	}

	public void setType(User_Relation_Type type) {
		this.type = type;
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

	public User getSource() {
		return source;
	}

	public void setSource(User source) {
		this.source = source;
	}

	public User getTarget() {
		return target;
	}

	public void setTarget(User target) {
		this.target = target;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
}
