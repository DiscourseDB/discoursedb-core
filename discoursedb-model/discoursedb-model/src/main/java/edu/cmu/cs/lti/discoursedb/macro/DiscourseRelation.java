package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class DiscourseRelation implements Serializable {

	private static final long serialVersionUID = -2533440012916999219L;

	private long id;
	
	private Contribution source;
	
	private Contribution target;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private DiscourseRelationType type;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public DiscourseRelation(){}

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

	public DiscourseRelationType getType() {
		return type;
	}

	public void setType(DiscourseRelationType type) {
		this.type = type;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
}
