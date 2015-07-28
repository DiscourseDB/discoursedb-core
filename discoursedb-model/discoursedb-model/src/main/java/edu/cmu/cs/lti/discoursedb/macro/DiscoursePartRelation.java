package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class DiscoursePartRelation implements Serializable {

	private static final long serialVersionUID = 1914547709687781470L;

	private long id;
	
	private DiscoursePart source;
	
	private DiscoursePart target;
	
	private Timestamp startTime;
	
	private Timestamp endTime;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	private DiscoursePartRelationType type;
	
	public DiscoursePartRelation(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public DiscoursePart getSource() {
		return source;
	}

	public void setSource(DiscoursePart source) {
		this.source = source;
	}

	public DiscoursePart getTarget() {
		return target;
	}

	public void setTarget(DiscoursePart target) {
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

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}

	public DiscoursePartRelationType getType() {
		return type;
	}

	public void setType(DiscoursePartRelationType type) {
		this.type = type;
	}
	
}
