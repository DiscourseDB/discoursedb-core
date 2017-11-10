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

import edu.cmu.cs.lti.discoursedb.github.converter.GithubConverterUtil;

/**
 * Simple PoJo to represent rows in GitHub issue extracts in CSV format.<br/>
 * Expects files with the following header:<br/>
 * <code>rectype,issueid,project_owner,project_name,actor,time,text,action,title,provenance,plus_1,urls,issues,userref,code</code><br/>
 * 
 * @author Oliver Ferschke
 *
 */
public class GitHubIssueComment {

	private static final Logger logger = LogManager.getLogger(GitHubIssueComment.class);	
	
	private String rectype;
	private long issueid;
	private String projectOwner;
	private String projectName;
	private String actor;
	private Date time;
	private String text;
	private String action;
	private String title;
	private String provenance;
	private String paths;
	private boolean plusOne;
	private List<String> urls;
	private String issues;
	private String userref;
	private String code;
	
	public GitHubIssueComment(){};
		
	public String getRectype() {
		return rectype;
	}
	public void setRectype(String rectype) {
		this.rectype = rectype;
	}
	public long getIssueid() {
		return issueid;
	}
	public void setIssueid(long issueid) {
		this.issueid = issueid;
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
	
	public String getIssueIdentifier() {
		return GithubConverterUtil.standardIssueIdentifier(getProjectFullName(), getIssueid());
		
	}
	
	public String getProjectFullName() {
		return getProjectOwner() + "/" + getProjectName();
	}
	
	public Date getTime() {
		return time;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssXXX")
	public void setTime(Date time) {
		this.time = time;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getProvenance() {
		return provenance;
	}
	public void setProvenance(String provenance) {
		this.provenance = provenance;
	}
	public boolean getPlusOne() {
		return plusOne;
	}
	@JsonProperty("plus_1")
	public void setPlusOne(String plusOne) {
		this.plusOne = Boolean.parseBoolean(plusOne.toLowerCase());
	}
	public List<String> getUrls() {
		return urls;
	}

	public void setUrls(String urls) {	
		if(urls.isEmpty()||urls.equals("\"\"")){
			this.urls= new ArrayList<String>();
			return;
		}
		ObjectMapper mapper = new ObjectMapper();
		List<String> urlList= new ArrayList<String>();
		try{
			urlList = mapper.readValue(urls, new TypeReference<List<String>>(){});			
		}catch(IOException e){
			logger.error("Could not parse URL field", e);
		}
		this.urls = urlList;
	}
	public String getIssues() {
		return issues;
	}
	public void setIssues(String issues) {
		this.issues = issues;
	}
	public String getUserref() {
		return userref;
	}
	public void setUserref(String userref) {
		this.userref = userref;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getPaths() {
		return paths;
	}

	public void setPaths(String paths) {
		this.paths = paths;
	}
	
	

}
