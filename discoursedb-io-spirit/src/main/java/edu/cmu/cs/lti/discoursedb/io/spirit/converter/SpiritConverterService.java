/*******************************************************************************
 * Copyright (C)  2015 - 2017  Carnegie Mellon University
 * Author:
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
package edu.cmu.cs.lti.discoursedb.io.spirit.converter;

import java.sql.SQLException;
import java.util.List;
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
import edu.cmu.cs.lti.discoursedb.io.spirit.io.SpiritDAO;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.Category;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.Comment;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.SpiritSourceMapping;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.Topic;
import edu.cmu.cs.lti.discoursedb.io.spirit.model.UserProfile;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class SpiritConverterService {
    private final @NonNull DataSourceService dataSourceService;
    private final @NonNull UserService userService;
    private final @NonNull ContentService contentService;
    private final @NonNull ContributionService contributionService;
    private final @NonNull DiscoursePartService discoursepartService;
    private final @NonNull DiscourseService discourseService;

    @Autowired
    private SpiritDAO database;

    public void map(String dataSetName, String discourseName) throws SQLException {
        Assert.notNull(database,      "Database information cannot be null.");
        Assert.hasText(dataSetName,   "Dataset name cannot be empty.");
        Assert.hasText(discourseName, "Discourse name cannot be empty.");

        Discourse discourse = discourseService.createOrGetDiscourse(discourseName);

        /*
         * Phase 1: Read through forum data from database and map all entities
         *
         * In spirit a Forum is referred to as a Category. Categories may have a parent category.
         */
        mapForum(discourse, dataSetName, discourseName);

        relateCategories(discourse, dataSetName, discourseName);

        /*
         * Phase 2: Read through thread data from database and map all entities
         *
         * In spirit a Thread is referred to as a Topic. Topics belong to a Category.
         */
        mapThread(discourse, dataSetName, discourseName);

        /*
         * Phase 3: Read through post data from database and map all entities
         */
        mapPost(discourse, dataSetName, discourseName);

        /*
         * Phase 4: Read through comment data from database and map all entities
         */
        relatePosts(discourse, dataSetName, discourseName);
    }

    /**
     * Maps all forum entities in forum table of the Spirit database to
     * DiscourseDB. Each forum entity is mapped to DiscourseDB as a
     * DiscoursePart entity.
     *
     * @param discourse
     * @param dataSetName
     * @param discourseName
     * @throws SQLException
     */
    private void mapForum(Discourse discourse, String dataSetName, String discourseName) throws SQLException {
        log.info("Importing Category (forum) data");

        for (Category category : database.getCategories()) {

            if (category.isClosed() || category.isRemoved()) {
                continue;
            }

            DiscoursePartTypes forumType = (category.getParentCategory() == null)
                    ? DiscoursePartTypes.FORUM
                    : DiscoursePartTypes.SUBFORUM;

            String forumName = (category.getTitle().length() > 0)
                    ? category.getTitle()
                    : "NoNameForum";

            DiscoursePart forum = discoursepartService.createOrGetTypedDiscoursePart(discourse, forumType);

            // TODO - Add creation data column for Categories
            forum.setStartTime(category.getReindexAt());

            forum.setName(forumName);

            log.info("Attempting to add Source " + forum.toString());

            dataSourceService.addSource(
                    forum,
                    new DataSourceInstance(String.valueOf(category.getId()),
                            SpiritSourceMapping.ID_STR_TO_DISCOURSEPART,
                            DataSourceTypes.SPIRIT,
                            dataSetName));
        }
    }

    /**
     * Maps all thread entities in thread table of the Spirit database to
     * DiscourseDB. Each thread entity is mapped to DiscourseDB as a
     * DiscoursePart entity.
     *
     * @param discourse
     * @param dataSetName
     * @param discourseName
     * @throws SQLException
     */
    private void mapThread(Discourse discourse, String dataSetName, String discourseName) throws SQLException {
        log.info("Importing Topic (thread) data");

        for (Topic topic : database.getTopics()) {

            if (topic.isClosed() || topic.isRemoved()) {
                continue;
            }

            // Only map the thread if the forum has been imported into DiscourseDB
            discoursepartService.findOneByDataSource(
                    String.valueOf(topic.getCategoryId()),
                    SpiritSourceMapping.ID_STR_TO_DISCOURSEPART,
                    dataSetName)
            .ifPresent(forum -> {
                        DiscoursePart thread = discoursepartService.createOrGetTypedDiscoursePart(discourse, DiscoursePartTypes.THREAD);

                        thread.setStartTime(topic.getDate());
                        thread.setEndTime(topic.getLastActive());
                        thread.setName(topic.getTitle());

                        dataSourceService.addSource(
                                thread,
                                new DataSourceInstance(
                                        String.valueOf(topic.getId()),
                                        SpiritSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD,
                                        DataSourceTypes.SPIRIT,
                                        dataSetName));

                        log.info("Relating forum " + forum.toString() + " and thread " + thread.toString());

                        discoursepartService.createDiscoursePartRelation(
                                forum,
                                thread,
                                DiscoursePartRelationTypes.SUBPART);
                    });
        }
    }

    /**
     * Maps all post entities in post table of the Spirit database to
     * DiscourseDB. Each post entity is mapped to DiscourseDB as a Contribution
     * entity. The content of each post entity is mapped to DiscourseDB as a
     * Content entity.
     *
     * @param discourse
     * @param dataSetName
     * @param discourseName
     * @throws SQLException
     */
    private void mapPost(Discourse discourse, String dataSetName, String discourseName) throws SQLException {
        log.info("Importing Comment (post) data");

        for (Topic topic : database.getTopics()) {
            if (topic.isClosed() || topic.isRemoved()) {
                continue;
            }

            List<Comment> comments = database.getCommentsForTopicByDate(topic.getId());

            if (comments == null || comments.size() <= 0) {
                continue;
            }

            // In Spirit the original post for a Topic is the comment with the earliest
            // creation timestamp.
            Comment originalPostComment = comments.get(0);

            for (Comment comment : comments) {
                // first check if this contribution for this post already exists in the database for some reason.
                Optional<Contribution> existingPost =
                        contributionService.findOneByDataSource(
                        String.valueOf(comment.getId()),
                        SpiritSourceMapping.ID_STR_TO_CONTRIBUTION,
                        dataSetName);

                // If the contrib for the post doesn't exist, import it
                if (!existingPost.isPresent()) {

                    // before actually importing the contrib, check if the thread
                    // actually exist.
                    // the thread or forum might have been flagged as deleted and
                    // thus not been imported
                    discoursepartService.findOneByDataSource(
                            String.valueOf(comment.getTopicId()),
                            SpiritSourceMapping.ID_STR_TO_DISCOURSEPART_THREAD,
                            dataSetName)
                    .ifPresent(thread -> {
                                // add content entity to database
                                log.info("Create Content entity");

                                UserProfile user = database.getUserByUserId(comment.getUserId());

                                User currentUser = userService.createOrGetUser(discourse,
                                        user.getSlug(),
                                        String.valueOf(user.getId()),
                                        SpiritSourceMapping.USER_TO_USER,
                                        DataSourceTypes.SPIRIT,
                                        dataSetName);

                                Content currentContent = contentService.createContent();
                                currentContent.setText(comment.getComment());
                                currentContent.setAuthor(currentUser);
                                currentContent.setStartTime(comment.getDate());

                                dataSourceService.addSource(
                                        currentContent,
                                        new DataSourceInstance(
                                                String.valueOf(comment.getId()),
                                                SpiritSourceMapping.ID_STR_TO_CONTENT,
                                                DataSourceTypes.SPIRIT,
                                                dataSetName));

                                log.info("Adding source for content " + currentContent.toString());

                                // add post contribution entity to database
                                log.info("Create Contribution entity");

                                ContributionTypes mappedType = (comment.getId() == originalPostComment.getId()) ?
                                        ContributionTypes.THREAD_STARTER :
                                        ContributionTypes.POST;

                                Contribution currentContribution = contributionService.createTypedContribution(mappedType);
                                currentContribution.setStartTime(comment.getDate());
                                currentContribution.setFirstRevision(currentContent);
                                currentContribution.setCurrentRevision(currentContent);

                                // TODO - Spirit has no concept of downvotes. Possible add this feature?
                                // currentContribution.setDownvotes(-1);
                                currentContribution.setUpvotes(comment.getLikesCount());

                                dataSourceService.addSource(
                                        currentContribution,
                                        new DataSourceInstance(
                                                String.valueOf(comment.getId()),
                                                SpiritSourceMapping.ID_STR_TO_CONTRIBUTION,
                                                DataSourceTypes.SPIRIT,
                                                dataSetName));

                                discoursepartService.addContributionToDiscoursePart(currentContribution, thread);

                                log.info("Adding source for contribution " + currentContribution.toString() + ". And adding contribution to thread " + thread.toString());
                            });
                }
            }
        }
    }

    private void relatePosts(Discourse discourse, String dataSetName, String discourseName) throws SQLException {
        log.info("Importing Comment (post) data");
        for (Topic topic : database.getTopics()) {
            List<Comment> comments = database.getCommentsForTopicByDate(topic.getId());
            if (comments == null || comments.size() <= 0) {
                continue;
            }

            // In Spirit the original post for a Topic is the comment with the earliest
            // creation timestamp.
            Comment originalPostComment = comments.remove(0);

            for (Comment comment : comments) {
                Optional<Contribution> existingComment = contributionService.findOneByDataSource(
                        String.valueOf(comment.getId()), SpiritSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);

                Optional<Contribution> existingOriginalPostComment = contributionService.findOneByDataSource(
                        String.valueOf(originalPostComment.getId()), SpiritSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);

                if (existingComment.isPresent() && existingOriginalPostComment.isPresent()) {
                    log.info("Creating relation between:\n" + existingOriginalPostComment.get() + "\n" + existingComment.get());

                    contributionService.createDiscourseRelation(existingComment.get(), existingOriginalPostComment.get(),
                            DiscourseRelationTypes.COMMENT);
                }
            }
        }
    }

    /**
     *
     * @param discourse
     * @param dataSetName
     * @param discourseName
     * @throws SQLException
     */
    private void relateCategories(Discourse discourse, String dataSetName, String discourseName) throws SQLException {
        log.info("Importing Comment (post) data");
        for (Category category : database.getCategories()) {
            if (category == null || category.getParentCategory() == null) {
                continue;
            }

            Category parentCategory = category.getParentCategory();

            DiscoursePartTypes forumType = getForumType(category);
            DiscoursePartTypes parentForumType = getForumType(parentCategory);

            DiscoursePart categoryDiscoursePart =
                    discoursepartService.createOrGetDiscoursePartByDataSource(
                            discourse,
                            String.valueOf(category.getId()),
                            SpiritSourceMapping.ID_STR_TO_DISCOURSEPART,
                            DataSourceTypes.SPIRIT,
                            dataSetName,
                            forumType);

            DiscoursePart parentCategoryDiscoursePart =
                    discoursepartService.createOrGetDiscoursePartByDataSource(
                            discourse,
                            String.valueOf(parentCategory.getId()),
                            SpiritSourceMapping.ID_STR_TO_DISCOURSEPART,
                            DataSourceTypes.SPIRIT,
                            dataSetName,
                            parentForumType);

            discoursepartService.createDiscoursePartRelation(
                    categoryDiscoursePart,
                    parentCategoryDiscoursePart,
                    DiscoursePartRelationTypes.SUBPART);
        }
    }

    private DiscoursePartTypes getForumType(Category category) {
        return (category.getParentCategory() == null)
                ? DiscoursePartTypes.FORUM
                : DiscoursePartTypes.SUBFORUM;
    }
}
