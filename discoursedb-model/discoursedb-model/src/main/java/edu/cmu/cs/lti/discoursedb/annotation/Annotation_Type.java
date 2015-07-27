package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;

public class Annotation_Type implements Serializable{

	private static final long serialVersionUID = 9194247332380412321L;

	private long id;
	private String name;
	private String description;
	private boolean isEntityAnnotation;
	
	public Annotation_Type(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEntityAnnotation() {
		return isEntityAnnotation;
	}

	public void setEntityAnnotation(boolean isEntityAnnotation) {
		this.isEntityAnnotation = isEntityAnnotation;
	}
	
}
