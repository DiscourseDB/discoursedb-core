package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.annotation.Annotation;

public class Audience implements Serializable {

	private static final long serialVersionUID = -8464122652580037597L;

	private long id;
	
	private Audience_Type type;
	
	private Set<Annotation> annotations = new HashSet<Annotation>();
	
	public Audience(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Audience_Type getType() {
		return type;
	}

	public void setType(Audience_Type type) {
		this.type = type;
	}

	public Set<Annotation> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<Annotation> annotations) {
		this.annotations = annotations;
	}
}
