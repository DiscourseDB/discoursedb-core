package edu.cmu.cs.lti.discoursedb.annotation.model;

import java.util.List;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;

public class AnnotationInterchange {
	
	private String table;
	
	private long id;
	
	private List<AnnotationInstance> annotations;
	
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
	public List<AnnotationInstance> getAnnotations() {
		return annotations;
	}
	public void setAnnotations(List<AnnotationInstance> annotations) {
		this.annotations = annotations;
	}
	
	
}
