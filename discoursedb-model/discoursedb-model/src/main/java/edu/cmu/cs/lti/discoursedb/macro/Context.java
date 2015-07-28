package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class Context implements Serializable {

	private static final long serialVersionUID = 6013322457584994562L;

	private long id;
	
	private Content firstRevision;
	
	private Content currentRevision;
	
	private Timestamp startTime;
	
	private Timestamp endTime;

	private ContextType type;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();

	public Context(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Content getFirstRevision() {
		return firstRevision;
	}

	public void setFirstRevision(Content firstRevision) {
		this.firstRevision = firstRevision;
	}

	public Content getCurrentRevision() {
		return currentRevision;
	}

	public void setCurrentRevision(Content currentRevision) {
		this.currentRevision = currentRevision;
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

	public ContextType getType() {
		return type;
	}

	public void setType(ContextType type) {
		this.type = type;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
}
