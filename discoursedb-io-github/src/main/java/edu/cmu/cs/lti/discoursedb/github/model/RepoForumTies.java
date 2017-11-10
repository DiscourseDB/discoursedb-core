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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonProperty;

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
