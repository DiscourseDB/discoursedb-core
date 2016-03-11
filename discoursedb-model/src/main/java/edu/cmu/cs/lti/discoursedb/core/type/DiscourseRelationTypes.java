package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines possible values for the type field in DiscourseRelationType entities.
 * 
 * @author Oliver Ferschke
 */
public enum DiscourseRelationTypes {
	/**
	 * The target contribution is a reply to the parent or source contribution.
	 */
	REPLY, 
	/**
	 * The target contribution is a descendant of the parent or source contribution
	 */
	DESCENDANT,
	/**
	 * The target contribution is a comment or note on the parent or source contribution
	 */
	COMMENT,
	/**
	 * The target contribution is a reshare or retweet of the parent or source contribution
	 */	
	RESHARE

	
}
