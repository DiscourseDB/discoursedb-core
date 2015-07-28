package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;

public class ContextType implements Serializable {

	private static final long serialVersionUID = 9191265196419948023L;

	private long id;
	
	private String type;
	
	public ContextType(){}

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
