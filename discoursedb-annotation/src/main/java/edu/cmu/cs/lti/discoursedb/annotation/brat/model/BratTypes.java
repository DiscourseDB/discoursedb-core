package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

public class BratTypes {

	public static final String CONTRIB_SEPARATOR = "[**** NEW CONTRIBUTION ****]";

	public enum EntityTypes{CONTRIBUTION, CONTENT};
	
	public enum AnnotationSourceType{ANNOTATION, FEATURE};
	
	/**
	 * See http://brat.nlplab.org/standoff.html
	 * 
	 * @author Oliver Ferschke
	 *
	 */
	public enum BratAnnotationType {

		/**
		 * Text-bound (entity) annotation 
		 */
		T 
		/** 
		 * Attribute 
		 */
		,A 
		/**
		 * Relation 
		 */
		,R 
		/**
		 * Event 
		 */
		,E 
		/**
		/**
		 * Modification 
		 */
		,M 
		/**
		 * Normalization 
		 */
		,N
	}
}
