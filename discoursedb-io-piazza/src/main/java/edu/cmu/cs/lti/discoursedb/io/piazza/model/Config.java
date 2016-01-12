package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Config element in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Config {
	
	 @JsonProperty("feed_groups")
	private String feedGroups;

	 @JsonProperty("is_default")
	private int isDefault;

}
