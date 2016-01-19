package edu.cmu.cs.lti.discoursedb.io.coursera.model;

/**
 * Wraps entities form the forum_comments table in coursera database
 * 
 * @author Haitian Gong
 *
 */

public class Comment {
	
	private long id;
	private long thread_id;
	private long post_id;
	private long user_id;
	private long votes;
	private String text;
	private int delete;
	private long post_time;
	
	public Comment(long id, long thread_id, long post_id, 
			long user_id, long votes, String text, int delete, long post_time) {
		this.id = id;
		this.thread_id = thread_id;
		this.post_id = post_id;
		this.user_id = user_id;
		this.votes = votes;
		this.text = text;
		this.delete = delete;
		this.setPost_time(post_time);
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getThread_id() {
		return thread_id;
	}

	public void setThread_id(long thread_id) {
		this.thread_id = thread_id;
	}

	public long getPost_id() {
		return post_id;
	}

	public void setPost_id(long post_id) {
		this.post_id = post_id;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getVotes() {
		return votes;
	}

	public void setVotes(long votes) {
		this.votes = votes;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getDelete() {
		return delete;
	}

	public void setDelete(int delete) {
		this.delete = delete;
	}

	public long getPost_time() {
		return post_time;
	}

	public void setPost_time(long post_time) {
		this.post_time = post_time;
	}
	
	
	
}
