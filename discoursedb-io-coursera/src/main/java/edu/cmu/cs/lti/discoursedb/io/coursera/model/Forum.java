package edu.cmu.cs.lti.discoursedb.io.coursera.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the forum_forums table in coursera database
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 */
@Data
@AllArgsConstructor
public class Forum {
	
	private long id;
	private long parent_id;
	private String name;
	private long opentime;
	
}
