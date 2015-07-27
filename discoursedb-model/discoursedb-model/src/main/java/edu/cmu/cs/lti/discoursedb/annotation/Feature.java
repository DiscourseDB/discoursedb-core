package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;

public class Feature implements Serializable{

	private static final long serialVersionUID = -5462337134833586687L;

	private long id;
	private String text;
	
	private Feature_Type type;
	
	public Feature(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Feature_Type getType() {
		return type;
	}

	public void setType(Feature_Type type) {
		this.type = type;
	}
	
}
