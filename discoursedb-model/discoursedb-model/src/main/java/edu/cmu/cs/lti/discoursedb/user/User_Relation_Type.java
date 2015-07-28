package edu.cmu.cs.lti.discoursedb.user;

import java.io.Serializable;

public class User_Relation_Type implements Serializable {

	private static final long serialVersionUID = 3266414066287662012L;

	private long id;
	
	private String type;

	public User_Relation_Type(){}

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
