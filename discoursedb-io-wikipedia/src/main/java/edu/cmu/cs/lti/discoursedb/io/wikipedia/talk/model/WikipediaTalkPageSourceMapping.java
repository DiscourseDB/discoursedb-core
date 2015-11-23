package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

public class WikipediaTalkPageSourceMapping {

	

	/**
	 * The combination of <code>talkPageRevision</code>_<code>title of discussion</code> identifies the source of a discourse part that wraps the contributions in a single discussion thread.
	 */
	public static final String DISCUSSION_TITLE_ON_TALK_PAGE_TO_DISCOURSEPART = "discoursePart#talkPageRevision_discussionTitle";
	/**
	 * The combination of <code>talkPageRevision</code>_<code>discussion title</code>_<code>number of turn within discussion</code> identifies the source of a single contribution
	 */
	public static final String TURN_NUMBER_IN_DISCUSSION_TO_CONTRIBUTION = "contribution#talkPageRevision_discussionTitle_turnNumber";
	/**
	 * The combination of <code>talkPageRevision</code>_<code>discussion title</code>_<code>number of turn within discussion</code> identifies the source of a single content entity
	 */
	public static final String TURN_NUMBER_IN_DISCUSSION_TO_CONTENT = "content#talkPageRevision_discussionTitle_turnNumber";
		
}
