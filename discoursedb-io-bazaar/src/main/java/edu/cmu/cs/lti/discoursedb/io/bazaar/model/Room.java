package edu.cmu.cs.lti.discoursedb.io.bazaar.model;
/**
 * POJO for the room information.
 * 
 * @author haitiang
 *
 */
public class Room {
	
	private String id;
	private String name;
	private String created_time;
	private String modified_time;
	private String comment;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCreated_time() {
		return created_time;
	}
	
	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}
	
	public String getModified_time() {
		return modified_time;
	}
	
	public void setModified_time(String modified_time) {
		this.modified_time = modified_time;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}
	
	
}
