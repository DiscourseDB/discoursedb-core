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
package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

public class WikipediaTalkPageSourceMapping {

	

	/**
	 * The combination of <code>talkPageRevision</code>_<code>title of discussion</code> identifies the source of a discourse part that wraps the contributions in a single discussion thread.
	 */
	public static final String DISCUSSION_TITLE_ON_TALK_PAGE_TO_DISCOURSEPART = "discoursePart#talkPageRevision_discussionTitle";
	/**
	 * The combination of <code>talkPageRevision</code>_<code>discussion title</code>_<code>number of turn within discussion</code> identifies the source of a single contribution
	 */
	public static final String TURN_NUMBER_IN_DISCUSSION_TO_CONTRIBUTION = "contribution#talkPageRevision_discussionTitle_turnNumber";
	/**
	 * The combination of <code>talkPageRevision</code>_<code>discussion title</code>_<code>number of turn within discussion</code> identifies the source of a single content entity
	 */
	public static final String TURN_NUMBER_IN_DISCUSSION_TO_CONTENT = "content#talkPageRevision_discussionTitle_turnNumber";
		
}
