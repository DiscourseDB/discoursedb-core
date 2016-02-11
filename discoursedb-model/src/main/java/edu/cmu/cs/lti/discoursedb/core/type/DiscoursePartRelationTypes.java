package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines possible values for the type field in DiscoursePartRelationType entities.
 * 
 * @author Oliver Ferschke
 */
public enum DiscoursePartRelationTypes {

	/**
	 * The parent or source DiscoursePart represents the discussion space of an
	 * article (i.e. one or more Talk pages) and the child or target
	 * DiscoursePart represents the DiscoursePart which represents a single
	 * Discussion
	 */
	TALK_PAGE_HAS_DISCUSSION, 
	/**
	 * Generic meronomy relationships
	 */
	SUBPART
}
