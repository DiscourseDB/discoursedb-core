package edu.cmu.cs.lti.discoursedb.io.coursera.model;

public class Forum {
	
	private long id;
	private long parent_id;
	private String name;
	private long opentime;
	
	public Forum(long id, long parent_id, String name, long opentime) {
		this.setId(id);
		this.setParent_id(parent_id);
		this.setName(name);
		this.setOpentime(opentime);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getParent_id() {
		return parent_id;
	}

	public void setParent_id(long parent_id) {
		this.parent_id = parent_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getOpentime() {
		return opentime;
	}

	public void setOpentime(long opentime) {
		this.opentime = opentime;
	}
	
	
	
}
