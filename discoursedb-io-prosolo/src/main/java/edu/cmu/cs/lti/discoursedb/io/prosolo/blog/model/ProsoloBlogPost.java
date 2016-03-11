package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProsoloBlogPost {
	private String url_rd;
	private List<ProsoloBlogComment> comments;
	private String author;
	private String created;
	private String course;
	private String id;
	private String text;
	private String title;
	private String url;
	
	public ProsoloBlogPost() {}

	public String getUrl_rd() {
		return url_rd;
	}
	public void setUrl_rd(String url_rd) {
		this.url_rd = url_rd;
	}
	public List<ProsoloBlogComment> getComments() {
		return comments;
	}
	
	@SuppressWarnings("unchecked")
	@JsonProperty("comments")
	public void setComments(List<Map<String, Object>> comments) {
		List<ProsoloBlogComment> result = new ArrayList<>();
		for(Object curComment:comments){
			ProsoloBlogComment comment = new ProsoloBlogComment();			
			comment.setDatetime((String)((Map<String, Object>)curComment).get("datetime"));
			comment.setContent((String)((Map<String, Object>)curComment).get("content"));
			comment.setAuthor((String)((Map<String, Object>)curComment).get("author"));
			comment.setComments((List<Map<String, Object>>)((Map<String, Object>)curComment).get("comments"));
			result.add(comment);
		}
		this.comments = result;
	}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCreated() {
		return created;
	}
	public void setCreated(String created) {
		this.created = created;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getCourse() {
		return course;
	}
	public void setCourse(String course) {
		this.course = course;
	}
	
	
}
