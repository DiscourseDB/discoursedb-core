package edu.cmu.cs.lti.discoursedb.io.coursera.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the forum_comments table in coursera database
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class Comment {

	private long id;
	private long thread_id;
	private long post_id;
	private long user_id;
	private long votes;
	private String text;
	private int delete;
	private long post_time;
	
}
