/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 * Contributor Haitian Gong
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
package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.piazza.model.Child;
import edu.cmu.cs.lti.discoursedb.io.piazza.model.PiazzaContent;
import edu.cmu.cs.lti.discoursedb.io.piazza.model.PiazzaSourceMapping;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * This Service class maps piazza contents to DiscourseDB
 * Each piazza content represents a note or question thread in QA part of a course in Piazza
 *  
 * @author Oliver Ferschke
 * @author Haitian Gong
 *
 */
@Log4j
@Service
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class PiazzaConverterService {
	
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull DataSourceService dataSourceService;
	
	/**
	 * Maps a single Piazza Content object as a discoursePart to DiscourseDB
	 * 
	 * @param discourseName  the name of the discourse
	 * @param dataSetName    the name of the ddataSet 
	 * @param content        the Piazza Content object to convert
	 */

	public void convertPiazzaContent(String discourseName, String dataSetName, PiazzaContent content){
		
		Assert.hasText(discourseName, "No discourse name defined in Piazza content converter");
		Assert.notNull(content, "The Piazza Content to convert was null");
		
		log.trace("Converting content object "+content.getId());
		
		
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName, dataSetName);
		
		//In Piazza, we regard each single Piazza Content as a DiscoursePart		
		
		// ---------- Create DiscoursePart -----------
		DiscoursePart piazzaContent = null;
		
		if(content.getTags().contains("instructor-note"))
			piazzaContent = discoursepartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.PIAZZA_NOTE);
		else
			piazzaContent = discoursepartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.PIAZZA_QUESTION);
		
		dataSourceService.addSource(piazzaContent, new DataSourceInstance(
				String.valueOf(content.getId()), 
				PiazzaSourceMapping.ID_STR_TO_DISCOURSEPART, 
				DataSourceTypes.PIAZZA, 
				dataSetName));
		// ---------- Create DiscoursePart End-----------
		
		//In Piazza, we regard the questions, notes, answers and follow up discussions in a Piazza Content
		//as contributions in their related DiscoursePart
		
		// ---------- Create Contribution and Content -----------
		
		// ---------- Map Note/Question in a Piazza Content as Contribution and Content in DiscourseDB-----------
		ContributionTypes mappedType = ContributionTypes.THREAD_STARTER;		
		Contribution curContribution = contributionService.createTypedContribution(mappedType);
		
		//set start and end time of the contribution
		curContribution.setStartTime(content.getCreated());
		curContribution.setEndTime(content.getHistory().get(0).getCreated());
		
		//set all contents of the note/question
		Content prevContent = null;
		for(int i=content.getHistory().size()-1;i>=0;i--) {
			Content curContent = contentService.createContent();
			curContent.setText(content.getHistory().get(i).getContent());
			curContent.setTitle(content.getHistory().get(i).getSubject());
			curContent.setStartTime(content.getHistory().get(i).getCreated());
			curContent.setEndTime(content.getHistory().get(i).getCreated());
			
			//set user, the author of the current revision of note/question
			User curUser = null;
			if(content.getHistory().get(i).getUid()!=null)
			    curUser = userService.createOrGetUser(discourse, String.valueOf(content.getHistory().get(i).getUid()));
			else 
				curUser = userService.createOrGetUser(discourse, "Anonymous");
			curContent.setAuthor(curUser);
			
			//build relation between different revisions of contents
			if(i!=content.getHistory().size()-1) {
				curContent.setPreviousRevision(prevContent);
				prevContent.setNextRevision(curContent);
			}
			if(i==0)
				curContribution.setCurrentRevision(curContent);
			if(i==content.getHistory().size()-1)
				curContribution.setFirstRevision(curContent);
			
			//add content entity to data source
			dataSourceService.addSource(
					curContent, new DataSourceInstance(
							content.getId(), 
							PiazzaSourceMapping.ID_STR_TO_CONTENT+String.valueOf(i), 
							DataSourceTypes.PIAZZA, dataSetName));
			
			prevContent = curContent;
		}
		
		dataSourceService.addSource(curContribution,
				new DataSourceInstance(
					String.valueOf(content.getId()), 
					PiazzaSourceMapping.ID_STR_TO_CONTRIBUTION, 
					DataSourceTypes.PIAZZA, dataSetName));
		
		discoursepartService.addContributionToDiscoursePart(curContribution, piazzaContent);
		
		// -------- Map Answers and Follow up Discussions in a Piazza Content as Contribution and Content in DiscourseDB---------
		
		//get all children of a piazza content in Json format file
		List<Child> children = content.getChildren();
		
		for(int i=0;i<children.size();i++) {
			Child curChild = children.get(i);
			
			ContributionTypes childType = null;
			if(curChild.getType().equals("followup"))
				childType = ContributionTypes.PIAZZA_FOLLOWUP;
			else if(curChild.getType().equals("s_answer"))
				childType = ContributionTypes.PIAZZA_STUDENT_ANSWER;
			else 
				childType = ContributionTypes.PIAZZA_INSTRUCTOR_ANSWER;
			
			Contribution childContribution = contributionService.createTypedContribution(childType);
			
			// -------- Map Answers in a Piazza Content as Contribution and Content in DiscourseDB---------
			
			if(curChild.getType().equals("s_answer") || children.get(i).getType().equals("i_answer")) {
				
				//set start and end time of an answer contribution
				childContribution.setStartTime(curChild.getCreated());
				childContribution.setEndTime(curChild.getHistory().get(0).getCreated());
				
				//for each answer contribution, map all its contents to DiscourseDB and build relation between the contents 
				Content prevAnswer = null;
				for(int j=curChild.getHistory().size()-1;j>=0;j--) {
					Content curContent = contentService.createContent();
					curContent.setText(curChild.getHistory().get(j).getContent());					
					curContent.setTitle(curChild.getHistory().get(j).getSubject());
					curContent.setStartTime(curChild.getHistory().get(j).getCreated());
					curContent.setEndTime(curChild.getHistory().get(j).getCreated());
					
					//set user, the author of current revision of answer
					User curUser = null;
					if(curChild.getHistory().get(j).getUid()==null)
						curUser = userService.createOrGetUser(discourse, "Anonymous");
					else
						userService.createOrGetUser(discourse, curChild.getHistory().get(j).getUid());
					curContent.setAuthor(curUser);
					
					//build relation between different revisions of contents
					if(j!=curChild.getHistory().size()-1) {
						curContent.setPreviousRevision(prevAnswer);
						prevAnswer.setNextRevision(curContent);
					}
					if(j==0)
						childContribution.setCurrentRevision(curContent);
					if(j==curChild.getHistory().size()-1)
						childContribution.setFirstRevision(curContent);
					
					//add current content entity to data source
					dataSourceService.addSource(
							curContent, new DataSourceInstance(
									curChild.getId(), 
									PiazzaSourceMapping.ID_STR_TO_CONTENT_ANSWER+curChild.getType()+String.valueOf(j), 
									DataSourceTypes.PIAZZA, dataSetName));
					
					prevAnswer = curContent;
				}
				
				dataSourceService.addSource(childContribution,
						new DataSourceInstance(
							String.valueOf(curChild.getId()), 
							PiazzaSourceMapping.ID_STR_TO_CONTRIBUTION, 
							DataSourceTypes.PIAZZA, dataSetName));
				
				discoursepartService.addContributionToDiscoursePart(childContribution, piazzaContent);
				
				contributionService.createDiscourseRelation(curContribution, childContribution, DiscourseRelationTypes.REPLY);
			}
			
			// -------- Map all Follow up Discussions in a Piazza Content as Contribution and Content in DiscourseDB---------
			else {
				
				//set start and end time of the followup contribution
				childContribution.setStartTime(curChild.getCreated());
				childContribution.setEndTime(curChild.getCreated());
				
				//set content of the followup contribution
				Content flupContent = contentService.createContent();
				flupContent.setStartTime(curChild.getCreated());
				flupContent.setEndTime(curChild.getCreated());
				flupContent.setText(curChild.getSubject());
				
				//set user, the author of the current revision of follow up
				User curUser = null;
				if(curChild.getUid()==null)
					curUser = userService.createOrGetUser(discourse, "Anonymous");
				else
					curUser = userService.createOrGetUser(discourse, curChild.getUid());
				flupContent.setAuthor(curUser);
				
				//add current content entity to data source
				dataSourceService.addSource(
						flupContent, new DataSourceInstance(
								curChild.getId(), 
								PiazzaSourceMapping.ID_STR_TO_CONTENT, 
								DataSourceTypes.PIAZZA, dataSetName));
				
				//add content to followup contribution
				childContribution.setCurrentRevision(flupContent);
				childContribution.setFirstRevision(flupContent);
				
				dataSourceService.addSource(childContribution,
						new DataSourceInstance(
							String.valueOf(curChild.getId()), 
							PiazzaSourceMapping.ID_STR_TO_CONTRIBUTION, 
							DataSourceTypes.PIAZZA, dataSetName));
				
				//build relation between follow up contribution and current discoursepart
				contributionService.createDiscourseRelation(curContribution, childContribution, DiscourseRelationTypes.REPLY);
				
				/*
				 * if there are replies to the current followup,
				 * also map them as contributions to DiscourseDB,
				 * and build relationship between the replies and the followup
				 */
				
				if(curChild.getChildren().size()>0) {
					for(int k=0;k<curChild.getChildren().size();k++) {
						Child reply = curChild.getChildren().get(k);
						Contribution replyContribution = contributionService.createTypedContribution(ContributionTypes.PIAZZA_FOLLOWUP);
						replyContribution.setStartTime(reply.getCreated());
						replyContribution.setEndTime(reply.getCreated());
						Content replyContent = contentService.createContent();
						replyContent.setStartTime(reply.getCreated());
						replyContent.setEndTime(reply.getCreated());
						replyContent.setText(reply.getSubject());
						
						//set user, the author of the current revision of follow up reply
						User replyer = null;
						if(reply.getUid()==null)
					        replyer = userService.createOrGetUser(discourse, "Anonymous");
						else
							replyer = userService.createOrGetUser(discourse, reply.getUid());
						replyContent.setAuthor(replyer);
						
						//add current content revision to data source
						dataSourceService.addSource(
								replyContent, new DataSourceInstance(
										reply.getId(), 
										PiazzaSourceMapping.ID_STR_TO_CONTENT, 
										DataSourceTypes.PIAZZA, dataSetName));
						
						replyContribution.setCurrentRevision(replyContent);
						replyContribution.setFirstRevision(replyContent);
						
						dataSourceService.addSource(replyContribution,
								new DataSourceInstance(
									String.valueOf(reply.getId()), 
									PiazzaSourceMapping.ID_STR_TO_CONTRIBUTION, 
									DataSourceTypes.PIAZZA, dataSetName));
						
						contributionService.createDiscourseRelation(childContribution, replyContribution, DiscourseRelationTypes.REPLY);
						discoursepartService.addContributionToDiscoursePart(replyContribution, piazzaContent);
					}
				}
			}
		}	
		
		
	}
}
