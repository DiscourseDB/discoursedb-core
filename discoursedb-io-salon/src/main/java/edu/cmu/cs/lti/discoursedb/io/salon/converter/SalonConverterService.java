package edu.cmu.cs.lti.discoursedb.io.salon.converter;

import java.sql.SQLException;
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
import edu.cmu.cs.lti.discoursedb.io.salon.converter.SalonConverterService;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonAnnotation;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonDB;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonDocInfo;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonInfo;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonQuestion;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonResponse;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonUser;
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
public class SalonConverterService {

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursePartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull AnnotationService annoService;
	
	public String discourseName;
	public String datasetName;
	public SalonDB salonDB;
	public int salonID;
	
	Map<String,Integer> idhash = new HashMap<String,Integer>();


	public void configure(String pdiscourseName, int salonID, SalonDB salondb) {
		discourseName = pdiscourseName;
		datasetName = "SALON+" + pdiscourseName;
		salonDB = salondb;
	}


	public long createSalon(int salonID) throws SQLException {
		this.salonID = salonID;
		SalonInfo salonInfo = SalonInfo.getSalonInfo(salonID, salonDB);
		DataSourceInstance dsi = dataSourceService.createIfNotExists(
				new DataSourceInstance("salons#" + salonID, "salon#salonIDs", datasetName));
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName, datasetName);

