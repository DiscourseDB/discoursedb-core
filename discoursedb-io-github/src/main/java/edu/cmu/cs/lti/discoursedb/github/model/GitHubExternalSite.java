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
