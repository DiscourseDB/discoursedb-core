package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;
import java.sql.Blob;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class Content implements Serializable {

	private static final long serialVersionUID = -7686736688076492828L;

	private long id;
	
	private Content previousRevision;
	
	private Content nextRevision;
	
	private Timestamp creationTime;
	
	private String text;
	
	private Blob data;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public Content(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Content getPreviousRevision() {
		return previousRevision;
	}

	public void setPreviousRevision(Content previousRevision) {
		this.previousRevision = previousRevision;
	}

	public Content getNextRevision() {
		return nextRevision;
	}

	public void setNextRevision(Content nextRevision) {
		this.nextRevision = nextRevision;
	}

	public Timestamp getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Timestamp creationTime) {
		this.creationTime = creationTime;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Blob getData() {
		return data;
	}

	public void setData(Blob data) {
		this.data = data;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
	
}
