package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;

public class Content_Interaction_Type implements Serializable{

	private static final long serialVersionUID = 4718842646037560198L;

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
	
	public Content_Interaction_Type(){}
}
