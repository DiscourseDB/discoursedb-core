package edu.cmu.cs.lti.discoursedb.io.courseraUPenn.converter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.courseraUPenn.converter.CourseraConverterService.DataSourceInfo;

//import lombok.extern.log4j.Log4j;

//@Log4j
@Component
public class CourseraConverter  implements CommandLineRunner {

	private String directory;
	private String dataset;
	
	private static final Logger log = LogManager.getLogger(CourseraConverter.class);
	@Autowired private CourseraConverterService ccs;

	@Override
	public void run(String... args) throws Exception {
		Assert.isTrue(args.length==2,"Usage: CourseraConverterApplication <directory> <dataset>.");

		// This is specific enough to a single workshop
		// that I'll just hardcode stuff -- it won't be reused likely
		
		this.directory = args[0];
		this.dataset = args[1];
		log.info("Starting Coursera conversion");
		convert(this.directory, this.dataset);		
		log.info("Finished Coursera conversion");
	}
	
	private CsvSchema.Builder mkCsvSchema(String fieldlist) {
		CsvSchema.Builder builder = new CsvSchema.Builder();
		for (String colname : fieldlist.split(",")) {
			builder.addColumn(colname);
		}
		builder.setColumnSeparator(',');
		builder.setUseHeader(false);
		return builder;
	}
	
	private Iterable<Map<String,String>> csvIteratorNoHeaders(String filename, String fieldlist) throws JsonProcessingException, IOException {
		InputStream in = new FileInputStream(filename);
        MappingIterator<Map<String, String>> iterator = new CsvMapper()
                .readerFor(Map.class)
                .with(mkCsvSchema(fieldlist).build())
                .readValues(in);
        return () -> iterator;
	}
	
	private Iterable<Map<String,String>> csvIteratorExistingHeaders(String filename) throws JsonProcessingException, IOException {
		return csvIteratorExistingHeaders(filename, (char)-1);
	}
	
