package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents a Content.Child in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
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
}