		return discourse.getId();
	}


	@Deprecated
	public Map<Long,Long> mapDocumentsAsDiscourseParts(long salon) throws SQLException {
		Discourse discourse = discourseService.findOne(salon).get();
		DiscoursePart docsdp = discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
				"salons#" + salonID + "#docs", "salon#salonIDs", DataSourceTypes.SALON, datasetName, DiscoursePartTypes.FORUM);
		List<SalonDocInfo> docInfos = SalonDocInfo.getSalonDocInfo(salonID, salonDB);
		HashMap<Long,Long> docIds = new HashMap<Long,Long>(); //Old to new (salon id to ddb id)
		for (SalonDocInfo doc : docInfos) {
			DiscoursePart docdp = discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
					"documents#" + doc.docID, "salon#documents", DataSourceTypes.SALON, datasetName, DiscoursePartTypes.DOCUMENT);
			docdp.setName(doc.title);
			docdp.setStartTime(doc.uploadDate);
			
			docIds.put(doc.docID, docdp.getId());
		}
		return docIds;
	}
	
	public Map<Long,Long> mapDocumentsAsContributions(long salon) throws SQLException {
		Discourse discourse = discourseService.findOne(salon).get();
		DiscoursePart docsdp = discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
				"salons#" + salonID + "#docs", "salon#salonIDs", DataSourceTypes.SALON, datasetName, DiscoursePartTypes.FOLDER);
		docsdp.setName("Documents for " + discourse.getName()); 
		List<SalonDocInfo> docInfos = SalonDocInfo.getSalonDocInfo(salonID, salonDB);
		HashMap<Long,Long> docIds = new HashMap<Long,Long>(); //Old to new (salon id to ddb id)
		for (SalonDocInfo doc : docInfos) {
			Optional<Contribution> odocc = contributionService.findOneByDataSource("documents#" + doc.docID, "salon#documents", 
					datasetName);
			if (!odocc.isPresent()) {
				Contribution docc = contributionService.createTypedContribution(ContributionTypes.CONTEXT_DOCUMENT);
				DataSourceInstance contribSource = dataSourceService.createIfNotExists(
						new DataSourceInstance("documents#" + doc.docID, "salon#documents",  datasetName));
				contribSource.setSourceType(DataSourceTypes.SALON);
				dataSourceService.addSource(docc, contribSource);		
				
				
				docc.setStartTime(doc.uploadDate);
				
				Content cont = contentService.createContent();
				cont.setTitle(doc.title);
				cont.setText(doc.body);
				
				SalonUser s_user = SalonUser.getSalonUser(doc.authorID, salonDB);
				User user = userService.createOrGetUser(discourse, s_user.name, "user#" + s_user.userId,"salon#user", 
						DataSourceTypes.SALON, datasetName);	
				// connect user to discourse
				if (user.getRealname() == null ||  user.getRealname().length() == 0 || user.getRealname().startsWith("")) {
					
				}
				user.setEmail(s_user.email);
				cont.setAuthor(user);
				
				
				docc.setFirstRevision(cont);
				docc.setCurrentRevision(cont);
				contentService.save(cont);
				discoursePartService.addContributionToDiscoursePart(docc, docsdp);
				docIds.put(doc.docID, cont.getId());
			} else {
				docIds.put(doc.docID, odocc.get().getCurrentRevision().getId());
			}
		}
		return docIds;
	}


	@Deprecated
	public List<Long> mapParagraphs(long salon, long doc) {
		// TODO Auto-generated method stub
		return new ArrayList<Long>();
	}

    // Which doc number is this -- the number in ddb or in salon?
	// I need both.  What's the easiest way to look it up?  Surely not reparsing the entitySourceId.
	public Map<Long,Long> mapSalonAnnotations2DiscourseContributions(long salon, long salon_doc, long ddb_doc) throws SQLException {
		Discourse discourse = discourseService.findOne(salon).get();
		Content docc = contentService.findOne(ddb_doc).get();
		DiscoursePart annodp = discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
				"salons#" + salonID + "#annos", "salon#salonIDs", DataSourceTypes.SALON, datasetName, 
				DiscoursePartTypes.FOLDER);
		annodp.setName("Participant Annotations for " + discourse.getName());
		DiscoursePart docannodp = discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
				"documentAnnotations#" + salon_doc, "salon#documentAnnotations", DataSourceTypes.SALON, datasetName, 
				DiscoursePartTypes.FOLDER);
		docannodp.setName("Annotations for " + docc.getTitle());
		discoursePartService.createDiscoursePartRelation(annodp, docannodp, DiscoursePartRelationTypes.SUBPART);
		
		List<SalonAnnotation> sannos = SalonAnnotation.getSalonAnnotation(salon_doc, salonDB);
		Map<Long,Long> docIds = new HashMap<Long,Long>(); // Map salon annotation_id to discoursedb's contribution id
		for (SalonAnnotation sanno : sannos) {
			
			Optional<Contribution> oannoc = contributionService.findOneByDataSource("annotations#" + sanno.annotationId, "salon#annotations", 
					datasetName);
			if (!oannoc.isPresent()) {
				Contribution annoc = contributionService.createTypedContribution(ContributionTypes.POST);
				DataSourceInstance contribSource = dataSourceService.createIfNotExists(
						new DataSourceInstance("annotations#" + sanno.annotationId, "salon#annotations",  datasetName));
				contribSource.setSourceType(DataSourceTypes.SALON);
				dataSourceService.addSource(annoc, contribSource);		
				
				Optional<Contribution> parentAnno = contributionService.findOneByDataSource(
						"annotations#" + sanno.replyTo, "salon#annotations", 
						datasetName);
				if (parentAnno.isPresent()) {
					contributionService.createDiscourseRelation(parentAnno.get(), annoc, DiscourseRelationTypes.REPLY);
				}
				ContributionContext ccx = contributionService.addContextToContribution(annoc, docc);
				ccx.setBeginOffset(sanno.startChar.intValue());
				ccx.setEndOffset(sanno.endChar.intValue());
				ccx.setStartTime(sanno.time);

				annoc.setStartTime(sanno.time);
				annoc.setEndTime(sanno.modifiedTime);
				
				Content cont = contentService.createContent();
				cont.setText(sanno.commentText);
				cont.setTitle(sanno.area);
				
				SalonUser s_user = SalonUser.getSalonUser(sanno.userId, salonDB);
				User user = userService.createOrGetUser(discourse, s_user.name, "user#" + s_user.userId,"salon#user", 
						DataSourceTypes.SALON, datasetName);		
				user.setEmail(s_user.email);
				cont.setAuthor(user);
				
				annoc.setFirstRevision(cont);
				annoc.setCurrentRevision(cont);
				contentService.save(cont);
				docIds.put(sanno.annotationId, annoc.getId());
				discoursePartService.addContributionToDiscoursePart(annoc, docannodp);

			} else {
				docIds.put(sanno.annotationId, oannoc.get().getId());
			}
		}
		return docIds;
	}


	public Map<Long,Long> mapQuestions(long salonID, long doc) throws SQLException {
		Discourse discourse = discourseService.findOne(salonID).get();
		DiscoursePart docsdp = discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
				"salons#" + salonID + "#questions", "salon#salonIDs", DataSourceTypes.SALON, datasetName, DiscoursePartTypes.FOLDER);
		String docname = contributionService.findOneByDataSource("documents#" + doc, "salon#documents", datasetName)
				.get().getFirstRevision().getTitle();
		docsdp.setName("Q/A: " + docname + " (" + discourse.getName() + ")");
		List<SalonQuestion> questions = SalonQuestion.getSalonQuestions(doc, salonDB);
		HashMap<Long,Long> questionIDs = new HashMap<Long,Long>(); //Old to new (question id to ddb contribution id)
		for (SalonQuestion question : questions) {
			Optional<Contribution> odocc = contributionService.findOneByDataSource("questions#" + question.questionID, "salon#questions", 
					datasetName);
			if (!odocc.isPresent()) {
				DiscoursePart qdp = discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
						"questionFolders#" + question.questionID, "salon#questions", DataSourceTypes.SALON, datasetName, DiscoursePartTypes.FOLDER);
				qdp.setName(question.questionTitle);
				discoursePartService.createDiscoursePartRelation(docsdp,qdp,DiscoursePartRelationTypes.SUBPART);

				Contribution docc = contributionService.createTypedContribution(ContributionTypes.QUESTION);
				DataSourceInstance contribSource = dataSourceService.createIfNotExists(
						new DataSourceInstance("questions#" + question.questionID, "salon#questions",  datasetName));
				contribSource.setSourceType(DataSourceTypes.SALON);
				dataSourceService.addSource(docc, contribSource);		
				
				
				docc.setStartTime(question.createdDate);
				
				Content cont = contentService.createContent();
				cont.setTitle(question.questionTitle);
				cont.setText(question.questionText);
				
				SalonUser s_user = SalonUser.getSalonUser(question.userID, salonDB);
				User user = userService.createOrGetUser(discourse, s_user.name, "user#" + s_user.userId,"salon#user", 
						DataSourceTypes.SALON, datasetName);	
				// connect user to discourse
				user.setEmail(s_user.email);
				cont.setAuthor(user);
				
				
				docc.setFirstRevision(cont);
				docc.setCurrentRevision(cont);
				contentService.save(cont);
				discoursePartService.addContributionToDiscoursePart(docc, qdp);
				questionIDs.put(question.questionID, cont.getId());
			} else {
				questionIDs.put(question.questionID, odocc.get().getCurrentRevision().getId());
			}
		}
		return questionIDs;		
	}


	

	public void mapResponseParagraphs(long salon, long doc, long question, long response, List<Long> paras) {
		// TODO Auto-generated method stub
		
	}


	public void linkDiscussions(long salon, List<Long> discs) {
		// TODO Auto-generated method stub
		
	}


	public List<Long> mapDiscussions(long salon) {
		// TODO Auto-generated method stub
		return new ArrayList<Long>();
	}


	public HashMap<Long,Long> mapResponses(long salonID, long docID, long questionID) throws SQLException {
		Discourse discourse = discourseService.findOne(salonID).get();
		DiscoursePart qdp = discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
				"questionFolders#" + questionID, "salon#questions", DataSourceTypes.SALON, datasetName, DiscoursePartTypes.FOLDER);
		List<SalonResponse> responses = SalonResponse.getSalonResponses(questionID, salonDB);
		HashMap<Long,Long> responseIDs = new HashMap<Long,Long>(); //Old to new (question id to ddb contribution id)
		for (SalonResponse response : responses) {
			Optional<Contribution> odocc = contributionService.findOneByDataSource("responses#" + response.responseID, "salon#responses", 
					datasetName);
			if (!odocc.isPresent()) {
				Contribution docc = contributionService.createTypedContribution(ContributionTypes.RESPONSE);
				DataSourceInstance contribSource = dataSourceService.createIfNotExists(
						new DataSourceInstance("responses#" + response.responseID, "salon#responses",  datasetName));
				contribSource.setSourceType(DataSourceTypes.SALON);
				dataSourceService.addSource(docc, contribSource);		
				
				
				docc.setStartTime(response.responseMade);
				docc.setEndTime(response.responseModified);
				
				Content cont = contentService.createContent();
				cont.setTitle(response.responseTitle);
				cont.setText(response.responseText);
				
				Optional<Contribution> question = contributionService.findOneByDataSource("questions#" + questionID, "salon#questions", datasetName);
				contributionService.createDiscourseRelation(question.get(), docc, DiscourseRelationTypes.REPLY);
				
				SalonUser s_user = SalonUser.getSalonUser(response.userID, salonDB);
				User user = userService.createOrGetUser(discourse, s_user.name, "user#" + s_user.userId,"salon#user", 
						DataSourceTypes.SALON, datasetName);	
				
				user.setEmail(s_user.email);
				cont.setAuthor(user);
				
				
				
				docc.setFirstRevision(cont);
				docc.setCurrentRevision(cont);
				contentService.save(cont);
				discoursePartService.addContributionToDiscoursePart(docc, qdp);
				responseIDs.put(response.responseID, cont.getId());
			} else {
				responseIDs.put(response.responseID, odocc.get().getCurrentRevision().getId());
			}
		}
		return responseIDs;	
	}
}