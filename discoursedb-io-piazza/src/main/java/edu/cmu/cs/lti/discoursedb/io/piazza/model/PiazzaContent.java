package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents a PiazzaContent item in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PiazzaContent {

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
}
