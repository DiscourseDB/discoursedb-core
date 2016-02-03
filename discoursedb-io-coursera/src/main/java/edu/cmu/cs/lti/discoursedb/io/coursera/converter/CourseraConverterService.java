package edu.cmu.cs.lti.discoursedb.io.coursera.converter;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.coursera.io.CourseraDB;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Comment;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.CourseraSourceMapping;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Forum;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Post;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Thread;
import lombok.extern.log4j.Log4j;

/**
 * This converter service maps Coursera forum data from a coursera database
 * to DiscourseDB. The connection to the coursera database is established by the
 * calling class, which has to pass a database access object to the methods in
 * this service class. Mapping methods are executed transactionally.
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 */
@Log4j
@Service
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
public class CourseraConverterService {
	
	@Autowired
	private DataSourceService dataSourceService;
	@Autowired
	private UserService userService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private ContributionService contributionService;
	@Autowired
	private DiscoursePartService discoursepartService;
	@Autowired
	private DiscourseService discourseService;
		
	/**
	 * Maps all forum entities in forum table of the coursera database to DiscourseDB.
	 * Each forum entity is mapped to DiscourseDB as a DiscoursePart entity.
	 * 
	 * @param database
	 *          a coursera database access object, which is used to query the coursera database
	 * @param dataSetName
	 * @param discourseName
	 * @throws SQLException
	 */
	
