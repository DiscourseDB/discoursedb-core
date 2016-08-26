/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
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
