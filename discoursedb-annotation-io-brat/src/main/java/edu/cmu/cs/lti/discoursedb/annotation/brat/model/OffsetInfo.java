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
package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import org.springframework.util.Assert;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Holds information about where in the aggregated brat file a particular Contribution or Content entity begins.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class OffsetInfo {
	
	/**
	 * Generates an OffsetInfo from a String that contains data in the format produced by toString()
	 * 
	 * @param parseLine
	 */
	public OffsetInfo(String parseLine){
		String[] data = parseLine.split("\t");
		Assert.isTrue(data.length==3, "Illegal format of offset info: "+parseLine);
		setSpanOffset(Integer.parseInt(data[0]));
		setDiscourseDbContributionId(Long.parseLong(data[1]));
		setDiscourseDbContentId(Long.parseLong(data[2]));
	}
	
	int spanOffset;
	Long discourseDbContributionId;
	Long discourseDbContentId;
	
	public String toString(){
		return getSpanOffset()+"\t"+getDiscourseDbContributionId()+"\t"+getDiscourseDbContentId();
	}
}
