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
