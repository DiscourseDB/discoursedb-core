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
 * Simple PoJo to represent <br/>
 * Expects files with the following header:<br/>
 * <code></code><br/>
 * 
 * @author Chris Bogart
 *
 */
public class RevisionEvent {

	private static final Logger logger = LogManager.getLogger(RevisionEvent.class);	
	
	private String projectOwner;
	private String projectName;
	private String pypiName;
	private String pypiRawname;
	private String version;
	private Date uploadTime;
	private String pythonVersion;	
	private String filename;	
	private String error;
	
	public RevisionEvent(){}

	public String getPypiRawname() {
		return pypiRawname;
	}

	@JsonProperty("pypi_rawname")
	public void setPypiRawname(String pypiRawname) {
		this.pypiRawname = pypiRawname;
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
	
	public String getProjectFullName() {
		return getProjectOwner() + "/" + getProjectName();
	}

	@JsonProperty("project_name")
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getPypiName() {
		return pypiName;
	}

	@JsonProperty("pypi_name")
	public void setPypiName(String pypiName) {
		this.pypiName = pypiName;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	@JsonProperty("upload_time")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getPythonVersion() {
		return pythonVersion;
	}

	@JsonProperty("python_version")
	public void setPythonVersion(String pythonVersion) {
		this.pythonVersion = pythonVersion;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

/*
	@JsonProperty("site_admin")
	public void setSiteAdmin(String siteAdmin) {
		try {
			this.siteAdmin = Boolean.parseBoolean(siteAdmin.toLowerCase());
		} catch (Exception e) {
			this.siteAdmin = false;
		}
	}

	@JsonProperty("public_repos")
	public void setPublicRepos(String publicRepos) {
		try {
		this.publicRepos = Integer.parseInt(publicRepos);
	} catch (Exception e) {
		this.siteAdmin = false;
	}
	}


	@JsonProperty("created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}
*/
	
	
}
