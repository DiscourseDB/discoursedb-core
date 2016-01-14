package edu.cmu.cs.lti.discoursedb.io.coursera.model;

public class User {
	
	private long courseraId;
	private String session_user_id;
	private String forum_user_id;
	
	public User(long courseraId, String session_user_id, String forum_user_id) {
		this.setCourseraId(courseraId);
		this.setSession_user_id(session_user_id);
		this.setForum_user_id(forum_user_id);
	}

	public long getCourseraId() {
		return courseraId;
	}

	public void setCourseraId(long courseraId) {
		this.courseraId = courseraId;
	}

	public String getSession_user_id() {
		return session_user_id;
	}

	public void setSession_user_id(String session_user_id) {
		this.session_user_id = session_user_id;
	}

	public String getForum_user_id() {
		return forum_user_id;
	}

	public void setForum_user_id(String forum_user_id) {
		this.forum_user_id = forum_user_id;
	}
	
	

}
