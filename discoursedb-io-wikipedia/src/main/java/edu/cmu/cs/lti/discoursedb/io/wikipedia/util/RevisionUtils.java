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
package edu.cmu.cs.lti.discoursedb.io.wikipedia.util;

import java.sql.Timestamp;

import org.joda.time.DateTime;
import org.joda.time.DurationFieldType;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.Revision;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionApi;

public class RevisionUtils {

	/**
 	 * Returns the revision of an article at a particular age
 	 * This method starts calculating the TimeStamp of the desired version from the creation timestamp of the provided article.
 	 * 
	 * 
	 * @param articleId id of the article
	 * @param ageInDays age in days
	 * @param revApi revisionApi object
	 * @return the revision object, if it exists. null, else
	 * @throws WikiApiException
	 */
	public static Revision getRevisionForAge(int articleId, int ageInDays, RevisionApi revApi) throws WikiApiException{		
		revApi.getFirstDateOfAppearance(articleId);
		return getRevisionForAge(articleId, revApi.getFirstDateOfAppearance(articleId), ageInDays, revApi);
	}


	/**
	 * Returns the revision of an article at a particular age
	 * 
	 * @param articleId id of the article
	 * @param creationTS explicitly defines the creation TimeStamp from where to count (e.g. if we want to retrieve the revision of the TalkPage based on the age of the article Page)
	 * @param ageInDays age in days
	 * @param revApi revisionApi object
	 * @return the revision object, if it exists. null, else
	 */
	public static Revision getRevisionForAge(int articleId, Timestamp creationTS, int ageInDays, RevisionApi revApi) throws WikiApiException{
		DateTime creation = new DateTime(creationTS);
		DateTime version = creation.withFieldAdded(DurationFieldType.days(), ageInDays);
		for(Timestamp curTS:revApi.getRevisionTimestamps(articleId)){
			DateTime curDT = new DateTime(curTS);
			if(curDT.isEqual(version)||curDT.isAfter(version)){
				return revApi.getRevision(articleId, curTS);
			}
		}
		return null;		
	}

	/**
	 * Returns the revision of an article at a particular age
	 * 
	 * @param articleId id of the article
	 * @param tagetTS we are looking for a revision created at or right after the provided timestamp
	 * @param revApi revisionApi object
	 * @return the revision object, if it exists. null, else
	 */
	public static Revision getRevisionForTimestamp(int articleId, Timestamp targetTS, RevisionApi revApi) throws WikiApiException{
		DateTime targetDT = new DateTime(targetTS);
		Timestamp prevTS=null;
		for(Timestamp curTS:revApi.getRevisionTimestamps(articleId)){
			DateTime curDT = new DateTime(curTS);
			if(curDT.isEqual(targetDT)){
				return revApi.getRevision(articleId, curTS);
			}
			if(curDT.isAfter(targetDT)&&prevTS!=null){
				return revApi.getRevision(articleId, prevTS);
			}
			prevTS=curTS;
		}
		return null;		
	}

	/**
	 * Returns the revision of an article at a particular age
	 * 
	 * @param articleId id of the article
	 * @param tagetTS we are looking for a revision created at or right after the provided timestamp
	 * @param revApi revisionApi object
	 * @return the revision object, if it exists. null, else
	 */
	public static Revision getRevisionBeforeTimestamp(int articleId, Timestamp targetTS, RevisionApi revApi) throws WikiApiException{
		DateTime targetDT = new DateTime(targetTS);
		Timestamp prevTS=null;
		for(Timestamp curTS:revApi.getRevisionTimestamps(articleId)){
			DateTime curDT = new DateTime(curTS);
			if((curDT.isEqual(targetDT)||curDT.isAfter(targetDT))&&prevTS!=null){
				return revApi.getRevision(articleId, prevTS);
			}
			prevTS=curTS;
		}
		return null;		
	}
}
