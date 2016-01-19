package edu.cmu.cs.lti.discoursedb.io.coursera.model;

/**
 * Defines source descriptors that identifies the mapping from source id to DiscourseDB entity and disambiguates source entities.
 * 
 * e.g. "contribution_comment#id_str" means that the source entity identified by "id" in table forum_comments was translated into a contribution.
 * the same comment might be also translated into a DiscourseDB content entity and be attached with a source with the descriptor "content_comment#id_str"
 * 
 * The main reason for this is disambiguation.
 * 
 * @author Haitian Gong
 *
 */
public class CourseraSourceMapping {
	public static final String ID_STR_TO_CONTRIBUTION = "contribution#id_str";
	public static final String ID_STR_TO_CONTENT = "content#id_str";
	public static final String FROM_USER_ID_STR_TO_USER= "user#from_user_id_str";
	public static final String ID_STR_TO_DISCOURSEPART = "discoursepart#id_str";
	public static final String ID_STR_TO_CONTRIBUTION_COMMENT = "contribution_comment#id_str";
	public static final String ID_STR_TO_DISCOURSEPART_THREAD = "discoursepart_thread#id_str";
	public static final String ID_STR_TO_CONTENT_COMMENT = "content_comment#id_str";
}
