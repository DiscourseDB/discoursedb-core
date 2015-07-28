package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;

public class FeatureType implements Serializable{

	private static final long serialVersionUID = -3343145417294760437L;

	private long id;
	
	private String name;
	
	private String datatype;
	
	private String description;
	
	public FeatureType(){}

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

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
