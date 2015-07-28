package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;

public class DiscoursePartRelationType implements Serializable {

	private static final long serialVersionUID = -3867055194845454430L;

	private long id;
	
	private String type;
	
	public DiscoursePartRelationType(){}

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
