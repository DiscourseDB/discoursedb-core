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
 * Defines possible values for the type field in ContributionType entities.
 * 
 * @author Oliver Ferschke
 */
public enum ContributionTypes {
/**
 * A post that starts a new thread
 */
THREAD_STARTER,
/**
 * A post that is part of a thread or a comment to a previous reply but not a thread starter.
 */
POST,
/**
 * An image posted in a bazaar chat
 */
BAZAAR_IMAGE,
/**
 * A private message, e.g. in a bazaar chat
 */
PRIVATE_MESSAGE,
/**
 * A regular tweet.
 */
TWEET,
/**
 * A ProSolo Goal Note
 */
GOAL_NOTE,
/**
 * A ProSolo NodeSocialActivity
 */
NODE_ACTIVITY,
/**
 * A ProSolo Node Comment
 */
NODE_COMMENT,
/**
 * A ProSolo NodeSocialActivity associated with a LearningGoal node
 */
LEARNING_GOAL,
/**
 * A ProSolo NodeSocialActivity associated with a Competence node
 */
COMPETENCE,
/**
 * A ProSolo NodeSocialActivity associated with a TargetLearningGoal node
 */
TARGET_LEARNING_GOAL,
/**
 * A ProSolo NodeSocialActivity associated with a TargetCompetence node
 */
TARGET_COMPETENCE,
/**
 * A ProSolo NodeSocialActivity associated with a ResourceActivity node
 */
RESOURCE_ACTIVITY,
/**
 * A ProSolo NodeSocialActivity associated with a TargetActivity node
 */
TARGET_ACTIVITY,
/**
 * A ProSolo NodeSocialActivity associated with a UploadAssignmentActivity node
 */
UPLOAD_ASSIGNMENT_ACTIVITY,
/**
 * A ProSolo SocialActivityComment
 */
SOCIAL_ACTIVITY_COMMENT,
/*
 * A contribution in a followup discussion on Piazza
 */
PIAZZA_FOLLOWUP,
/*
 * A reply a question by a student on Piazza
 */
PIAZZA_STUDENT_ANSWER,
/*
 * A reply a question by an instructor on Piazza
 */
PIAZZA_INSTRUCTOR_ANSWER, 
/*
 * The message attached to a code commit in Git
 */
GIT_COMMIT_MESSAGE,
/*
 * A comment appended to a GIT_COMMIT_MESSAGE, in Github
 */
GITHUB_COMMIT_COMMENT,
/*
 * A wiki page
 */
WIKI_PAGE



}
