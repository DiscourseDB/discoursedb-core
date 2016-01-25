package edu.cmu.cs.lti.discoursedb.io.coursera.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the _coursera_user table in coursera database
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class User {
	
	private long courseraId;
	private String session_user_id;
	private String forum_user_id;
}
