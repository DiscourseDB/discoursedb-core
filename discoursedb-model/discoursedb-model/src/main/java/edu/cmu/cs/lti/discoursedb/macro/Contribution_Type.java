package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;

public class Contribution_Type implements Serializable {

	private static final long serialVersionUID = -4187467243916373251L;

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
}
