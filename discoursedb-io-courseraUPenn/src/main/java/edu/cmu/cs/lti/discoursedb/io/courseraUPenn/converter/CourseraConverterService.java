package edu.cmu.cs.lti.discoursedb.io.courseraUPenn.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.user.GroupRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartInteractionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;


//@Log4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
public class CourseraConverterService {
	private static final Logger log = LogManager.getLogger(CourseraConverterService.class);

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	@Autowired private EntityManager entityManager;
	private static SimpleDateFormat sdf = new SimpleDateFormat("M/d/yy HH:mm:ss");
	private static SimpleDateFormat sdf2 = new SimpleDateFormat("M/d/yy HH:mm");
	private static SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
	private static SimpleDateFormat sdf4 = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
	private static SimpleDateFormat sdf5 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	public <T> T getProxy(Long id, Class clazz) {
		Session session = entityManager.unwrap(Session.class);
		return (T) session.get(clazz, id);
	}
	

	
	User getProxyUser(Long id) {
		Session session = entityManager.unwrap(Session.class);
		return (User) session.get(User.class, id);
	}
	
	
	
	
	static Date forgiving_date_parse(String thedate) throws ParseException {
		
		try { return	sdf.parse(thedate); } catch (ParseException pe) { }
		try { return	sdf2.parse(thedate); } catch (ParseException pe) { }
		try { return	sdf3.parse(thedate); } catch (ParseException pe) { }
		try { return	sdf4.parse(thedate); } catch (ParseException pe) { }
		try { return	sdf5.parse(thedate); } catch (ParseException pe) { }
		return	sdf6.parse(thedate); 
	}
	
	
	
	static java.util.Date dummyTime = new java.util.Date();   // For undated things, use a date to keep convos sorted
	
	public static class DataSourceInfo {
		DataSourceInfo(String descriptor, String id, String dataset, DataSourceTypes dstype, DiscoursePartTypes dptype) {
			m_descriptor = descriptor; m_id=id; m_dataset=dataset; m_dstype=dstype; m_dptype = dptype;
		}
		String m_descriptor;
		DiscoursePartTypes m_dptype;
		String m_id;
		String m_dataset;
		DataSourceTypes m_dstype;
		String index() { return m_id + "@" + m_descriptor; }
	}
	
	HashMap<String,Long> dpCache = new HashMap<String,Long>();
	DiscoursePart getDiscoursePart(Discourse discourse, DataSourceInfo dsinf) {
		
		if (dpCache.containsKey(dsinf.index())) {
			return getProxy(dpCache.get(dsinf.index()), DiscoursePart.class);
		} else {
			DiscoursePart dp = discoursepartService.createOrGetDiscoursePartByDataSource(
					discourse, dsinf.m_id, dsinf.m_descriptor, dsinf.m_dstype, dsinf.m_dataset, dsinf.m_dptype);
			dpCache.put(dsinf.index(), dp.getId());
			return dp;
		}
	}
	
	
	/*
	 * row.get("course_id"),
						discourseName, datasetName,
						"courses","course_id",row.get("course_id"),
						row.get("course_slug")
	 */
	public long mapCourse(String name, String dataset, 
			String from_file, String from_column, String native_id) {
				
		Discourse curDiscourse = discourseService.createOrGetDiscourse(name, dataset);
		return curDiscourse.getId();
	}
	public DiscoursePart unknownFolder(Discourse curDiscourse, String dataset) {
		return getDiscoursePart(curDiscourse, new DataSourceInfo("discussion_course_forums#discussion_forum_id", "unknown", "dataset", DataSourceTypes.COURSERA , 
				DiscoursePartTypes.FOLDER));
	}
	public DataSourceInfo mapCourseRevision(String revision_name, String dataset, String createdAt, Long discourseId, 
			String from_file, String from_column, String native_id) {
				
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		DataSourceInfo dsi = new DataSourceInfo(from_file+"#"+from_column, native_id, dataset, DataSourceTypes.COURSERA, DiscoursePartTypes.FOLDER);
		DiscoursePart course_rev_dp = getDiscoursePart(curDiscourse, dsi);
		course_rev_dp.setName(revision_name);
		return dsi;
	}

