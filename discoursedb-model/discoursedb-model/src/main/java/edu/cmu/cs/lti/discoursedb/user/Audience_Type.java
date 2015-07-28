package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;

public class Audience_Type implements Serializable {

	private static final long serialVersionUID = 2596255302608073060L;

	private long id;
	
	private String type;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public Audience_Type(){}
}
