package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Represents a Content.ChangeLog in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeLog {

	private Date when;
	
	private String data;
	
	private String uid;
	
	private String to;
	
	private String anon;
	
	private String type;
}
