/*******************************************************************************
 * Copyright (C)  2015 - 2017  Carnegie Mellon University
 * Author: Oliver Ferschke, Chris Bogart
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
package edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionContext;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.BulkImporterService.MissingParameterException;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.SalonTrConverterService;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.models.BulkImportOrder;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.models.SalonTranscript;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * Service for mapping data retrieved from salon classroom transcripts to DiscourseDB
 * 
 * @author Chris Bogart
 *
 */
@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SalonTrConverterService {

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursePartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull AnnotationService annoService;
	private final @NonNull BulkImporterService importerService;
	
	SimpleDateFormat ddmmmyyyy = new SimpleDateFormat("ddMMMyyyy");
	public void importTranscript_deprecated(SalonTranscript t, String discourseName) {
		String datasetName = "salonTranscripts#" + discourseName;
		String shortdate = ddmmmyyyy.format(t.getClassDate());
		String dpname = t.getModerator() + " " +shortdate + " Discussion";
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		DiscoursePart discussionDP = discoursePartService.createOrGetDiscoursePartByDataSource(
				discourse, "salonTranscripts#" + discourseName + "#" + shortdate, "salonTranscripts#class#classDate",
				DataSourceTypes.SALON, datasetName, DiscoursePartTypes.CLASS_TRANSCRIPT);
		// connect dp to d
		discussionDP.setName(dpname);
		DataSourceInstance ds = new DataSourceInstance("salonTranscripts#" + discourseName + "#" + shortdate,
				"salonTranscripts#class#classDate", DataSourceTypes.SALON, datasetName);
		dataSourceService.addSource(discussionDP, ds);
		User mod = userService.createOrGetUser(discourse, t.getModerator());
		Contribution lastModComment = null;
		for (SalonTranscript.SpeakerAndWords item : t.getClassroomContributions()) {
			User u = userService.createOrGetUser(discourse, item.speaker);
			
			//dataSourceService.addSource(u, ds);

			Content curContent = contentService.createContent();
			curContent.setText(item.words);
			curContent.setStartTime(t.getClassDate());
			curContent.setAuthor(u);
			//dataSourceService.addSource(curContent, ds);

			Contribution curContribution = contributionService.createTypedContribution(ContributionTypes.CLASS_RESPONSE);
			curContribution.setFirstRevision(curContent);
			curContribution.setCurrentRevision(curContent);
			curContribution.setStartTime(t.getClassDate());
			
			//dataSourceService.addSource(curContribution, ds);
			if (item.info.equals("moderation")) {
				curContribution.setType("MODERATOR_PROMPT");
				lastModComment = curContribution;
			} else if (lastModComment != null){
				contributionService.createDiscourseRelation(lastModComment, curContribution, DiscourseRelationTypes.REPLY);
			}
			discoursePartService.addContributionToDiscoursePart(curContribution,  discussionDP);
		}
		
		if (t.hasInterview()) {
			User i = userService.createOrGetUser(discourse, "Interviewer");
			DiscoursePart interviewDP = discoursePartService.createOrGetDiscoursePartByDataSource(
					discourse, "salonTranscripts#" + discourseName + "#" + t.getClassDate(), "salonTranscripts#salonId#classDate",
					DataSourceTypes.SALON, "salonTranscripts#" + discourseName, DiscoursePartTypes.INTERVIEW);
			interviewDP.setName( t.getModerator() + " " +shortdate + " Interview");
			DataSourceInstance dsi = new DataSourceInstance("salonTranscripts#" + discourseName + "#" + shortdate + "interview",
					"salonTranscripts#class#classDate#intv", DataSourceTypes.SALON, datasetName);
			dataSourceService.addSource(discussionDP, dsi);
			discoursePartService.createDiscoursePartRelation(discussionDP, interviewDP, DiscoursePartRelationTypes.TALK_ABOUT_TALK);
			Contribution lastInterviewQuestion = null;
			for (SalonTranscript.SpeakerAndWords item: t.getInterviewContributions()) {
				Content curContent = contentService.createContent();
				curContent.setText(item.words);
				curContent.setStartTime(t.getClassDate());
				//dataSourceService.addSource(curContent, ds);

				Contribution curContribution = contributionService.createTypedContribution(ContributionTypes.INTERVIEW_ANSWER);
				curContribution.setFirstRevision(curContent);
				curContribution.setCurrentRevision(curContent);
				curContribution.setStartTime(t.getClassDate());
				//dataSourceService.addSource(curContribution, ds);

				if (item.info.equals("interviewer")) {
					lastInterviewQuestion = curContribution;
					curContribution.setType("INTERVIEW_QUESTION");
					curContent.setAuthor(i);
				} else {
					if (lastInterviewQuestion != null) {
						contributionService.createDiscourseRelation(lastInterviewQuestion, curContribution, DiscourseRelationTypes.REPLY);
					}
					curContent.setAuthor(mod);
				}
				discoursePartService.addContributionToDiscoursePart(curContribution,  interviewDP);
			}
		}
	}
	
	
	public void importTranscript(SalonTranscript t, String discourseName) throws MissingParameterException {
		String datasetName = "salonTranscripts#" + discourseName;
		String shortdate = ddmmmyyyy.format(t.getClassDate());
		String dpname = t.getModerator() + " " +shortdate + " Discussion";
		
		List<BulkImportOrder> ios = new ArrayList<BulkImportOrder>();
		
		
		ios.add(new BulkImportOrder(datasetName,
				"discourse", "salonTranscripts#class",
				"salonTranscripts"
				).put("name",discourseName)
				);
		String dpId = "salonTranscripts#" + discourseName + "#" + shortdate;
		ios.add(new BulkImportOrder(datasetName,
				"discourse_part", "salonTranscripts#class#classDate",
				dpId
				).put("name",dpname).put("type", "CLASS_TRANSCRIPT")
				.put("fk_discourse", "salonTranscripts")
				);
		ios.add(new BulkImportOrder(datasetName,
				"user", "salonTranscripts#user",
				"salonTranscripts#" + t.getModerator()
				).put("username", t.getModerator()));
		

		String lastModComment = null;
		int line = 1;
		for (SalonTranscript.SpeakerAndWords item : t.getClassroomContributions()) {
			
			ios.add(new BulkImportOrder(datasetName,
					"user", "salonTranscripts#user",
					"salonTranscripts#" + item.speaker
					).put("username", item.speaker));
			ios.add(new BulkImportOrder(datasetName,
					"content", "salonTranscripts#class#classDate#line",
					"salonTranscripts#" + discourseName + "#" + shortdate + "#" + line
					).put("text",item.words)
					.put("start_time", BulkImporterService.sdfmt.format(t.getClassDate()))
					.put("fk_user_id", "salonTranscripts#" + item.speaker)
					);
			String contribId ="salonTranscripts#" + discourseName + "#" + shortdate + "#" + line;
			if (item.info.equals("moderation")) {
				ios.add(new BulkImportOrder(datasetName,
						"contribution", "salonTranscripts#class#classDate#line",
						contribId
						).put("type","MODERATOR_PROMPT")
						.put("fk_current_revision", "salonTranscripts#" + discourseName + "#" + shortdate + "#" + line)
						.put("start_time", BulkImporterService.sdfmt.format(t.getClassDate()))
						);
				lastModComment = "salonTranscripts#" + discourseName + "#" + shortdate + "#" + line;
			} else {
				ios.add(new BulkImportOrder(datasetName,
					"contribution", "salonTranscripts#class#classDate#line",
					contribId
					).put("type","CLASS_RESPONSE")
						.put("fk_current_revision", "salonTranscripts#" + discourseName + "#" + shortdate + "#" + line)
						.put("start_time", BulkImporterService.sdfmt.format(t.getClassDate()))
					
					);
				if (lastModComment != null) {
				   ios.add(new BulkImportOrder(datasetName,
						"discourse_relation", "salonTranscripts#class#classDate#line",
						"salonTranscripts#" + discourseName + "#" + shortdate + "#" + line
						).put("type","REPLY")
						.put("fk_target", contribId)
						.put("fk_source", lastModComment)
						);
				}
			}
			ios.add(new BulkImportOrder(datasetName,
					"contribution_partof_discourse_part", "salonTranscripts#class#classDate#line",
					"salonTranscripts#" + discourseName + "#" + shortdate + "#" + line
					).put("fk_contribution",contribId)
					.put("fk_discourse_part", dpId)
					);
			line += 1;
		}
		
		if (t.hasInterview()) {
			ios.add(new BulkImportOrder(datasetName,
					"user", "salonTranscripts#user",
					"salonTranscripts#interviewer"
					).put("username", "Interviewer"));
			
			String dpiId = "salonTranscripts#" + discourseName + "#" + shortdate + "#interview";
			String dpiname = t.getModerator() + " " +shortdate + " Interview";
			ios.add(new BulkImportOrder(datasetName,
					"discourse_part", "salonTranscripts#class#classDate",
					dpiId
					).put("name",dpiname).put("type", "INTERVIEW")
					.put("fk_discourse", "salonTranscripts")
					);
			ios.add(new BulkImportOrder(datasetName,
					"discourse_part_relation", "salonTranscripts#class#classDate#line",
					"salonTranscripts#" + discourseName + "#" + shortdate + "#" + line
					).put("fk_source",dpiId)
					.put("fk_target", dpId)
					.put("type", "TALK_ABOUT_TALK")
					);
			String lastInterviewQuestion = null;
			for (SalonTranscript.SpeakerAndWords item: t.getInterviewContributions()) {
				String contribId ="salonTranscripts#" + discourseName + "#" + shortdate + "#" + line;
				ios.add(new BulkImportOrder(datasetName,
						"content", "salonTranscripts#class#classDate#line",
						contribId
						).put("text",item.words)
						.put("start_time", BulkImporterService.sdfmt.format(t.getClassDate()))
						.put("fk_user_id", "salonTranscripts#" + item.speaker)
						);
				if (item.info.equals("interviewer")) {
					ios.add(new BulkImportOrder(datasetName,
							"contribution", "salonTranscripts#class#classDate#line",
							contribId
							).put("type","INTERVIEW_QUESTION")
							.put("fk_current_revision", contribId)
							.put("start_time", BulkImporterService.sdfmt.format(t.getClassDate()))
							);
					lastModComment = contribId;
				} else {
					ios.add(new BulkImportOrder(datasetName,
							"contribution", "salonTranscripts#class#classDate#line",
							contribId
							).put("type","INTERVIEW_ANSWER")
							.put("fk_current_revision", contribId)
							.put("start_time", BulkImporterService.sdfmt.format(t.getClassDate()))
							);
					if (lastModComment != null) {
						   ios.add(new BulkImportOrder(datasetName,
								"discourse_relation", "salonTranscripts#class#classDate#line",
								contribId
								).put("type","REPLY")
								.put("fk_target", contribId)
								.put("fk_source", lastModComment)
								);
						}
				}
				ios.add(new BulkImportOrder(datasetName,
						"contribution_partof_discourse_part", "salonTranscripts#class#classDate#line",
						contribId
						).put("fk_contribution",contribId)
						.put("fk_discourse_part", dpiId)
						);
				
				line += 1;
			}
		}
		importerService.batchImport(ios, datasetName, DataSourceTypes.SALON);
	}
}