	public void mapForum(CourseraDB database, String dataSetName, String discourseName) throws SQLException {
		
		//initialize discourse the forums belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		//get the list of all forum ids in the source database
		ArrayList<Integer> forumIds = (ArrayList<Integer>) database.getIds("forum");
		
		log.info("Importing forum data");
		
		for(int i=0;i<forumIds.size();i++) {
			
			int forumid = forumIds.get(i);
			Forum curForum = (Forum) database.getDbEntity("forum", (long) forumid);
			
			DiscoursePart forum = 
					discoursepartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.FORUM);
			Date d = new Date(curForum.getOpentime()*1000L);
			forum.setStartTime(d);
			
			//If a forum has no name, set "NoNameForum" as its name
			if(curForum.getName().length()==0)
				curForum.setName("NoNameForum");
			forum.setName(curForum.getName());
			
			dataSourceService.addSource(forum, new DataSourceInstance(
					String.valueOf(curForum.getId()), 
					CourseraSourceMapping.ID_STR_TO_DISCOURSEPART, 
					DataSourceTypes.COURSERA, 
					dataSetName));
			
		}
		
	}
	
	/**
	 * Maps all thread entities in thread table of the coursera database to DiscourseDB.
	 * Each thread entity is mapped to DiscourseDB as a DiscoursePart entity.
	 * 
	 * @param database
	 *          a coursera database access object, which is used to query the coursera database
	 * @param dataSetName
	 * @param discourseName
	 * @throws SQLException
	 */
	
	public void mapThread(CourseraDB database, String dataSetName, String discourseName) throws SQLException {
		
		//initialize the discourse the threads belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		//get the list of all thread ids in the source database
	    ArrayList<Integer> threadIds = (ArrayList<Integer>) database.getIds("thread");
	    
	    log.info("Importing thread data");
		
		for(int i=0;i<threadIds.size();i++) {
			int threadid = threadIds.get(i);
			Thread curThread = (Thread) database.getDbEntity("thread", (long) threadid);

			//A few threads have very long title (longer than 255)
			//These threads are skipped
			//TODO OF: check if we should do that. maybe the range of the title field should be extended 
			if(curThread.getTitle().length()>=255)
				continue;			
			
			DiscoursePart thread = 
					discoursepartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.THREAD);
						
			//set time and name
			Date start = new Date(curThread.getPosted_time()*1000L);
			Date last_update = new Date(curThread.getLast_updated_time()*1000L);
			thread.setStartTime(start);
			thread.setEndTime(last_update);
			thread.setName(curThread.getTitle());
			
			//add discoursepart to database
			dataSourceService.addSource(thread, new DataSourceInstance(
					String.valueOf(curThread.getId()), 
					CourseraSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD, 
					DataSourceTypes.COURSERA, 
					dataSetName));
			
			//build relation between a thread and the forum it belongs to
			Optional<DiscoursePart> existingForum = 
					discoursepartService.findOneByDataSource(
							String.valueOf(curThread.getForum_id()),
							CourseraSourceMapping.ID_STR_TO_DISCOURSEPART, 
							dataSetName);
			
			if(existingForum.isPresent())
				discoursepartService.createDiscoursePartRelation(
						existingForum.get(), thread, DiscoursePartRelationTypes.TALK_PAGE_HAS_DISCUSSION);
			
		}
		
	}
	
	/**
	 * Maps all post entities in post table of the coursera database to DiscourseDB.
	 * Each post entity is mapped to DiscourseDB as a Contribution entity.
	 * The content of each post entity is mapped to DiscourseDB as a Content entity.
	 * 
	 * @param database
	 *          a coursera database access object, which is used to query the coursera database
	 * @param dataSetName
	 * @param discourseName
	 * @throws SQLException
	 */
	
	public void mapPost(CourseraDB database, String dataSetName, String discourseName) throws SQLException {
		
		//initialize the discourse the threads belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		//get the list of all post ids in the source database
		ArrayList<Integer> postIds = (ArrayList<Integer>) database.getIds("post");
		
		log.info("Importing post data");
		
		for(int i=0;i<postIds.size();i++) {
			int postid = postIds.get(i);
			Post curPost = (Post) database.getDbEntity("post", (long) postid);
			
			//if a post is already in discoursedb, then it is skipped
			Optional<Contribution> existingContribution = 
					contributionService.findOneByDataSource(
						String.valueOf(curPost.getId()),
						CourseraSourceMapping.ID_STR_TO_CONTRIBUTION, 
						dataSetName);
			if(existingContribution.isPresent())
				continue;
			
			Date strat = new Date(curPost.getPost_time()*1000L);
				
			//add content entity to database
			log.trace("Create Content entity");
			User curUser = userService.createOrGetUser(discourse, String.valueOf(curPost.getUser_id()));
			Content curContent = contentService.createContent();
			curContent.setText(curPost.getPost_text());
			curContent.setAuthor(curUser);
			curContent.setStartTime(strat); 
			dataSourceService.addSource(
					curContent, new DataSourceInstance(
							String.valueOf(curPost.getId()), 
							CourseraSourceMapping.ID_STR_TO_CONTENT, 
							DataSourceTypes.COURSERA, dataSetName));
				
			//add post contribution entity to database
			log.trace("Create Contribution entity");
			ContributionTypes mappedType = null;
			if(curPost.getOriginal()==0)
				mappedType = ContributionTypes.POST;
			else
				mappedType = ContributionTypes.THREAD_STARTER;
				
			Contribution curContribution = contributionService.createTypedContribution(mappedType);
			curContribution.setStartTime(strat);
			curContribution.setCurrentRevision(curContent);
			if(curPost.getVotes()>0)
				curContribution.setUpvotes((int) curPost.getVotes());
			else
				curContribution.setDownvotes((int) Math.abs(curPost.getVotes()));
				
			dataSourceService.addSource(curContribution,
					new DataSourceInstance(
						String.valueOf(curPost.getId()), 
						CourseraSourceMapping.ID_STR_TO_CONTRIBUTION, 
						DataSourceTypes.BAZAAR, dataSetName));
			
			//build relation between a post and the thread it belongs to
			Optional<DiscoursePart> existingThread = 
					discoursepartService.findOneByDataSource(
						String.valueOf(curPost.getThread_id()),
						CourseraSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD, 
						dataSetName);
			
			if(existingThread.isPresent())
				discoursepartService.addContributionToDiscoursePart(curContribution, existingThread.get());
		}			
	}
	
	/**
	 * Maps all comment entities in comment table of the coursera database to DiscourseDB.
	 * Each comment entity is mapped to DiscourseDB as a Contribution entity.
	 * The content of each comment entity is mapped to DiscourseDB as a Content entity.
	 * 
	 * @param database
	 *          a coursera database access object, which is used to query the coursera database
	 * @param dataSetName
	 * @param discourseName
	 * @throws SQLException
	 */
	
	public void mapComment(CourseraDB database, String dataSetName, String discourseName) throws SQLException {
		
		//initialize the discourse the comments belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		//get the list of all comment ids in the source database
		ArrayList<Integer> commentIds = (ArrayList<Integer>) database.getIds("comment");
		
		log.info("Importing comment data");
		
		for(int i=0;i<commentIds.size();i++) {
			int commentid = commentIds.get(i);
			Comment curComment = (Comment) database.getDbEntity("comment", (long) commentid);
			
			Optional<Contribution> existingContribution = 
					contributionService.findOneByDataSource(
							String.valueOf(curComment.getId()),
							CourseraSourceMapping.ID_STR_TO_CONTRIBUTION_COMMENT, 
							dataSetName);
			
			if(!existingContribution.isPresent()) {
				Date start = new Date(curComment.getPost_time()*1000L);
				//add content entity to database
				log.trace("Create Content entity");
				Content curContent = contentService.createContent();
				curContent.setText(curComment.getText());
				User curUser = userService.createOrGetUser(discourse, String.valueOf(curComment.getUser_id()));
				curContent.setAuthor(curUser);
				curContent.setStartTime(start);
				dataSourceService.addSource(
						curContent, new DataSourceInstance(
								String.valueOf(curComment.getId()), 
								CourseraSourceMapping.ID_STR_TO_CONTENT_COMMENT, 
								DataSourceTypes.COURSERA, 
								dataSetName));
				
				
				//add contribution entity to database
				log.trace("Create Contribution entity");
				Contribution curContribution = contributionService.createTypedContribution(ContributionTypes.POST);
				curContribution.setCurrentRevision(curContent);
				curContribution.setFirstRevision(curContent);
				curContribution.setStartTime(start);
				if(curComment.getVotes()>0)
					curContribution.setUpvotes((int) curComment.getVotes());
				else
					curContribution.setDownvotes((int) Math.abs(curComment.getVotes()));
				
				dataSourceService.addSource(curContribution,
						new DataSourceInstance(
								String.valueOf(curComment.getId()), 
								CourseraSourceMapping.ID_STR_TO_CONTRIBUTION_COMMENT, 
								DataSourceTypes.COURSERA, 
								dataSetName));
				
				//build relation between a comment and the post it belongs to
				Optional<Contribution> post = contributionService.findOneByDataSource(
						String.valueOf(curComment.getPost_id()), 
						CourseraSourceMapping.ID_STR_TO_CONTRIBUTION, 
						dataSetName);				
				if(post.isPresent()) 
					contributionService.createDiscourseRelation(
							post.get(), curContribution, DiscourseRelationTypes.COMMENT);
				
				//build relation between a comment and the thread it belongs to
				Optional<DiscoursePart> existingThread = 
						discoursepartService.findOneByDataSource(
								String.valueOf(curComment.getThread_id()),
								CourseraSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD, 
								dataSetName);
				if(existingThread.isPresent()) {
					discoursepartService.addContributionToDiscoursePart(curContribution, existingThread.get());
				}
			}
		}
	}
	
}
