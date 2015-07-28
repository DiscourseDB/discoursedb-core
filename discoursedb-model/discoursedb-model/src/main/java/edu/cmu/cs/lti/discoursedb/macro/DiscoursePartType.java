package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;

public class DiscoursePartType implements Serializable {

	private static final long serialVersionUID = 532051260123187170L;

	private long id;
	
	private String type;
	
	public DiscoursePartType(){}

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
