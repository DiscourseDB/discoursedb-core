package edu.cmu.cs.lti.discoursedb.core.type;

public enum ContributionInteractionTypes {
	/**
	 * A content entity was read by a user
	 */
	READ,
	/**
	 * A user follows a content entity
	 */
	FOLLOW,
	/**
	 * A user deletes a contribution
	 */
	DELETE,
	/**
	 * A user reverts the content of a contribution (needs content field to be filled)
	 */
	REVERT	
}
