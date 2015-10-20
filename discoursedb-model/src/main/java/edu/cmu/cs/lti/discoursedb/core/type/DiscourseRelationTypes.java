package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines possible values for the type field in DiscourseRelationType entities.
 * 
 * @author Oliver Ferschke
 */
public enum DiscourseRelationTypes {
	/**
	 * The target contribution is a reply to the parent contribution.
	 */
	REPLY, 
	/**
	 * The target contribution is a descendant of the parent contribution
	 */
	DESCENDANT,
	/**
	 * The target contribution is a comment or note on the parent contribution
	 */
	COMMENT,
	/**
	 * The target contribution is a reshare or retweet of the parent contribution
	 */	
	RESHARE

	
}
