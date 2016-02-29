package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Represents a Content.History in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class History {
	
	private Date created;

	private String subject;
	
	private String uid;
	
	private String anon;
	
	private String content;
}
