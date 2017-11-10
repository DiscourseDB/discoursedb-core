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
