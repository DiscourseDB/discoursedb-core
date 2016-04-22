package edu.cmu.cs.lti.discoursedb.io.tags.model;

/**
 * 
 * Defines source descriptors that identifies the mapping from source id to DiscourseDB entity and disambiguates source entities.
 * 
 * e.g. 
 * "contribution#id_str" means that a tweet in source csv file identified by its "id" was translated into a contribution entity.
 * The same "id" with descriptor "content#id_str" is used as unique identifier for the content entity of this tweet. 
 * 
 * The main reason for this is to avoid collision in importing process.
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
public class TweetSourceMapping {
	public static final String ID_STR_TO_CONTRIBUTION = "contribution#id_str";
	public static final String ID_STR_TO_CONTENT = "content#id_str";
	public static final String FROM_USER_ID_STR_TO_USER= "user#from_user_id_str";
}
