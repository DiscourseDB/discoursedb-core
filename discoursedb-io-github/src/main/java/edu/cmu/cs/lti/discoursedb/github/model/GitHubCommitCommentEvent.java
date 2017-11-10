/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Authors: Oliver Ferschke and Chris Bogart
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.github.model;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple PoJo to represent rows in GitHub event extracts in CSV format.<br/>
 * 
 * @author Oliver Ferschke
 *
 */
public class GitHubCommitCommentEvent {

	private static final Logger logger = LogManager.getLogger(GitHubCommitCommentEvent.class);	
	
	String eventType;
	String project;
	String actor, userAuth, projectAuth;
	Date createdAt;
	String commitComment;
	String commitCommentUrl;
	String sha;
	
	public String getEventType() {
		return eventType;
	}
	@JsonProperty("event_type")
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public String getCommitComment() {
		return commitComment;
	}
	@JsonProperty("commit_comment")
	public void setCommitComment(String commitComment) {
		this.commitComment = commitComment;
	}
	public String getCommitCommentUrl() {
		return commitCommentUrl;
	}
	@JsonProperty("commit_comment_url")
	public void setCommitCommentUrl(String commitCommentUrl) {
		this.commitCommentUrl = commitCommentUrl;
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
	
	public String getSha() {
		return sha;
	}
	public void setSha(String sha) {
		this.sha = sha;
	}
	
	
}
