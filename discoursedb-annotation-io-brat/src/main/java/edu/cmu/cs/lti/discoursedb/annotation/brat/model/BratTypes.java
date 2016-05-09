package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

public class BratTypes {

	public static final String CONTRIB_SEPARATOR = "[**** NEW CONTRIBUTION ****]";

	public enum EntityTypes {
		DDB_CONTRIBUTION, DDB_CONTENT
	};

	public enum AnnotationSourceType {
		DDB_ANNOTATION, DDB_FEATURE
	};

	/**
	 * See http://brat.nlplab.org/standoff.html
	 * 
	 * @author Oliver Ferschke
	 *
	 */
	public enum BratAnnotationType {

		BRAT_TEXT("T")
		, BRAT_ATTRIBUTE("A")
		, BRAT_RELATION("R")
		, BRAT_EVENT("E")
		, BRAT_MODIFICATION("M")
		, BRAT_NORMALIZATION("N")
		, BRAT_NOTE("#");

		private String value;

		private BratAnnotationType(String value) {
			this.value = value;
		}

		public String toString() {
			return this.value;
		}

	}
}
