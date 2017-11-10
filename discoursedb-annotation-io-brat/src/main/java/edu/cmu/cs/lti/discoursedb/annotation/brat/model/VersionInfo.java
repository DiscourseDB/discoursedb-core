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

import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.AnnotationSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds information about the DiscourseDB entity that is associated with a
 * particular Brat annotation along with its version at export time.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionInfo {

	/**
	 * Generates a VersionInfo from a String that contains data in the format produced by toString()
	 * 
	 * @param parseLine
	 */
	public VersionInfo(String parseLine){
		String[] data = parseLine.split("\t");
		Assert.isTrue(data.length==4, "Illegal format of version info: "+parseLine);
		setType(AnnotationSourceType.valueOf(data[0]));
		setBratAnnotationId(data[1]);
		setDiscourseDBEntityId(Long.parseLong(data[2]));
		setDiscourseDBEntityVersion(Long.parseLong(data[3]));		
	}
	
	AnnotationSourceType type;
	String bratAnnotationId;
	Long discourseDBEntityId;
	Long discourseDBEntityVersion;
	
	@Override
	public String toString(){
		return getType().name()+"\t"+getBratAnnotationId()+"\t"+getDiscourseDBEntityId()+"\t"+getDiscourseDBEntityVersion();
	}
}
