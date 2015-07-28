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
	
	private Content previous_revision;
	
	private Content next_revision;
	
	private Timestamp creation_time;
	
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

	public Content getPrevious_revision() {
		return previous_revision;
	}

	public void setPrevious_revision(Content previous_revision) {
		this.previous_revision = previous_revision;
	}

	public Content getNext_revision() {
		return next_revision;
	}

	public void setNext_revision(Content next_revision) {
		this.next_revision = next_revision;
	}

	public Timestamp getCreation_time() {
		return creation_time;
	}

	public void setCreation_time(Timestamp creation_time) {
		this.creation_time = creation_time;
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
