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

import java.util.HashMap;
import java.util.List;

/**
 * Extracts References between UserTurns from Wikipedia discussion pages
 * 
 * @author chebotar
 * @author ferschke
 *
 */
public class ReferenceExtractor {
	
	/**
	 * Adds references to User Turns for each Topic based on the Turn indentations
	 * @param topics
	 */
	public void setReferences(List<Topic> topics){	
		for(Topic topic : topics){
			// Save last user turn for each occured indentation amount 
			HashMap<Integer, Turn> lastUserTurnAtLevel = new HashMap<Integer, Turn>();
			for(Turn userTurn : topic.getTurns()){				

				// Delete from cache all user turns that had bigger indentation amount than current
				// e.g. :::  <- to delete
				//		:::: <- to delete, no user turn further in the page can reference it
				//		::   <- current
				//		:::
				int j  = lastUserTurnAtLevel.size(); // Maximum number of entries in lastUserTurnAtLevel
				for(int i = userTurn.getIndentAmount()+1; j > 0;i++,j--){
					if(lastUserTurnAtLevel.containsKey(i))
						lastUserTurnAtLevel.remove(i);
				}
				// Check if there was a user turn with smaller indentation amount before current user turn
				for(int i = userTurn.getIndentAmount()-1;i>=0;i--){
					if(lastUserTurnAtLevel.containsKey(i)){
						Turn refersTo = lastUserTurnAtLevel.get(i);
						userTurn.setReference(refersTo);						
						break;
					}
				}
				lastUserTurnAtLevel.put(userTurn.getIndentAmount(), userTurn); // update Turn at current indenation level		
			}
		}
	}
		
}
