package edu.cmu.cs.lti.discoursedb.io.bazaar.model;
/**
 * POJO for the message information.
 * 
 * @author haitiang
 *
 */
public class Message {
	
	private String roomid;
	private String id;
	private String created_time;
	private String type;
	private String content;
	private String username;
	private String parentid;
	private String useraddress;
	
	public String getRoomid() {
		return roomid;
	}
	
	public void setRoomid(String roomid) {
		this.roomid = roomid;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getCreated_time() {
		return created_time;
	}
	
	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}

	public String getUseraddress() {
		return useraddress;
	}

	public void setUseraddress(String useraddress) {
		this.useraddress = useraddress;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}
	
}
