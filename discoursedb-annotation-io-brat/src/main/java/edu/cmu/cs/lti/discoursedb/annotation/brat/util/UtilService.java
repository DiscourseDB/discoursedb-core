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
package edu.cmu.cs.lti.discoursedb.annotation.brat.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class UtilService {

	private final @NonNull DiscourseService discourseService;
	private final @NonNull ContributionService contribService;
	private final @NonNull ContentService contentService;
	private final @NonNull AnnotationService annoService;
	
	
	/**
	 * Imports the annotations of all brat-annotated documents located in the provided folder.  
	 * 
	 * @param inputFolder the path to the brat corpus folder to import 
	 * @throws IOException if an Exception occurs accessing the folder
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	public void removeUnannotatedContribs(String[] discourseNames) throws EntityNotFoundException{
		Assert.notEmpty(discourseNames, "At least one discourse name needs to be defined.");

		for(String discourseName: discourseNames){
			removeUnannotatedContribs(discourseName);
		}
	}

	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	public void removeUnannotatedContribs(String discourseName) throws EntityNotFoundException{
		Assert.notNull(discourseName, "The discourseName cannot be null");
		Assert.hasText(discourseName, "The discourseName cannot be empty");
		
		Discourse curDiscourse = discourseService.findOne(discourseName).orElseThrow(()->new EntityNotFoundException());
		for(Contribution curContrib: contribService.findAllByDiscourse(curDiscourse)){
			if(annoService.findAnnotations(curContrib).isEmpty()){

				
				/*
				 * Without invalidating the references to related entities, we cannot delete.
				 * This should not be the case - FIX CASCADE TYPES 
				 */
				for(DiscoursePartContribution dpc:curContrib.getContributionPartOfDiscourseParts()){
					dpc.setContribution(null);
					dpc.setDiscoursePart(null);
				}
				for(DiscourseRelation sdr:curContrib.getSourceOfDiscourseRelations()){
					sdr.setSource(null);
					sdr.setTarget(null);
				}
				for(DiscourseRelation tdr:curContrib.getTargetOfDiscourseRelations()){
					tdr.setSource(null);
					tdr.setTarget(null);
				}
				/*
				 *  ------------------
				 */

				contribService.delete(curContrib);
			}
		}
		
	}	
	
	public <T> List<T> threadsort(List<T> original, Function<T, Long> contributionId, Function<T, Long> parentId) {
		LinkedList<T> reordered = new LinkedList<T>();
		Map<Long,Integer> tails = new HashMap<Long, Integer>();
		Map<Long,ArrayList<Long>> parents = new HashMap<Long, ArrayList<Long>>();
		tails.put(0L, -1);
		parents.put(0L, new ArrayList<Long>());
		
		
		for (T d : original) {
			Long id = contributionId.apply(d);
			Long parent = parentId.apply(d);
			Integer tail = tails.get(parent);
			reordered.add(tail+1,d);
			tails.put(id, tail+1);
			if (!parents.containsKey(id)) { parents.put(id, new ArrayList<Long>()); }
			parents.get(id).add(parent);
			for (Long t: tails.keySet()) {
				if (parents.get(id).contains(t) && tails.get(t) < tail+1) {
					tails.put(t, tail+1);
				} else if (t != id && tails.get(t) >= tail+1) {
					tails.put(t, tails.get(t) + 1);
				}
			}
		}
		return reordered;
	}
	
}
