package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines possible values for the type field in DiscoursePartType entities.
 * 
 * @author Oliver Ferschke
 */
public enum DiscoursePartTypes {
	/**
	 * A forum that is not part of another forum
	 */
	FORUM,
	/**
	 * A forum that is part of another forum
	 */
	SUBFORUM,
	/**
	 * A thread of interconnected contributions. 
	 */
	THREAD,
	/**
	 * Prosolo course credentials 
	 */
	PROSOLO_COURSE_CREDENTIALS,
	/**
	 * A Prosolo Social Activity 
	 */
	PROSOLO_SOCIAL_ACTIVITY
}
