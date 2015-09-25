package edu.cmu.cs.lti.discoursedb.core.type;

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
	 * A Prosolo post with the type PostSocialActivity, i.e. a post to the wall. 
	 */
	PROSOLO_POST_SOCIAL_ACTIVITY
	
}
