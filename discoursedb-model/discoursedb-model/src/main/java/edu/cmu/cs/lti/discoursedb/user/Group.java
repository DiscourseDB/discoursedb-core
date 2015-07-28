package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class Group implements Serializable {

	private static final long serialVersionUID = -8400689664755883198L;

	private long id;

	private GroupType type;

	private String name;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public Group(){}

	public GroupType getType() {
		return type;
	}

	public void setType(GroupType type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
