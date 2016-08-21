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
public class GitHubPullReqCommits {

	private static final Logger logger = LogManager.getLogger(GitHubPullReqCommits.class);	
	
	String sha;
	String pullreqId;
	String committer;
	String author;
	String fullName;
	Date createdAt;
	


	@JsonProperty("created_at")
	public Date getCreatedAt() {
		return createdAt;
	}
	@JsonProperty("created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public String getIssueIdentifier() {
		return "Issue " + getFullName() + "#" + getPullreqId();
	}
	
	
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}
	public String getPullreqId() {
		return pullreqId;
	}
	@JsonProperty("pullreq_id")
	public void setPullreqId(String pullreqId) {
		this.pullreqId = pullreqId;
	}
	public String getCommitter() {
		return committer;
	}
	public void setCommitter(String committer) {
		this.committer = committer;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getFullName() {
		return fullName;
	}
	@JsonProperty("full_name")
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	
}