	public DataSourceInfo mapModule(String module_name, DataSourceInfo branch_info, String dataset, 
			long discourseId,String from_file, String from_column, String native_id) {
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		DiscoursePart parent = getDiscoursePart(curDiscourse, branch_info);
		DataSourceInfo dsi = new DataSourceInfo(from_file+"#"+from_column,native_id,dataset,DataSourceTypes.COURSERA,  DiscoursePartTypes.MODULE);
		DiscoursePart course_mod_dp = getDiscoursePart(curDiscourse,dsi);
		discoursepartService.createDiscoursePartRelation(parent, course_mod_dp, DiscoursePartRelationTypes.SUBPART);
		course_mod_dp.setName(module_name);
		return dsi;
	}
	
	
	public DataSourceInfo mapLesson(String lesson_name, DataSourceInfo module_info, 
			String dataset, long discourseId, String from_file, String from_column, String native_id) {
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		DiscoursePart parent = getDiscoursePart(curDiscourse, module_info);
		DataSourceInfo dsi = new DataSourceInfo(from_file+"#"+from_column,native_id, dataset,DataSourceTypes.COURSERA,  DiscoursePartTypes.LESSON);
		DiscoursePart course_less_dp = getDiscoursePart(curDiscourse,dsi);
		discoursepartService.createDiscoursePartRelation(parent, course_less_dp, DiscoursePartRelationTypes.SUBPART);
		course_less_dp.setName(lesson_name);
		return dsi;
	}

	
	public DataSourceInfo mapItem(String item_name, DataSourceInfo lesson_info, String dataset, long discourseId, String from_file, String from_column, String native_id) {
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		DiscoursePart parent = getDiscoursePart(curDiscourse, lesson_info);
		DataSourceInfo dsi = new DataSourceInfo(from_file+"#"+from_column,native_id, dataset,DataSourceTypes.COURSERA,  DiscoursePartTypes.LESSON_ITEM);
		DiscoursePart course_item_dp = getDiscoursePart(curDiscourse,dsi);
		discoursepartService.createDiscoursePartRelation(parent, course_item_dp, DiscoursePartRelationTypes.SUBPART);
		course_item_dp.setName(item_name);
		return dsi;
	}

	
	
