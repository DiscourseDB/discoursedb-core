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
 * Simple PoJo to represent rows in mailing list extracts in CSV format.<br/>
 * Expects files with the following header:<br/>
 * <code>project_owner, project_name, outside_forum, unique_message, date, author_email, author_name, title, body, response_to, thread_path, message_path</code><br/>
 * 
 * @author Chris Bogart
 *
 */
public class RepoForumTies {

	private static final Logger logger = LogManager.getLogger(RepoForumTies.class);	
	
	private String projectOwner;
	private String projectName;
	private String forum;
	private boolean internal; // does mailing list really belong to this project?
	
	public RepoForumTies(){};
		
	public String getForum() {
		return forum;
	}

	public void setForum(String forum) {
		this.forum = forum;
	}

	public boolean getInternal() {
		return internal;
	}

	@JsonProperty("internal")
	public void setInternal(boolean internal) {
		this.internal = internal;
	}

	public String getProjectOwner() {
		return projectOwner;
	}
	
	@JsonProperty("project_owner")
	public void setProjectOwner(String projectOwner) {
		this.projectOwner = projectOwner;
	}
	public String getProjectName() {
		return projectName;
	}
	@JsonProperty("project_name")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	

	
	

}
