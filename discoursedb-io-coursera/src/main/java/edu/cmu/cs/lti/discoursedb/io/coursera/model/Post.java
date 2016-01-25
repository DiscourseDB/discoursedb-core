package edu.cmu.cs.lti.discoursedb.io.coursera.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the forum_posts table in coursera database
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class Post {
	
	private long id;
	private long thread_id;
	private long user_id;
	private long post_time;
	private int delete;
	private long votes;
	private String post_text;
	private String user_agent;
	private String text_type;
	private int original;

}
