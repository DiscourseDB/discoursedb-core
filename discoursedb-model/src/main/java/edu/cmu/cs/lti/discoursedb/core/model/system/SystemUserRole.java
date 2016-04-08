package edu.cmu.cs.lti.discoursedb.core.model.system;

/**
 * User roles used in DiscourseDB.
 * Might be superseded or complemented by Domain ACLs in the future.
 * 
 * @author Oliver Ferschke
 *
 */
public enum SystemUserRole {
	
	/**
	 * Role for creators of DiscourseDB annotations. 
	 */
	ANNOTATOR, 
	/**
	 * Role for DiscourseDB administrators with full access and manipulation rights. 
	 */
	ADMIN 
}
