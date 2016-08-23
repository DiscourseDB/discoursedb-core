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
