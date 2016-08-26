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

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities from the social_activity table in prosolo. 
 * Comments are based on discussions with the prosolo developers.
 * 
 * @author Oliver Ferschke
 */
@JsonPropertyOrder({ "dtype", "id", "created", "deleted", "dc_description", "title", "action", "bookmark_count",
		"comments_disabled", "dislike_count", "last_action", "like_count", "share_count", "text", "visibility",
		"avatar_url", "name", "nickname", "post_link", "profile_url", "service_type", "user_type", "actor", "maker",
		"reason", "rich_content", "goal_target", "post_object", "user_target", "node_object", "user_object",
		"node_target", "node", "social_activity", "enrollment_object", "course_object", "course_enrollment_object" })
@Data
@AllArgsConstructor
public class SocialActivity {
	/**
	 * Type of activity
	 */
	private String dtype;
	private Long id;
	private Date created;
	/**
	 * Whether activity has been deleted or not
	 */
	private String deleted;
	/**
	 * not used
	 */
	private String dc_description;
	private String title;
	/**
	 * Type of action performed in an activity, e.g. liking, creating, deleting
	 * etc.
	 */
	private String action;
	private Long bookmark_count;
	private String comments_disabled;
	private Integer dislike_count;
	/**
	 * Date when the social activity was carried out
	 */
	private Date last_action;
	private Integer like_count;
	private Integer share_count;
	/**
	 * Textual content of the activity
	 */
	private String text;
	private String visibility;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String avatar_url;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String name;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String nickname;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String post_link;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String profile_url;
	/**
	 * Related to TwitterPostSocialActivity. Null for all other activities.
	 */
	private String service_type;
	/**
	 * Twitter user or Prosolo user
	 */
	private String user_type;
	/**
	 * Removed in new schema
	 */
	private Long actor;
	/**
	 * The user who performed the action in this activity
	 */
	private Long maker;
	/**
	 * Relation to another social activity
	 */
	private Long reason;
	private Long rich_content;
	/**
	 * Pointer to goal note activity for which this action has been performed
	 * Null for other types of activities.
	 */
	private Long goal_target;
	/**
	 * Pointer to post activityfor which this action has been performed Null for
	 * other types of activities.
	 */
	private Long post_object;
	/**
	 * Pointer to user activity for which this action has been performed. Null
	 * for other types of activities.
	 */
	private Long user_target;
	private Long node_object;
	private Long user_object;
	/**
	 * Pointer to node activity for which this action has been performed. Null
	 * for other types of activities.
	 */
	private Long node_target;
	private Long node;
	/**
	 * Relation to another social activity
	 */
	private Long social_activity;
	private Long enrollment_object;
	private Long course_object;
	private Long course_enrollment_object;
}
