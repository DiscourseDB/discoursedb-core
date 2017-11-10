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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Java object to represent GithubArchive rows, which could be events of several different
 * types, and stored in different formats depending on the year.
 * 
 * @author Chris Bogart
 *
 */
public class GitHubArchiveEvent {

	private static final Logger logger = LogManager.getLogger(GitHubArchiveEvent.class);	
	private JsonNode props = null;
	private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
	
	public GitHubArchiveEvent(JsonNode root) {
		props = root;
	}
	
	public String getRecordType() { return props.get("type").asText(); }
	private boolean style2013() { return props.get("actor").isTextual(); }
	
	public String toString() { return this.getRecordType() + " " + props.toString(); }
	public String getActor() {
		if (this.style2013()) {
			return props.get("actor").asText();
		} else {
			return props.get("actor").get("login").asText();
		}
	}
	
	public String getProjectFullName() {
		if (this.style2013()) {
			return props.get("repository").get("owner").asText() + "/" + props.get("repository").get("name").asText();
		} else {
			return props.get("repo").get("name").asText();
		}
	}
	
	public Date getCreatedAt() throws ParseException {
		return formatter.parse(props.get("created_at").asText());
	}
	
}
