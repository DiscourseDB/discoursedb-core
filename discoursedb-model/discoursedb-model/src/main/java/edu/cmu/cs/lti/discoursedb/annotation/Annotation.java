package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;
import java.util.Set;

public class Annotation implements Serializable{

	private static final long serialVersionUID = 5124203257286967702L;

	private long id;
	private Set<Annotation_Instance> annotations;
	
	public Annotation(){}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Set<Annotation_Instance> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Set<Annotation_Instance> annotations) {
		this.annotations = annotations;
	}
	
	
	
}
