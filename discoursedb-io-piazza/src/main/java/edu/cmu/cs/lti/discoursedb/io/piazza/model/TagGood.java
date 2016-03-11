package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents a Content.TagGood in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagGood {

	private boolean admin;
	
	@JsonProperty("facebook-id")
	private String facebookId;
	
	private String email;
	
	private String photo;
	
	private boolean us;
	
	private String id;
	
	private String role;
	
	private String name;
}
