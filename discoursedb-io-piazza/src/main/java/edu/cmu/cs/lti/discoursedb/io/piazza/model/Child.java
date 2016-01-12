package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Content.Child in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Child {

	private Date created;

	private Date updated;

	private String id;
	
	private String uid;

	private String anon;
	
	private String subject;

	private String type;

	@JsonProperty("no_upvotes")
	private int numUpvotes;

	@JsonProperty("no_answer")
	private int numAnswers;

	@JsonProperty("d-bucket")
	private String dBucket;

	@JsonProperty("bucket_name")
	private String bucketName;

	@JsonProperty("bucket_order")
	private int bucketOrder;
	
	@JsonProperty("tag-endorse")
	private List<TagEndorse> tagEndorse;
		
	private List<String> folders;
	
	private List<History> history;
	
	private Config config;
	
	private List<Child> children;

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public Date getUpdated() {
		return updated;
	}

	public void setUpdated(Date updated) {
		this.updated = updated;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getAnon() {
		return anon;
	}

	public void setAnon(String anon) {
		this.anon = anon;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getNumUpvotes() {
		return numUpvotes;
	}

	public void setNumUpvotes(int numUpvotes) {
		this.numUpvotes = numUpvotes;
	}

	public int getNumAnswers() {
		return numAnswers;
	}

	public void setNumAnswers(int numAnswers) {
		this.numAnswers = numAnswers;
	}

	public String getdBucket() {
		return dBucket;
	}

	public void setdBucket(String dBucket) {
		this.dBucket = dBucket;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public int getBucketOrder() {
		return bucketOrder;
	}

	public void setBucketOrder(int bucketOrder) {
		this.bucketOrder = bucketOrder;
	}

	public List<TagEndorse> getTagEndorse() {
		return tagEndorse;
	}

	public void setTagEndorse(List<TagEndorse> tagEndorse) {
		this.tagEndorse = tagEndorse;
	}

	public List<String> getFolders() {
		return folders;
	}

	public void setFolders(List<String> folders) {
		this.folders = folders;
	}

	public List<History> getHistory() {
		return history;
	}

	public void setHistory(List<History> history) {
		this.history = history;
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
