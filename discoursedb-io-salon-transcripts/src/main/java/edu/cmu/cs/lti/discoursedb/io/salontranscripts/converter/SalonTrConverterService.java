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
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.SalonTrConverterService;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.models.SalonTranscript;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * Service for mapping data retrieved from Salon to DiscourseDB
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
	
	SimpleDateFormat ddmmmyyyy = new SimpleDateFormat("ddMMMyyyy");
	public void importTranscript(SalonTranscript t, String discourseName) {
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
	
}