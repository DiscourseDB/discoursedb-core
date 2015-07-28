package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;

public class DiscourseRelationType implements Serializable {

	private static final long serialVersionUID = -6905270877949246079L;

	private long id;
	
	private String type;

	public DiscourseRelationType(){}
	
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
