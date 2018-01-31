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
package edu.cmu.cs.lti.discoursedb.core.type;

/**
 * Defines the types of data sources supported by DiscourseDB.
 * This is used in the DataSourceInstance entities in addition to the entity source ids and concrete dataset names. 
 * 
 * @author Oliver Ferschke
 */
public enum DataSourceTypes {
	/**
	 * An edX dataset
	 */
	EDX,
	/**
	 * A ProSolo dataset
	 */
	PROSOLO,
	/**
	 * A ProSolo blog dataset
	 */
	PROSOLO_BLOG,
	/**
	 * A TAGS dataset 
	 */
	TAGS,
	/**
	 * Github dataset
	 */
	GITHUB,
	/**
	 * Google Groups dataset
	 */
	GOOGLE_GROUPS,
	/**
	 * A Bazaar chat dataset
	 */
	BAZAAR,
	/**
	 * A Coursera dataset
	 */
	COURSERA,
	/**
	 * A Piazza dataset
	 */
	PIAZZA,
	/**
	 * A Habworlds dataset
	 */
	HABWORLDS,
	/**
	 * A Neuwirth dataset
	 */
	NEUWIRTH,
	/*
	 * A Ravelry dataset
	 */
	RAVELRY,
	/*
	 * A Salon dataset
	 */
	SALON,
	/*
	 * An "Other" unidentified dataset
	 */
	OTHER

}
