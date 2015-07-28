package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;

public class GroupType implements Serializable{

	private static final long serialVersionUID = -8085963752024237480L;

	private long id;
	
	private String type;

	public GroupType(){}

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
}
