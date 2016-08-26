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
package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

/**
 * Defines source descriptors that identifies the mapping from source id to DiscourseDB entity and disambiguates source entities.
 * 
 * e.g. "contribution#social_activity.id" means that the source entity identified by "id" in table social_activity was translated into a contribution.
 * the same social activity might be also translated into a DiscourseDB content entity and be attached with a source with the descriptor "content#social_activity.id"
 * 
 * The main reason for this is disambiguation.
 * 
 * @author Oliver Ferschke
 *
 */
public class ProsoloSourceMapping {
	public static final String SOCIAL_ACTIVITY_TO_CONTRIBUTION = "contribution#social_activity.id"; 
	public static final String SOCIAL_ACTIVITY_TO_CONTENT = "content#social_activity.id"; 
	public static final String NODE_TO_CONTRIBUTION = "contribution#node.id"; 
	public static final String NODE_TO_CONTENT = "content#node.id"; 
	public static final String SOCIAL_ACTIVITY_TO_USER = "user#social_activity.id"; 
	public static final String POST_TO_CONTRIBUTION = "contribution#post.id"; 
	public static final String POST_TO_CONTENT = "content#post.id"; 
	public static final String USER_TO_USER = "user#user.id"; 
}
