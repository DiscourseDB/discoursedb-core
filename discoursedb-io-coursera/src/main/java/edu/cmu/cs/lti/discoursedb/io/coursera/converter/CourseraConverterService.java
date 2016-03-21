package edu.cmu.cs.lti.discoursedb.io.coursera.converter;

import java.sql.SQLException;
import java.util.Date;

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
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.coursera.io.CourseraDB;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Comment;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.CourseraSourceMapping;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Forum;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Post;
import edu.cmu.cs.lti.discoursedb.io.coursera.model.Thread;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class CourseraConverterService {
	
	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	private final @NonNull DiscourseService discourseService;
		
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
		Assert.notNull(database, "Database information cannot be null.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");
		Assert.hasText(discourseName, "Discourse name cannot be empty.");
		
		//initialize discourse the forums belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		log.info("Importing forum data");
		
		for(int forumid:database.getIds("forum")) {
			Forum curForum = (Forum) database.getDbEntity("forum", (long) forumid);
			
			DiscoursePart forum = 
					discoursepartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.FORUM);
			Date d = new Date(curForum.getOpentime()*1000L);
			forum.setStartTime(d);
			
			//If a forum has no name, set "NoNameForum" as its name
			forum.setName(curForum.getName().length()>0?curForum.getName():"NoNameForum");
			
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
		Assert.notNull(database, "Database information cannot be null.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");
		Assert.hasText(discourseName, "Discourse name cannot be empty.");
		
		//initialize the discourse the threads belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
	    log.info("Importing thread data");
		
	    for(int threadid: database.getIds("thread")) {
			Thread curThread = (Thread) database.getDbEntity("thread", (long) threadid);

			DiscoursePart thread = discoursepartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.THREAD);
						
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
			discoursepartService.findOneByDataSource(
							String.valueOf(curThread.getForum_id()),
							CourseraSourceMapping.ID_STR_TO_DISCOURSEPART, dataSetName).
							ifPresent(forum->discoursepartService.createDiscoursePartRelation(forum, thread, DiscoursePartRelationTypes.TALK_PAGE_HAS_DISCUSSION));			
						
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
		Assert.notNull(database, "Database information cannot be null.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");
		Assert.hasText(discourseName, "Discourse name cannot be empty.");
		
		//initialize the discourse the threads belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		log.info("Importing post data");
		
		for(int postid:database.getIds("post")){
			Post curPost = (Post) database.getDbEntity("post", (long) postid);
			
			//create new contribution for post if it doesn't already exist 
			contributionService.findOneByDataSource(String.valueOf(curPost.getId()), CourseraSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName)
				.orElseGet(()->{
							Date startTime = new Date(curPost.getPost_time()*1000L);
							
							//add content entity to database
							log.trace("Create Content entity");
							User curUser = userService.createOrGetUser(discourse, String.valueOf(curPost.getUser_id()));
							Content curContent = contentService.createContent();
							curContent.setText(curPost.getPost_text());
							curContent.setAuthor(curUser);
							curContent.setStartTime(startTime); 
							dataSourceService.addSource(curContent, new DataSourceInstance(
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
							curContribution.setStartTime(startTime);
							curContribution.setFirstRevision(curContent);
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
							discoursepartService.findOneByDataSource(
										String.valueOf(curPost.getThread_id()),
										CourseraSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD, dataSetName).
										ifPresent(dp->discoursepartService.addContributionToDiscoursePart(curContribution, dp));			

							return curContribution; //only necessary for orElseGet()					
						});			
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
		Assert.notNull(database, "Database information cannot be null.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");
		Assert.hasText(discourseName, "Discourse name cannot be empty.");
		
		//initialize the discourse the comments belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		log.info("Importing comment data");
		
		for(int commentid:database.getIds("comment")) {
			Comment curComment = (Comment) database.getDbEntity("comment", (long) commentid);
			
			//create conribution if it doesn't exist yet
			contributionService.findOneByDataSource(String.valueOf(curComment.getId()),CourseraSourceMapping.ID_STR_TO_CONTRIBUTION_COMMENT, dataSetName)
				.orElseGet(()-> {
				
					Date startTime = new Date(curComment.getPost_time()*1000L);

					//add content entity to database
				log.trace("Create Content entity");
				Content curContent = contentService.createContent();
				curContent.setText(curComment.getText());
				User curUser = userService.createOrGetUser(discourse, String.valueOf(curComment.getUser_id()));
				curContent.setAuthor(curUser);
				curContent.setStartTime(startTime);
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
				curContribution.setStartTime(startTime);
				if (curComment.getVotes() > 0)
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
				contributionService.findOneByDataSource(
						String.valueOf(curComment.getPost_id()), CourseraSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).
						ifPresent(p->contributionService.createDiscourseRelation(p, curContribution, DiscourseRelationTypes.COMMENT));				
				
				//build relation between a comment and the thread it belongs to
				discoursepartService.findOneByDataSource(
								String.valueOf(curComment.getThread_id()),
								CourseraSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD, dataSetName).
								ifPresent(t->discoursepartService.addContributionToDiscoursePart(curContribution, t));
				return curContribution; //only necessary for orElseGet()
			}
			);
		}
	}
	
}
