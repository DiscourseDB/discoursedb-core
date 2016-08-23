package edu.cmu.cs.lti.discoursedb.github.model;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

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
