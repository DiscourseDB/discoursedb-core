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
 * Defines possible values for the type field in DiscoursePartType entities.
 * 
 * @author Oliver Ferschke
 */
public enum DiscoursePartTypes {
	/**
	 * A wiki talk page that contains discussions about an article. 
	 * Discourse Parts of this type might carry the title of the article to aggregate ALL Talk pages that refer to an article 
	 * without differentiating between individual archives.
	 * i.e. a DiscoursePart of this type might represent multiple talk pages that are related to the same article.
	 */
	TALK_PAGE,
	/**
	 * A forum that is not part of another forum
	 */
	FORUM,
	/**
	 * A forum that is part of another forum
	 */
	SUBFORUM,
	/**
	 * A thread of interconnected contributions. 
	 */
	THREAD,
	/**
	 * A chat room 
	 */
	CHATROOM,
	/**
	 * Prosolo course credentials 
	 */
	PROSOLO_COURSE_CREDENTIALS,
	/**
	 * A Prosolo Social Activity 
	 */
	PROSOLO_SOCIAL_ACTIVITY,
	/**
	 * A Prosolo Blog 
	 */
	PROSOLO_BLOG,
	/**
	 * A github owner's set of repositories
	 */
	GITHUB_OWNER_REPOS,
	/**
	 * A Github repository
	 */
	GITHUB_REPO, 
	/**
	 * A Github Issue
	 */
	GITHUB_ISSUE,
	/*
	 * A question on Piazza
	 */
	PIAZZA_QUESTION,
	/*
	 * A note on Piazza
	 */
	PIAZZA_NOTE, 
	/*
	 * A "push" in git contains a bunch of
	 * source code revisions, and a message for each one,
	 * and possible comments about those revisions/messages
	 */
	GIT_PUSH,
	/*
	 * A wiki associated with a project within github's infrastructure
	 */
	GITHUB_WIKI,
	/*
	 * A selection of twitter status postings
	 */
	TWEETS


	
}
