package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;

public class UserRelationType implements Serializable {

	private static final long serialVersionUID = 3266414066287662012L;

	private long id;
	
	private String type;

	public UserRelationType(){}

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
