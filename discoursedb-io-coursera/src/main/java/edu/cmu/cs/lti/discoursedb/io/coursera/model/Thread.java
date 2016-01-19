package edu.cmu.cs.lti.discoursedb.io.coursera.model;

/**
 * Wraps entities form the forum_threds table in coursera database
 * 
 * @author Haitian Gong
 *
 */
public class Thread {
	
	private long id;
	private long forum_id;
	private long user_id;
	private long posted_time;
	private long last_updated_time;
	private long last_updated_user;
	private int delete;
	private long votes;
	private String title;
	
	public Thread(long id, long forum_id, long user_id, long posted_time, long last_updated_time, 
			long last_updated_user, int delete, long votes, String title) {
		this.setId(id);
		this.setForum_id(forum_id);
		this.setUser_id(user_id);
		this.setPosted_time(posted_time);
		this.setLast_updated_time(last_updated_time);
		this.setLast_updated_user(last_updated_user);
		this.setDelete(delete);
		this.setVotes(votes);
		this.setTitle(title);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getForum_id() {
		return forum_id;
	}

	public void setForum_id(long forum_id) {
		this.forum_id = forum_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getPosted_time() {
		return posted_time;
	}

	public void setPosted_time(long posted_time) {
		this.posted_time = posted_time;
	}

	public long getLast_updated_time() {
		return last_updated_time;
	}

	public void setLast_updated_time(long last_updated_time) {
		this.last_updated_time = last_updated_time;
	}

	public long getLast_updated_user() {
		return last_updated_user;
	}

	public void setLast_updated_user(long last_updated_user) {
		this.last_updated_user = last_updated_user;
	}

	public int getDelete() {
		return delete;
	}

	public void setDelete(int delete) {
		this.delete = delete;
	}

	public long getVotes() {
		return votes;
	}

	public void setVotes(long votes) {
		this.votes = votes;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	
	
	

}
