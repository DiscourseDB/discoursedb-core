package edu.cmu.cs.lti.discoursedb.annotation.model;

import java.util.Set;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;

public class AnnotationInterchange {
	
	private String table;
	
	private long id;
	
	private Set<AnnotationInstance> annotations;
	
	public String getTable() {
		return table;
	}
	public void setTable(String table) {
		this.table = table;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Set<AnnotationInstance> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(Set<AnnotationInstance> annotations) {
		this.annotations = annotations;
	}
	
	
}