	public long mapEnrollment(String userid, String dataset, long discourseId, String from_file, String from_column,
			String index) {
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		User u = userService.createOrGetUser(curDiscourse,  userid);
		dataSourceService.addSource(u,
				index, from_file + "#" + from_column, DataSourceTypes.COURSERA, dataset);
		return u.getId();
	}

	
	public DataSourceInfo mapForum(String forum_title, DataSourceInfo branch_inf, String dataset, long discourseId, String from_file, String from_column, String native_id) {
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		
		DiscoursePart parent = (branch_inf!=null)?getDiscoursePart(curDiscourse, branch_inf):
													unknownFolder(curDiscourse, dataset);
		DataSourceInfo dsi = new DataSourceInfo(from_file+"#"+from_column,native_id, dataset,DataSourceTypes.COURSERA,  DiscoursePartTypes.FORUM);
		DiscoursePart course_forum_dp = getDiscoursePart(curDiscourse,dsi);
		course_forum_dp.setName(forum_title);
		discoursepartService.createDiscoursePartRelation(parent, course_forum_dp, DiscoursePartRelationTypes.SUBPART);
		return dsi;
	}

	
	public DataSourceInfo mapQuestion(String question_title, String question_text, long user_id, DataSourceInfo forum_info, DataSourceInfo under_dp_info, 
			String createdAt, String updatedAt,
			String dataset, long discourseId, String from_file, String from_column, String native_id) {
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		DiscoursePart parent1 = getDiscoursePart(curDiscourse, forum_info);
		DiscoursePart parent2 = getDiscoursePart(curDiscourse, under_dp_info);
		DataSourceInfo dsi = new DataSourceInfo(from_file+"#"+from_column,native_id, dataset,DataSourceTypes.COURSERA,  DiscoursePartTypes.SUBFORUM);
		DiscoursePart course_q_dp = getDiscoursePart(curDiscourse,dsi);
		course_q_dp.setName(question_title);
		Date t = null;
		try { t=sdf5.parse(createdAt); } catch (Exception e) { t = dummyTime; }
		if (course_q_dp.getEndTime() == null || t.after(course_q_dp.getEndTime())) {
			course_q_dp.setEndTime(t);
		}
		if (course_q_dp.getStartTime() == null || t.before(course_q_dp.getStartTime())) {
			course_q_dp.setStartTime(t);
		}
			
		Contribution curContribution = null;
		Optional<Contribution> opt_course_q_c = contributionService.findOneByDataSource(native_id, from_file+"#"+from_column+"#contribution", dataset);
		if (!opt_course_q_c.isPresent()) {
			Content curContent = contentService.createContent();
			curContent.setText(question_text);
			curContent.setTitle(question_title);
			curContent.setAuthor(getProxyUser(user_id));
			curContent.setStartTime(t);
			dataSourceService.addSource(curContent, 
					native_id, from_file + "#" + from_column + "#content",
					DataSourceTypes.COURSERA, dataset);
			
			log.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(ContributionTypes.QUESTION);
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			curContribution.setStartTime(t);
			dataSourceService.addSource(curContribution, 
					native_id, from_file + "#" + from_column + "#contribution",
					DataSourceTypes.COURSERA, dataset);
			discoursepartService.addContributionToDiscoursePart(curContribution, course_q_dp);
		} else {
			curContribution = opt_course_q_c.get();
		}
				
		discoursepartService.createDiscoursePartRelation(parent1, course_q_dp, DiscoursePartRelationTypes.SUBPART);
		if (parent1.getId() != parent2.getId()) {
			discoursepartService.createDiscoursePartRelation(parent2, course_q_dp, DiscoursePartRelationTypes.SUBPART);
		}
		return dsi;
	}
	
	
	public Long mapAnswer(String text, DataSourceInfo question_dp_inf, long user_id, long parent_answer_id, String createdAt, String updatedAt,
			String dataset, long discourseId, String from_file, String from_column, String native_id) {
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		DiscoursePart course_q_dp = getDiscoursePart(curDiscourse, question_dp_inf);
		
		Date t = null;
		try { t=sdf5.parse(createdAt); } catch (Exception e) { t = null; }
		if (course_q_dp.getEndTime() == null || t.after(course_q_dp.getEndTime())) {
			course_q_dp.setEndTime(t);
		}
		if (course_q_dp.getStartTime() == null || t.before(course_q_dp.getStartTime())) {
			course_q_dp.setStartTime(t);
		}
			
		Contribution curContribution = null;
		Optional<Contribution> opt_course_a_c = contributionService.findOneByDataSource(native_id, from_file+"#"+from_column+"#contribution", dataset);
		if (!opt_course_a_c.isPresent()) {
			Optional<Contribution> o_parentContribution = contributionService.findOne(parent_answer_id);
			Content curContent = contentService.createContent();
			curContent.setText(text);
			curContent.setTitle("");
			curContent.setAuthor(getProxyUser(user_id));
			curContent.setStartTime(t);
			dataSourceService.addSource(curContent, 
					native_id, from_file + "#" + from_column + "#content",
					DataSourceTypes.COURSERA, dataset);
			
			log.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(ContributionTypes.RESPONSE);
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			curContribution.setStartTime(t);
			dataSourceService.addSource(curContribution, 
					native_id, from_file + "#" + from_column + "#contribution",
					DataSourceTypes.COURSERA, dataset);
			
			discoursepartService.addContributionToDiscoursePart(curContribution, course_q_dp);
			if (o_parentContribution.isPresent()) {
				contributionService.createDiscourseRelation(curContribution, o_parentContribution.get(), DiscourseRelationTypes.REPLY);
			}
		} else {
			curContribution = opt_course_a_c.get();
		}
				
		return curContribution.getId();	
	}

	
	public void mapFeedbackComment(String text, String createdAt, long user_id, String context, String dataset,
			long discourseId, String from_file, String from_column, String native_id) {
		Discourse curDiscourse = discourseService.findOne(discourseId).get();
		DiscoursePart feedback = discoursepartService.createOrGetDiscoursePartByDataSource(
				curDiscourse, "feedback", "feedback", DataSourceTypes.COURSERA, dataset, DiscoursePartTypes.FOLDER);
		feedback.setName("Course Feedback");
		
		Date t = null;
		try { t=sdf5.parse(createdAt); } catch (Exception e) { t = null; }
		if (feedback.getEndTime() == null || t.after(feedback.getEndTime())) {
			feedback.setEndTime(t);
		}
		if (feedback.getStartTime() == null || t.before(feedback.getStartTime())) {
			feedback.setStartTime(t);
		}
			
		Contribution curContribution = null;
		Optional<Contribution> opt_course_a_c = contributionService.findOneByDataSource(native_id, from_file+"#"+from_column+"#contribution", dataset);
		if (!opt_course_a_c.isPresent()) {
			Content curContent = contentService.createContent();
			curContent.setText(text);
			curContent.setTitle(context);
			curContent.setAuthor(getProxyUser(user_id));
			curContent.setStartTime(t);
			dataSourceService.addSource(curContent, 
					native_id, from_file + "#" + from_column + "#content",
					DataSourceTypes.COURSERA, dataset);
			
			log.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(ContributionTypes.FEEDBACK);
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			curContribution.setStartTime(t);
			dataSourceService.addSource(curContribution, 
					native_id, from_file + "#" + from_column + "#contribution",
					DataSourceTypes.COURSERA, dataset);
			
			discoursepartService.addContributionToDiscoursePart(curContribution, feedback);
		} else {
			curContribution = opt_course_a_c.get();
		}
				
					
	}


	
	








	
	
	
}
