package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class UserRelation implements Serializable {

	private static final long serialVersionUID = -5267036520925282560L;

	private long id;
	
	private UserRelationType type;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private User source;
	
	private User target;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public UserRelation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public UserRelationType getType() {
		return type;
	}

	public void setType(UserRelationType type) {
		this.type = type;
	}

	public Timestamp getStartTime() {
		return startTime;
	}

	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}

	public Timestamp getEnd_time() {
		return endTime;
	}

	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
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
