package edu.cmu.cs.lti.discoursedb.io.bazaar.model;

/**
 * Defines source descriptors that identifies the mapping from source id to DiscourseDB entity and disambiguates source entities.
 * For user entities in source Bazaar chatroom data, the usernames are used as source id for users 
 * 
 * Schema: DiscoursDB_Entity#source_locator
 * 
 * @author Haitian Gong
 *
 */

public class BazaarSourceMapping {
	
	public static final String ID_STR_TO_DISCOURSEPART = "discoursepart#id_str";
	
	public static final String ID_STR_TO_CONTRIBUTION = "contribution#id_str";
	
	public static final String ID_STR_TO_CONTENT = "content#id_str";
	
	public static final String FROM_USER_ID_STR_TO_USER= "user#from_user_id_str";
}
