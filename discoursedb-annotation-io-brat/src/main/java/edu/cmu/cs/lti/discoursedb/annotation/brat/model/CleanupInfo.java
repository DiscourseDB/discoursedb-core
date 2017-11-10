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

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contains all the information necessary during the cleanup phase, i.e. the entities that need to be deleted
 * and the entries in the *.versions files that need to be removed.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor	
public class CleanupInfo{
	
	File versionsFile;
	Set<Long> featuresToDelete = new HashSet<>();	
	Set<Long> annotationsToDelete = new HashSet<>();
	
	public void addFeatures(Set<Long> features){
		featuresToDelete.addAll(features);
	}
	public void addAnnotations(Set<Long> annotations){
		annotationsToDelete.addAll(annotations);
	}
}