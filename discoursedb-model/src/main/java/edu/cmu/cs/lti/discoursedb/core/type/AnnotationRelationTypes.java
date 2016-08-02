package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines possible values for the type field in AnnotationRelation entities.
 * 
 * @author Oliver Ferschke
 */
public enum AnnotationRelationTypes {
	/**
	 * An discontinuous annotation within a single (Content) entity
	 */
	DISCONTINUOUS_ANNOTATION, 
	/**
	 * An annotation that spans multiple (Content) entities
	 */
	MULTI_ENTITY_ANNOTATION

	
}
