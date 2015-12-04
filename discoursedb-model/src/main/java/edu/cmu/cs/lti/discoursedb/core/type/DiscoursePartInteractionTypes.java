package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines possible values for the type field in DiscoursePartInteractionType entities.
 * 
 * @author Oliver Ferschke
 */
public enum DiscoursePartInteractionTypes {
	/**
	 * A user joins a conversation (e.g. chat room)
	 */
	JOIN,
	/**
	 * A user leaves a conversation (e.g. chat room)
	 */
	LEAVE,
	/**
	 * A user indicates they are ready to move on in a conversation (e.g. in an agent supported chat)
	 */
	READY
}

