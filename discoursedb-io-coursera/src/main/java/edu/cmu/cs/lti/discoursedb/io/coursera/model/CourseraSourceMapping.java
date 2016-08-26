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
package edu.cmu.cs.lti.discoursedb.io.coursera.model;

/**
 * Defines source descriptors that identifies the mapping from source id to DiscourseDB entity and disambiguates source entities.
 * 
 * e.g. "contribution_comment#id_str" means that the source entity identified by "id" in table forum_comments was translated into a contribution.
 * the same comment might be also translated into a DiscourseDB content entity and be attached with a source with the descriptor "content_comment#id_str"
 * 
 * The main reason for this is disambiguation.
 * 
 * @author Haitian Gong
 *
 */
public class CourseraSourceMapping {
	public static final String ID_STR_TO_CONTRIBUTION = "contribution#id_str";
	public static final String ID_STR_TO_CONTENT = "content#id_str";
	public static final String FROM_USER_ID_STR_TO_USER= "user#from_user_id_str";
	public static final String ID_STR_TO_DISCOURSEPART = "discoursepart#id_str";
	public static final String ID_STR_TO_CONTRIBUTION_COMMENT = "contribution_comment#id_str";
	public static final String ID_STR_TO_DISCOURSEPART_THREAD = "discoursepart_thread#id_str";
	public static final String ID_STR_TO_CONTENT_COMMENT = "content_comment#id_str";
}
