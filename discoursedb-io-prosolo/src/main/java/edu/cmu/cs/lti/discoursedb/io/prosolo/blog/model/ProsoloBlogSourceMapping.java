package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model;

/**
 * Defines source descriptors that identifies the mapping from source id to DiscourseDB entity and disambiguates source entities.
 * 
 * Schema: DiscoursDB_Entity#source_locator
 * 
 * @author Oliver Ferschke
 *
 */
public class ProsoloBlogSourceMapping {
	public static final String AUTHOR_NAME_TO_USER = "user#author";
	public static final String BLOG_ID_TO_CONTRIBUTION= "contribution#id";
	public static final String BLOG_ID_TO_CONTENT= "content#id";
}
