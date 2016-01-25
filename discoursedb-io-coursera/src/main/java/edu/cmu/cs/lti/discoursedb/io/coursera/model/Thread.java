package edu.cmu.cs.lti.discoursedb.io.coursera.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the forum_threds table in coursera database
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class Thread {
	
	private long id;
	private long forum_id;
	private long user_id;
	private long posted_time;
	private long last_updated_time;
	private long last_updated_user;
	private int delete;
	private long votes;
	private String title;
	
}
