package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Content item in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Content {

	private Date created;
	
	private String id;

	private String status;

	private String type;

	private int nr;

	private Config config;
	
	@JsonProperty("unique_views")
	private long uniqueViews;
	
	@JsonProperty("no_answer_followup")
	private int noAnswerFollowUp;
	
	@JsonProperty("no_answer")
	private int noAnswer;

	private List<String> folders;
		
	private List<String> tags;
	
	private List<History> history;
	
	@JsonProperty("tag-good")
	private List<TagGood> tagGood;

	@JsonProperty("change-log")
	private List<ChangeLog> changeLog;	
		
	private List<Child> children;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNr() {
		return nr;
	}

	public void setNr(int nr) {
		this.nr = nr;
	}

	public long getUniqueViews() {
		return uniqueViews;
	}

	public void setUniqueViews(long uniqueViews) {
		this.uniqueViews = uniqueViews;
	}

	public int getNoAnswerFollowUp() {
		return noAnswerFollowUp;
	}

	public void setNoAnswerFollowUp(int noAnswerFollowUp) {
		this.noAnswerFollowUp = noAnswerFollowUp;
	}

	public int getNoAnswer() {
		return noAnswer;
	}

	public void setNoAnswer(int noAnswer) {
		this.noAnswer = noAnswer;
	}

	public List<String> getFolders() {
		return folders;
	}

	public void setFolders(List<String> folders) {
		this.folders = folders;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public List<History> getHistory() {
		return history;
	}

	public void setHistory(List<History> history) {
		this.history = history;
	}

	public List<TagGood> getTagGood() {
		return tagGood;
	}

	public void setTagGood(List<TagGood> tagGood) {
		this.tagGood = tagGood;
	}

	public List<ChangeLog> getChangeLog() {
		return changeLog;
	}

	public void setChangeLog(List<ChangeLog> changeLog) {
		this.changeLog = changeLog;
	}

	public List<Child> getChildren() {
		return children;
	}

	public void setChildren(List<Child> children) {
		this.children = children;
	}

	public Config getConfig() {
		return config;
	}

	public void setConfig(Config config) {
		this.config = config;
	}
}
