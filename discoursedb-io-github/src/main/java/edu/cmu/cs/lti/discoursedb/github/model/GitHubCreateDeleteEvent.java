package edu.cmu.cs.lti.discoursedb.github.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Simple PoJo to represent rows in GitHub event extracts in CSV format.<br/>
 * 
 * @author Oliver Ferschke
 *
 */
public class GitHubCreateDeleteEvent {

	private static final Logger logger = LogManager.getLogger(GitHubCreateDeleteEvent.class);	
	
	String eventType;
	
	String project;
	String actor, userAuth, projectAuth;
	Date createdAt;
	String what;
	String what_type;
	
	public String getEventType() {
		return eventType;
	}
	@JsonProperty("event_type")
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getProject() {
		return project;
	}
	public void setProject(String project) {
		this.project = project;
	}
	public String getActor() {
		return actor;
	}
	public void setActor(String actor) {
		this.actor = actor;
	}
	public String getUserAuth() {
		return userAuth;
	}
	@JsonProperty("user_auth")
	public void setUserAuth(String userAuth) {
		this.userAuth = userAuth;
	}
	public String getProjectAuth() {
		return projectAuth;
	}
	@JsonProperty("project_auth")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	public void setProjectAuth(String projectAuth) {
		this.projectAuth = projectAuth;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	@JsonProperty("created_at")
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getWhat() {
		return what;
	}
	public void setWhat(String what) {
		this.what = what;
	}
	public String getWhatType() {
		return what_type;
	}
	@JsonProperty("what_type")
	public void setWhatType(String what_type) {
		this.what_type = what_type;
	}
	
	
}
