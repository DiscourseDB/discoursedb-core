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
 * Simple PoJo to represent rows in GitHub issue extracts in CSV format.<br/>
 * Expects files with the following header:<br/>
 * <code>rectype,issueid,project_owner,project_name,actor,time,text,action,title,provenance,plus_1,urls,issues,userref,code</code><br/>
 * 
 * @author Oliver Ferschke
 *
 */
public class GitHubExternalSite {

	private static final Logger logger = LogManager.getLogger(GitHubExternalSite.class);	
	
	private String project;
	private String siteType;
	private String style;
	private String canonical;
	private String url;
	
	public GitHubExternalSite(){}

	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}


	public String getSiteType() {
		return siteType;
	}



	@JsonProperty("site_type")
	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}



	public String getStyle() {
		return style;
	}



	public void setStyle(String style) {
		this.style = style;
	}



	public String getCanonical() {
		return canonical;
	}



	public void setCanonical(String canonical) {
		this.canonical = canonical;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}

	
}
