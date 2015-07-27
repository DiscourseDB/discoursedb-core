package edu.cmu.cs.lti.discoursedb.macro;

import java.io.Serializable;

public class Discourse implements Serializable{

	private static final long serialVersionUID = -3736157436274230022L;

	private long id;
	private String name;
	private String source;
	
	public Discourse(){}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}
	
	
}
