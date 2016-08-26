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
package edu.cmu.cs.lti.discoursedb.io.coursera.converter;

import java.sql.SQLException;
import java.util.Date;
import java.util.Optional;

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
 * This converter service maps Coursera forum data from a coursera database to
 * DiscourseDB. The connection to the coursera database is established by the
 * calling class, which has to pass a database access object to the methods in
 * this service class. Mapping methods are executed transactionally.
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 */
@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CourseraConverterService {

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	private final @NonNull DiscourseService discourseService;

	/**
	 * Maps all forum entities in forum table of the coursera database to
	 * DiscourseDB. Each forum entity is mapped to DiscourseDB as a
	 * DiscoursePart entity.
	 * 
	 * @param database
	 *            a coursera database access object, which is used to query the
	 *            coursera database
	 * @param dataSetName
	 * @param discourseName
	 * @throws SQLException
	 */

	public void mapForum(CourseraDB database, String dataSetName, String discourseName) throws SQLException {
		Assert.notNull(database, "Database information cannot be null.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");
		Assert.hasText(discourseName, "Discourse name cannot be empty.");

		// initialize discourse the forums belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);

		log.info("Importing forum data");

		for (int forumid : database.getIds("forum")) {
			Forum curForum = (Forum) database.getDbEntity("forum", (long) forumid);

			DiscoursePart forum = discoursepartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.FORUM);
			Date d = new Date(curForum.getOpentime() * 1000L);
			forum.setStartTime(d);

			// If a forum has no name, set "NoNameForum" as its name
			forum.setName(curForum.getName().length() > 0 ? curForum.getName() : "NoNameForum");

			dataSourceService.addSource(forum, new DataSourceInstance(String.valueOf(curForum.getId()),
					CourseraSourceMapping.ID_STR_TO_DISCOURSEPART, DataSourceTypes.COURSERA, dataSetName));

		}

	}

	/**
	 * Maps all thread entities in thread table of the coursera database to
	 * DiscourseDB. Each thread entity is mapped to DiscourseDB as a
	 * DiscoursePart entity.
	 * 
	 * @param database
	 *            a coursera database access object, which is used to query the
	 *            coursera database
	 * @param dataSetName
	 * @param discourseName
	 * @throws SQLException
	 */

	public void mapThread(CourseraDB database, String dataSetName, String discourseName) throws SQLException {
		Assert.notNull(database, "Database information cannot be null.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");
		Assert.hasText(discourseName, "Discourse name cannot be empty.");

		// initialize the discourse the threads belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);

		log.info("Importing thread data");

		for (int threadid : database.getIds("thread")) {
			Thread curThread = (Thread) database.getDbEntity("thread", (long) threadid);

			// only map the thread if the forum has been imported into DiscourseDB
			// The forum might have been marked as deleted while the thread was not.
			// We shouldn't import threads of deleted forums.
			discoursepartService.findOneByDataSource(String.valueOf(curThread.getForum_id()),
					CourseraSourceMapping.ID_STR_TO_DISCOURSEPART, dataSetName).ifPresent(forum -> {
						DiscoursePart thread = discoursepartService.createTypedDiscoursePart(discourse,
								DiscoursePartTypes.THREAD);

						// set time and name
						Date start = new Date(curThread.getPosted_time() * 1000L);
						Date last_update = new Date(curThread.getLast_updated_time() * 1000L);
						thread.setStartTime(start);
						thread.setEndTime(last_update);
						thread.setName(curThread.getTitle());

						// add discoursepart to database
						dataSourceService.addSource(thread,
								new DataSourceInstance(String.valueOf(curThread.getId()),
										CourseraSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD, DataSourceTypes.COURSERA,
										dataSetName));

						discoursepartService.createDiscoursePartRelation(forum, thread,
								DiscoursePartRelationTypes.TALK_PAGE_HAS_DISCUSSION);
					});
		}

	}

	/**
	 * Maps all post entities in post table of the coursera database to
	 * DiscourseDB. Each post entity is mapped to DiscourseDB as a Contribution
	 * entity. The content of each post entity is mapped to DiscourseDB as a
	 * Content entity.
	 * 
	 * @param database
	 *            a coursera database access object, which is used to query the
	 *            coursera database
	 * @param dataSetName
	 * @param discourseName
	 * @throws SQLException
	 */

	public void mapPost(CourseraDB database, String dataSetName, String discourseName) throws SQLException {
		Assert.notNull(database, "Database information cannot be null.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");
		Assert.hasText(discourseName, "Discourse name cannot be empty.");

		// initialize the discourse the threads belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);

		log.info("Importing post data");

		for (int postid : database.getIds("post")) {
			Post curPost = (Post) database.getDbEntity("post", (long) postid);

			// first check if this contribution for this post already exists in the database for some reason.
			Optional<Contribution> existingPost = contributionService.findOneByDataSource(
					String.valueOf(curPost.getId()), CourseraSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);

			// if the contrib for the post doesn't exist, import it
			if (!existingPost.isPresent()) {

				// before actually importing the contrib, check if the thread
				// actually exist.
				// the thread or forum might have been flagged as deleted and
				// thus not been imported
				discoursepartService.findOneByDataSource(String.valueOf(curPost.getThread_id()),CourseraSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD, dataSetName)
				.ifPresent(thread -> {

							Date startTime = new Date(curPost.getPost_time() * 1000L);

							// add content entity to database
							log.trace("Create Content entity");
							User curUser = userService.createOrGetUser(discourse, String.valueOf(curPost.getUser_id()));
							Content curContent = contentService.createContent();
							curContent.setText(curPost.getPost_text());
							curContent.setAuthor(curUser);
							curContent.setStartTime(startTime);
							dataSourceService.addSource(curContent,
									new DataSourceInstance(String.valueOf(curPost.getId()),
											CourseraSourceMapping.ID_STR_TO_CONTENT, DataSourceTypes.COURSERA,
											dataSetName));

							// add post contribution entity to database
							log.trace("Create Contribution entity");
							ContributionTypes mappedType = null;
							if (curPost.getOriginal() == 0)
								mappedType = ContributionTypes.POST;
							else
								mappedType = ContributionTypes.THREAD_STARTER;

							Contribution curContribution = contributionService.createTypedContribution(mappedType);
							curContribution.setStartTime(startTime);
							curContribution.setFirstRevision(curContent);
							curContribution.setCurrentRevision(curContent);
							if (curPost.getVotes() > 0)
								curContribution.setUpvotes((int) curPost.getVotes());
							else
								curContribution.setDownvotes((int) Math.abs(curPost.getVotes()));

							dataSourceService.addSource(curContribution,
									new DataSourceInstance(String.valueOf(curPost.getId()),
											CourseraSourceMapping.ID_STR_TO_CONTRIBUTION, DataSourceTypes.COURSERA,
											dataSetName));

							discoursepartService.addContributionToDiscoursePart(curContribution, thread);
						});
			}

		}
	}

	/**
	 * Maps all comment entities in comment table of the coursera database to
	 * DiscourseDB. Each comment entity is mapped to DiscourseDB as a
	 * Contribution entity. The content of each comment entity is mapped to
	 * DiscourseDB as a Content entity.
	 * 
	 * @param database
	 *            a coursera database access object, which is used to query the
	 *            coursera database
	 * @param dataSetName
	 * @param discourseName
	 * @throws SQLException
	 */

	public void mapComment(CourseraDB database, String dataSetName, String discourseName) throws SQLException {
		Assert.notNull(database, "Database information cannot be null.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");
		Assert.hasText(discourseName, "Discourse name cannot be empty.");

		// initialize the discourse the comments belong to
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);

		log.info("Importing comment data");

		for (int commentid : database.getIds("comment")) {
			Comment curComment = (Comment) database.getDbEntity("comment", (long) commentid);

			//check if contribution for this comment already exists
			Optional<Contribution> existingComment = contributionService.findOneByDataSource(
					String.valueOf(curComment.getId()), CourseraSourceMapping.ID_STR_TO_CONTRIBUTION_COMMENT,dataSetName);
			
			// create comment if it doesn't exist yet ...
			if (!existingComment.isPresent()) {
				
				//...but only if its thread exists
				discoursepartService.findOneByDataSource(String.valueOf(curComment.getThread_id()),CourseraSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD, dataSetName).
				ifPresent(thread -> {

					//...and only if its parent post exists
					contributionService
						.findOneByDataSource(String.valueOf(curComment.getPost_id()),CourseraSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName)
						.ifPresent(post -> {

										Date startTime = new Date(curComment.getPost_time() * 1000L);

										log.trace("Create Content entity");
										Content curContent = contentService.createContent();
										curContent.setText(curComment.getText());
										User curUser = userService.createOrGetUser(discourse,
												String.valueOf(curComment.getUser_id()));
										curContent.setAuthor(curUser);
										curContent.setStartTime(startTime);
										dataSourceService.addSource(curContent,
												new DataSourceInstance(String.valueOf(curComment.getId()),
														CourseraSourceMapping.ID_STR_TO_CONTENT_COMMENT,
														DataSourceTypes.COURSERA, dataSetName));

										// add contribution entity to database
										log.trace("Create Contribution entity");
										Contribution curContribution = contributionService
												.createTypedContribution(ContributionTypes.POST);
										curContribution.setCurrentRevision(curContent);
										curContribution.setFirstRevision(curContent);
										curContribution.setStartTime(startTime);
										if (curComment.getVotes() > 0)
											curContribution.setUpvotes((int) curComment.getVotes());
										else
											curContribution.setDownvotes((int) Math.abs(curComment.getVotes()));

										dataSourceService.addSource(curContribution,
												new DataSourceInstance(String.valueOf(curComment.getId()),
														CourseraSourceMapping.ID_STR_TO_CONTRIBUTION_COMMENT,
														DataSourceTypes.COURSERA, dataSetName));

										discoursepartService.addContributionToDiscoursePart(curContribution, thread);
										contributionService.createDiscourseRelation(post, curContribution,
												DiscourseRelationTypes.COMMENT);
									});
						});
			}

		}
	}

}