	private Iterable<Map<String,String>> csvIteratorExistingHeaders(String filename, char escapeChar) throws JsonProcessingException, IOException {
		//InputStream in = new FileInputStream(filename, "UTF-8");
		InputStreamReader in = new InputStreamReader(new FileInputStream(filename), "ISO-8859-1");
        MappingIterator<Map<String, String>> iterator = new CsvMapper()
                .readerFor(Map.class)
                .with(CsvSchema.emptySchema().withColumnSeparator(',').withHeader().withEscapeChar(escapeChar))
                .readValues(in);
        List<Map<String,String>> sortable = iterator.readAll();
        if (sortable.get(0).containsKey("discussion_answer_created_ts")) {
        		sortable.sort(new Comparator<Map<String,String>>() {
        		    @Override
        		    public int compare(Map<String,String> lhs, Map<String,String> rhs) {
        		        return lhs.get("discussion_answer_created_ts").compareTo(rhs.get("discussion_answer_created_ts"));
        		    }
        		} );
        }
        return () -> sortable.iterator();
	}

	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
	private void convert(String directory, String datasetName) throws ParseException, IOException {
		Map<String,Long> courseId2discourse = new HashMap<String,Long>();
		Map<String,DataSourceInfo> branchId2dp = new HashMap<String,DataSourceInfo>();
		Map<String,Long> branchId2discourse = new HashMap<String,Long>();
		Map<String,DataSourceInfo> branchModuleId2dp = new HashMap<String,DataSourceInfo>();
		Map<String,DataSourceInfo> branchLessonId2dp = new HashMap<String,DataSourceInfo>();
		Map<String,DataSourceInfo> branchItemId2dp = new HashMap<String,DataSourceInfo>();
		Map<String,Long> cuser2duser = new HashMap<String,Long>();
		Map<String,DataSourceInfo> forumId2dp = new HashMap<String,DataSourceInfo>();
		Map<String,String> forum2branch = new HashMap<String,String>();
		Map<String,DataSourceInfo> question2dp = new HashMap<String,DataSourceInfo>();
		Map<String,Long> answer2contribution = new HashMap<String,Long>();
							// need this for parent relationship
		
		
		// Courses  (become discourses)
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "courses.csv")) {
			Long id = ccs.mapCourse(row.get("course_slug"), datasetName,
					"courses","course_id",row.get("course_id"));
			courseId2discourse.put(row.get("course_id"),  id);		
			
		}
			
		log.info("branches");
		// branches
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "course_branches.csv")) {
			String branchName = row.getOrDefault("course_branch_changes_description","") 
					+ "/" + row.getOrDefault("authoring_course_branch_name","");
			if (branchName.equals("/")) { branchName = row.get("course_branch_id"); }
			Long discourseId = courseId2discourse.get(row.get("course_id"));
			branchId2discourse.put(row.get("course_branch_id"), discourseId);
			branchId2dp.put(row.get("course_branch_id"), 
					ccs.mapCourseRevision(branchName, datasetName, row.get("authoring_course_branch_created_ts"),
							discourseId, "course_branches","course_branch_id",row.get("course_branch_id")));
		}
		
		log.info("modules");
		// modules
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "course_branch_modules.csv", '\\')) {
			String index = row.get("course_branch_id")+"#"+row.get("course_module_id");
			branchModuleId2dp.put(index,
					ccs.mapModule(row.get("course_branch_module_name"), 
							branchId2dp.get(row.get("course_branch_id")),
							datasetName, branchId2discourse.get(row.get("course_branch_id")),
						"course_branch_modules","course_module_id",index));
		}
		
		log.info("lessons");
		// lessons
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "course_branch_lessons.csv")) {
			String index = row.get("course_branch_id")+"#"+row.get("course_lesson_id");
			branchLessonId2dp.put(index,
					ccs.mapLesson(row.get("course_branch_lesson_name"), 
							branchModuleId2dp.get(row.get("course_branch_id")+"#"+row.get("course_module_id")),
							datasetName, branchId2discourse.get(row.get("course_branch_id")),
						"course_branch_lessons","course_lesson_id",index));
		}
		
		log.info("items");
		// items
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "course_branch_items.csv", '\\')) {
			String index = row.get("course_branch_id")+"#"+row.get("course_item_id");
			branchItemId2dp.put(index,
					ccs.mapItem(row.get("course_branch_item_name"), 
							branchLessonId2dp.get(row.get("course_branch_id")+"#"+row.get("course_lesson_id")),
							datasetName, branchId2discourse.get(row.get("course_branch_id")),
						"course_branch_items","course_item_id",index));
		}
		
		// users
		// TODO: This requires that users not be hitched to discourses, which violates the current
		// expectations of the userService.  That's an error that should be fixed.
		/*
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "users.csv")) {
			String index = row.get("penn_user_id");
			// TODO: Could add country, state, language, gender here if needed; ignoring for now
			cuser2duser.put(row.get("penn_user_id"),
					ccs.mapUser(row.get("penn_user_id"), 
							dataset,"users", "penn_user_id", index));
		}*/
		
		log.info("users");
		// course users
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "course_memberships.csv")) {
			String index = row.get("penn_user_id") + "#" + row.get("course_id") + "#" + 
				row.get("membership_role") + "#" + row.get("course_membership_ts").toString();
			cuser2duser.put(row.get("penn_user_id"),ccs.mapEnrollment(row.get("penn_user_id"), 
							dataset, courseId2discourse.get(row.get("course_id")),"course_memberships", "all", index));
		}
		
		log.info("Forums");
		// forums
		/* discussion_forum_id,course_branch_id,discussion_course_forum_title,discussion_course_forum_description,discussion_course_forum_order
		 * "NvRF9mf7EeeixgqJj-yehA","branch~NvmdqWf7EeebgRLClU3TKA","Week 2","Discuss this week's modules here.",2
		 */
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "discussion_course_forums.csv")) {
			String index = row.get("discussion_forum_id");
			forumId2dp.put(row.get("discussion_forum_id"), 
					ccs.mapForum(row.get("discussion_course_forum_title"), 
							branchId2dp.get(row.get("course_branch_id")),
							dataset, branchId2discourse.get(row.get("course_branch_id")),"discussion_course_forums", "discussion_forum_id", index));
			forum2branch.put(row.get("discussion_forum_id"), row.get("course_branch_id"));
		}
		
		log.info("Questions");
		// questions, mapping both items and modules and forums
		/*
		 * discussion_question_id,penn_user_id,discussion_question_title,discussion_question_details,
		 *     discussion_question_context_type,course_id,course_module_id,course_item_id,
		 *     discussion_forum_id,country_cd,group_id,discussion_question_created_ts,
		 *     discussion_question_updated_ts
		 * "BDoIlxybEeeFChIDJOAcDg",ee184a7f6ba66ae5dd8680b0627acd74c21e0361,"please grade my week 2 assignment (pdf uploaded blank initially)","<co-content><text>https://www.coursera.org/learn/wharton-capstone-analytics/peer/Kdawg/strategy/review/2q8eLRwVEeepgQ59jal4IA</text><text></text></co-content>","week","NNe5CEW4EeW8ow5lHOiKYQ",,,"2foWTyj2Eea8jw6UvTi2Tw",,,2017-04-08 20:36:17.451,2017-07-21 07:42:02.216
		 */
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "discussion_questions.csv", '\\')) {
			String index = row.get("discussion_question_id");
			DataSourceInfo underDP = null;
			if (row.get("discussion_question_context_type").equals("module")) {
				underDP = branchModuleId2dp.get(forum2branch.get(row.get("discussion_forum_id")) + "#" + row.get("course_module_id"));
			} else if (row.get("discussion_question_context_type").equals("item")) {
				underDP = branchItemId2dp.get(forum2branch.get(row.get("discussion_forum_id")) + "#" + row.get("course_item_id"));
			} else if (row.get("discussion_question_context_type").equals("forum")) {
				underDP = forumId2dp.get(row.get("discussion_forum_id"));
			} else if (row.get("discussion_question_context_type").equals("week")) {
				underDP = branchId2dp.get(forum2branch.get(row.get("discussion_forum_id")));
			} else if (row.get("discussion_question_context_type").equals("promptItem")) {
				underDP = branchId2dp.get(forum2branch.get(row.get("discussion_forum_id")));
			}
			if (underDP == null) {
				log.info("Don't know where to put this question: " + index);
				underDP = ccs.mapForum("Unknown forum " + row.get("discussion_forum_id"), 
						null,
						dataset, courseId2discourse.get(row.get("course_id")),"discussion_question", "discussion_forum_id", row.get("discussion_forum_id"));
				forumId2dp.put(row.get("discussion_forum_id"), underDP);
			}
			DataSourceInfo dsiq = new DataSourceInfo("discussion_question#discussion_question_id",index, dataset,DataSourceTypes.COURSERA,  DiscoursePartTypes.SUBFORUM);
			question2dp.put(row.get("discussion_question_id"), dsiq);
			answer2contribution.put(row.get("discussion_question_id"), 
					ccs.mapQuestion(row.get("discussion_question_title"), 
							row.get("discussion_question_details"),
							cuser2duser.get(row.get("penn_user_id")),
							forumId2dp.get(row.get("discussion_forum_id")),
							underDP,
							row.get("discussion_question_created_ts"),
							row.get("discussion_question_updated_ts"),
							dataset, courseId2discourse.get(row.get("course_id")),"discussion_question", "discussion_question_id", index, dsiq));
		}
		
		// TODO: votes and following
		
		log.info("Answers");
		// answers
		/*
		 * discussion_answer_id,penn_user_id,course_id,discussion_answer_content,discussion_question_id,
		 *      discussion_answer_parent_discussion_answer_id,discussion_answer_created_ts,discussion_answer_updated_ts
		 * "mzpGPNCZEeaJrBIAwnTPnA",cfafa784655954b1787d4f265175e4ebcdc3779f,"NNe5CEW4EeW8ow5lHOiKYQ",
		 *      "<co-content><text>Hi my name Yunan from Indonesia, </text><text>I am working in Risk management 
		 *      field.</text><text>I am excited to learn in this Capstone project.</text></co-content>",
		 *      "crf1E5c4EeafQAoOsWxOnA",,2017-01-02 03:14:43.424,2017-01-02 03:14:43.424
		 */
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "discussion_answers.csv", '\\')) {
			String index = row.get("discussion_answer_id");
			DataSourceInfo question = question2dp.getOrDefault(row.get("discussion_question_id"),null);
			DataSourceInfo forum = forumId2dp.getOrDefault(row.get("discussion_forum_id"),
					new DataSourceInfo("discussion_answer#discussion_forum_id", row.getOrDefault("discussion_forum_id","(empty)"), dataset, 
							DataSourceTypes.COURSERA, DiscoursePartTypes.FORUM));
			if (question == null) {
				question = new DataSourceInfo("discussion_question#discussion_question_id",index, dataset,DataSourceTypes.COURSERA,  DiscoursePartTypes.SUBFORUM);
				question2dp.put(row.get("discussion_question_id"), question);
				answer2contribution.put(row.get("discussion_question_id"), 
				  ccs.mapQuestion("(Missing question)", 
						"(Missing question)",
						cuser2duser.get(row.get("penn_user_id")),
						forum,forum,
						row.get("discussion_answer_created_ts"),
						row.get("discussion_answer_updated_ts"),
						dataset, courseId2discourse.get(row.get("course_id")),"discussion_answer", "discussion_question_id", index, question));
			}
			String parent = row.get("discussion_answer_parent_discussion_answer_id");
			Long parent_cont = answer2contribution.getOrDefault(parent, -1L);
			if (parent_cont == -1L) {
				parent = row.get("discussion_question_id"); 
				parent_cont =  answer2contribution.getOrDefault(parent, -1L); 
			}
			answer2contribution.put(row.get("discussion_answer_id"), 
					ccs.mapAnswer(row.get("discussion_answer_content"), 
							question,
							cuser2duser.get(row.get("penn_user_id")),
							parent_cont,
							row.get("discussion_answer_created_ts"),
							row.get("discussion_answer_updated_ts"),
							dataset,courseId2discourse.get(row.get("course_id")), "discussion_answers", "discussion_answer_id", index));
			forum2branch.put(row.get("discussion_forum_id"), row.get("course_branch_id"));
			
		}
		
		// TODO answer votes
		// TODO answer following actions
		
		log.info("Feedback");
		// feedback/course
		/*
		 * course_id,feedback_system,penn_user_id,feedback_category,feedback_text,feedback_ts
		 * "NNe5CEW4EeW8ow5lHOiKYQ","NPS_FIRST_WEEK",32497181d76ad6cbe24251f4a9626f00272004ec,"NPS_REASON","Bla bla bla",2016-04-27 17:46:38.787
		 */
		HashSet<String> checkunique = new HashSet<String>();
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "feedback_course_comments.csv", '\\')) {
			String index = row.get("penn_user_id") + "#course#" + row.get("feedback_ts");
			if (checkunique.contains(index)) {
				throw new RuntimeException("User id and time aren't unique indicators of feedback." + index);
			}
			ccs.mapFeedbackComment(row.get("feedback_text"),
					row.get("feedback_ts"),
					cuser2duser.get(row.get("penn_user_id")),
					"Course feedback: " + row.get("feedback_system"),   // Shove this under this DP
					dataset,courseId2discourse.get(row.get("course_id")), "feedback_course_comments", "user_and_timestamp", index);
		}
		
		// feedback/item
		/*
		 * course_id,course_item_id,feedback_unit_id,feedback_unit_type,feedback_system,detailed_context,penn_user_id,
		 *            feedback_category,feedback_text,feedback_ts,feedback_active
		 * "NNe5CEW4EeW8ow5lHOiKYQ","79FOF","uDd7SgGUEea8PhJiK87Ztw@5","peerAssignment","FLAG",
		 *           "{\"typeName\":\"peerAssignmentContext\",\"definition\":{\"courseId\":\"NNe5CEW4EeW8ow5lHOiKYQ\",
		 *                   \"itemId\":\"79FOF\",\"assignmentId\":\"uDd7SgGUEea8PhJiK87Ztw@5\"}}",
		 *          b9a08ec7e2d645e29b24830f4c3dc5a632263c2d,
		 * 			"content","BLA BLA BLA",2016-05-22 20:46:44.638,t
		 */
		for (Map<String,String> row : csvIteratorExistingHeaders(
				directory + "feedback_item_comments.csv", '\\')) {
			String index = row.get("penn_user_id") + "#item#" + row.get("feedback_ts");
			if (checkunique.contains(index)) {
				throw new RuntimeException("User id and time aren't unique indicators of feedback." + index);
			}
			ccs.mapFeedbackComment(row.get("feedback_text"),
					row.get("feedback_ts"),
					cuser2duser.get(row.get("penn_user_id")),
					"Item feedback: " + row.get("feedback_system") + " " +
					    row.get("feedback_unit_type") + ":" + row.get("feedback_unit_id") + " " + row.get("detailed_context")
					    + "  Category:" + row.get("feedback_category")
					
					,   // Shove this under this DP
					dataset,courseId2discourse.get(row.get("course_id")), "feedback_item_comments", "user_and_timestamp", index);
		}
	}
}