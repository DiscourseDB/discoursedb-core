package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

public class BratTypes {

	public static final String CONTRIB_SEPARATOR = "[**** NEW CONTRIBUTION ****]";

	public enum EntityTypes {
		CONTRIBUTION, CONTENT
	};

	public enum AnnotationSourceType {
		ANNOTATION, FEATURE
	};

	/**
	 * See http://brat.nlplab.org/standoff.html
	 * 
	 * @author Oliver Ferschke
	 *
	 */
	public enum BratAnnotationType {

		TEXT("T")
		, ATTRIBUTE("A")
		, RELATION("R")
		, EVENT("E")
		, MODIFICATION("M")
		, NORMALIZATION("N")
		, NOTE("#");

		private String value;

		private BratAnnotationType(String value) {
			this.value = value;
		}

		public String toString() {
			return this.value;
		}

	}
}
