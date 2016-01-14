package edu.cmu.cs.lti.discoursedb.io.coursera.model;

public class Post {
	
	private long id;
	private long thread_id;
	private long user_id;
	private long post_time;
	private int delete;
	private long votes;
	private String post_text;
	private String user_agent;
	private String text_type;
	private int original;
	
	public Post(long id, long thread_id, long user_id, long post_time, 
			int delete, long votes, String post_text, String user_agent, String text_type, int original) {
		this.setId(id);
		this.setThread_id(thread_id);
		this.setUser_id(user_id);
		this.setPost_time(post_time);
		this.setDelete(delete);
		this.setVotes(votes);
		this.setPost_text(post_text);
		this.setUser_agent(user_agent);
		this.setText_type(text_type);
		this.setOriginal(original);
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

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public long getPost_time() {
		return post_time;
	}

	public void setPost_time(long post_time) {
		this.post_time = post_time;
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

	public String getPost_text() {
		return post_text;
	}

	public void setPost_text(String post_text) {
		this.post_text = post_text;
	}

	public String getUser_agent() {
		return user_agent;
	}

	public void setUser_agent(String user_agent) {
		this.user_agent = user_agent;
	}

	public String getText_type() {
		return text_type;
	}

	public void setText_type(String text_type) {
		this.text_type = text_type;
	}

	public int getOriginal() {
		return original;
	}

	public void setOriginal(int original) {
		this.original = original;
	}
	
	

}
