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
 * Defines possible values for the type field in DiscoursePartInteractionType entities.
 * 
 * @author Oliver Ferschke
 */
public enum DiscoursePartInteractionTypes {
	/**
	 * A user joins a conversation (e.g. chat room)
	 */
	JOIN,
	/**
	 * A user leaves a conversation (e.g. chat room)
	 */
	LEAVE,
	/**
	 * A user indicates they are ready to move on in a conversation (e.g. in an agent supported chat)
	 */
	READY,
	/**
	 *  * A user or group is not ready to move on with a conversation
	 */
	UNREADY,
    /**
	 * Watch a collaboration (e.g. a Github repository)
	 */
	WATCH,
	/**
	 * Unwatch a collaboration (e.g. a Github repository)
	 */
	UNWATCH,
	/**
	 * Create a collaboration (e.g. a Github repository)
	 */
	CREATE,
	/**
	 * Delete a collaboration (e.g. a Github repository)
	 */
	DELETE, 
	/**
	 * Make a personal copy of a collaboration (e.g. a Github repository)
	 */
	FORK_FROM, 
	/*
	 * Push a set of changes to a software repository
	 * (along with a natural language message associated with each change)
	 */
	GIT_PUSH,
	/*
	 * Merge a pull request in Git
	 * (something a repository owner/committer does to accept a
	 * proposed software change, possibly from someone without
	 * the right to commit the change themselves)
	 */
	GIT_PULL_REQUEST_MERGE,
	/*
	 * Close a github issue discussion thread.  This is sometimes
	 * done by a commit message that mentions the issue number.
	 */
	GITHUB_ISSUE_CLOSE,
	/*
	 * A pointer in one discussion linking to another
	 */
	REFER


}

