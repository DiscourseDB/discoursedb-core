package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines possible values for the type field in DiscoursePartType entities.
 * 
 * @author Oliver Ferschke
 */
public enum DiscoursePartTypes {
	/**
	 * A wiki talk page that contains discussions about an article. 
	 * Discourse Parts of this type might carry the title of the article to aggregate ALL Talk pages that refer to an article 
	 * without differentiating between individual archives.
	 * i.e. a DiscoursePart of this type might represent multiple talk pages that are related to the same article.
	 */
	TALK_PAGE,
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
	 * A chat room 
	 */
	CHATROOM,
	/**
	 * Prosolo course credentials 
	 */
	PROSOLO_COURSE_CREDENTIALS,
	/**
	 * A Prosolo Social Activity 
	 */
	PROSOLO_SOCIAL_ACTIVITY,
	/**
	 * A Prosolo Blog 
	 */
	PROSOLO_BLOG
	
}
