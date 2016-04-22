package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

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
