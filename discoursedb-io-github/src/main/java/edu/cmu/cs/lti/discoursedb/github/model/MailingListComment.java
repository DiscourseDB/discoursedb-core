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
 * Simple PoJo to represent rows in mailing list extracts in CSV format.<br/>
 * Expects files with the following header:<br/>
 * <code>project_owner, project_name, outside_forum, unique_message, date, author_email, author_name, title, body, response_to, thread_path, message_path</code><br/>
 * 
 * @author Chris Bogart
 *
 */
public class MailingListComment {

	private static final Logger logger = LogManager.getLogger(MailingListComment.class);	
	
	private String projectOwner;
	private String projectName;
	private String outsideForum;
	private String uniqueMessage;
	private Date date;
	private String authorEmail;
	private String authorName;
	private String title;
	private String body;
	private String responseTo;
	private String threadPath;
	private String messagePath;
	
	public MailingListComment(){};
		
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
	
	public Date getDate() {
		return date;
	}
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ssXXX")
	public void setDate(Date time) {
		this.date = time;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}

	public String getOutsideForum() {
		return outsideForum;
	}
	@JsonProperty("outside_forum_id")
	public void setOutsideForum(String outsideForum) {
		this.outsideForum = outsideForum;
	}

	
	public String getFullForumName() {
		return "Forum " + outsideForum;
	}

	public String getForumThreadIdentifier() {
		return "ggroups:" + getOutsideForum() + "/" + getThreadPath();
	}

	public String getFullyQualifiedUniqueMessage() {
		return outsideForum + "#" + uniqueMessage;
	}
	
	public String getUniqueMessage() {
		return uniqueMessage;
	}

	@JsonProperty("unique_message_id")
	public void setUniqueMessage(String uniqueMessage) {
		this.uniqueMessage = uniqueMessage;
	}

	public String getAuthorName() {
		return authorName;
	}
	
	@JsonProperty("author_name")
	public void setAuthorName(String authorName) {
		this.authorName = authorName;
	}

	public String getAuthorEmail() {
		return authorEmail;
	}

	@JsonProperty("author_email")
	public void setAuthorEmail(String authorEmail) {
		this.authorEmail = authorEmail;
	}
	
	public String getAuthorNameAndEmail() {
		return getAuthorName() + " <" + getAuthorEmail() + ">";
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public String getFullyQualifiedResponseTo() {
		return outsideForum + "#" + responseTo;
	}
	@JsonProperty("response_to_message_id")
	public String getResponseTo() {
		return responseTo;
	}

	public void setResponseTo(String responseTo) {
		this.responseTo = responseTo;
	}

	@JsonProperty("thread_path")
	public String getThreadPath() {
		return threadPath;
	}

	public void setThreadPath(String threadPath) {
		this.threadPath = threadPath;
	}

	@JsonProperty("message_path")
	public String getMessagePath() {
		return messagePath;
	}

	public void setMessagePath(String messagePath) {
		this.messagePath = messagePath;
	}
	
	
	

}
