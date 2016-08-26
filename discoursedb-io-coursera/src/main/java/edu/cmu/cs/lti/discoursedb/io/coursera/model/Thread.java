/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 * Contributor Haitian Gong
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

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the forum_threds table in coursera database
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class Thread {
	
	private long id;
	private long forum_id;
	private long user_id;
	private long posted_time;
	private long last_updated_time;
	private long last_updated_user;
	private int delete;
	private long votes;
	private String title;
	
}
