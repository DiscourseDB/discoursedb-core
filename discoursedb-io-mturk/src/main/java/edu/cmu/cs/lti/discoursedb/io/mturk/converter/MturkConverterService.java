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
package edu.cmu.cs.lti.discoursedb.io.mturk.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.lang.time.DateUtils;
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

/**
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Log4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
public class MturkConverterService {
	
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
	

	public long mapUser(String name, String discourseName, String dataset, 
			String from_file, String from_column, String native_id) {
				
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		//groupService.addToGroup(curUser, "Team " + team);
		//groupService.addToGroup(curUser, "Group " + group);
		//groupService.addToGroup(curUser, "Experiment " + experiment);
		
		User curUser = userService.createOrGetUser(curDiscourse, name);
		dataSourceService.addSource(curUser, new DataSourceInstance(
				native_id, from_file + "#" + from_column, DataSourceTypes.BAZAAR, dataset));
		return curUser.getId();
	}
	
	User getProxyUser(Long id) {
		Session session = entityManager.unwrap(Session.class);
		return (User) session.get(User.class, id);
	}
	
	
	private Map<Long,Long> discussion_source2ddb = new HashMap<Long,Long>();
	Contribution getProxyContributionBySourceId(long id) {
		Session session = entityManager.unwrap(Session.class);
		if (!discussion_source2ddb.containsKey(id)) {
			return null;
		}
		return (Contribution) session.get(Contribution.class, discussion_source2ddb.get(id)); 
	}
	
	/**
	 * Maps a discussion forum message to DiscourseDB
	 * 
	 * @param subj the message subject
	 * @param text the message text
	 * @param forum_uid
	 * @param thread_uid
	 * @param group
	 * @param team
	 * @param author the message author (discoursedb user id number)
	 * @param reply_to  the discoursed_db contribution this replies to (or zero)
	 * @param discourse_name
	 * @param dataset_name
	 * @param source_file_name
	 * @param source_column_name
	 * @param source_unique_index
	 * 
	 * @returns the contribution Id written to the database
	 */
	public Long mapDiscussionPost(String subj, String text, 
			String forum_uid, String thread_uid, String group, String team,
			long author, String when,
			long reply_to, String discourse_name, String dataset_name,
			String source_file_name, String source_column_name, String source_unique_index) {
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourse_name);
		 
		User proxyUser = getProxyUser(author);
		ContributionTypes mappedType = null;
		if (reply_to == 0L) {
			mappedType = ContributionTypes.POST;
		} else {
			mappedType = ContributionTypes.RESPONSE;			
		}
		
		log.trace("Create Content entity");
		Content curContent = contentService.createContent();
		curContent.setText(text);
		curContent.setTitle(subj);
		curContent.setAuthor(proxyUser);
		dataSourceService.addSource(curContent, new DataSourceInstance(
				source_unique_index, source_file_name + "#" + source_column_name+ "(content)",
				DataSourceTypes.BAZAAR, dataset_name));
		
		log.trace("Create Contribution entity");
		Contribution curContribution = contributionService.createTypedContribution(mappedType);
		curContribution.setCurrentRevision(curContent);
		curContribution.setFirstRevision(curContent);
		dataSourceService.addSource(curContribution, new DataSourceInstance(
				source_unique_index, source_file_name + "#" + source_column_name + "(contribution)",
				DataSourceTypes.BAZAAR, dataset_name));
		discussion_source2ddb.put(Long.valueOf(source_unique_index), curContribution.getId());
		
		
		DiscoursePart thread = discoursepartService.createOrGetDiscoursePartByDataSource(curDiscourse, 
				thread_uid, source_file_name + "#thread_uid", DataSourceTypes.BAZAAR, dataset_name, DiscoursePartTypes.THREAD);
		DiscoursePart forum = discoursepartService.createOrGetDiscoursePartByDataSource(curDiscourse, 
				forum_uid, source_file_name + "#forum_uid", DataSourceTypes.BAZAAR, dataset_name, DiscoursePartTypes.FORUM);
		discoursepartService.createDiscoursePartRelation(forum, thread, DiscoursePartRelationTypes.SUBPART);
		if (!forum.getName().equals("dummy_name") && ! forum.getName().equals( forumDpName(forum_uid))) {
			System.out.println("Changing forum " + forum_uid + " name from " + forum.getName() + " to " +forumDpName(forum_uid) + " in post " + source_unique_index );
		}
		forum.setName(forumDpName(forum_uid));
		if (reply_to == 0) {
			if (!thread.getName().equals("dummy_name") && !thread.getName().equals(threadDpName(forum_uid, subj))) {
				System.out.println("Changing thread " + thread_uid + " name from " + thread.getName() + " to " +threadDpName(forum_uid, subj)+ " in post " + source_unique_index);
			}
			thread.setName(threadDpName(forum_uid, subj));
		} else {
			Contribution prior = getProxyContributionBySourceId(reply_to);
			if (prior == null) {
				System.out.println("What is this in reply to? Post # " + source_unique_index + "Replies to " + reply_to + " text starts with " + text);
				
			} else {
				contributionService.createDiscourseRelation(prior, curContribution, DiscourseRelationTypes.REPLY);
			}
		}
		if (reply_to == 0) {
			if (group != null) {
				DiscoursePart grp_dp = discoursepartService.createOrGetTypedDiscoursePart(
						curDiscourse, groupDpName(group), DiscoursePartTypes.GROUP);
				discoursepartService.createDiscoursePartRelation(grp_dp, forum, DiscoursePartRelationTypes.SUBPART);
				//discourses = grp_dp.getDiscourseToDiscourseParts().
			}
			if (team != null && group != null) {
				DiscoursePart team_dp = discoursepartService.createOrGetTypedDiscoursePart(
						curDiscourse, teamDpName(group,team), DiscoursePartTypes.TEAM);
				discoursepartService.createDiscoursePartRelation(team_dp, thread, DiscoursePartRelationTypes.SUBPART);
			}
		}
		
		discoursepartService.addContributionToDiscoursePart(curContribution, thread);
		
		//parse and set creation time for content and contribution
		try{
			Date date = forgiving_date_parse(when);									
			curContent.setStartTime(date);
			curContent.setEndTime(date);
			curContribution.setStartTime(date);
			curContribution.setEndTime(date);									
			if (thread.getEndTime() == null || date.after(thread.getEndTime())) {
				thread.setEndTime(date);
			}
			if (thread.getStartTime() == null || date.before(thread.getStartTime())) {
				thread.setStartTime(date);
			}
		}catch(ParseException e){
			log.error("Could not parse creation time "+ when, e);
		}
		
		return curContribution.getId();
	
	}
	
	static Date forgiving_date_parse(String thedate) throws ParseException {
		try { return	sdf.parse(thedate); } catch (ParseException pe) { }
		try { return	sdf2.parse(thedate); } catch (ParseException pe) { }
		try { return	sdf3.parse(thedate); } catch (ParseException pe) { }
		try { return	sdf4.parse(thedate); } catch (ParseException pe) { }
		try { return	sdf5.parse(thedate); } catch (ParseException pe) { }
		return	sdf6.parse(thedate); 
	}
	
	/*
	 * Maps a file as a single standalong contribution
	 * 
	 * @param group
	 * @param team
	 * @param username
	 * @param title
	 * @param content
	 * @param discourseName
	 * @param datasetName
	 * @param sourceFileName
	 * @param sourceColumnName
	 * @param sourceUniqueIndex
	 * @param contributionType
	 * 
	 */
	public void mapFile(
			String group, String team, String username, String title, ContributionTypes contributionType,
			String content, String discourseName, String datasetName, 
			String sourceFileName, String sourceColumnName,
			String sourceUniqueIndex) {
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		 
		User curUser = username != null?userService.createOrGetUser(curDiscourse,  username):null;
		
		log.trace("Create Content entity");
		Content curContent = contentService.createContent();
		curContent.setText(content);
		curContent.setTitle(title);
		if (curUser != null) { curContent.setAuthor(curUser); }
		dataSourceService.addSource(curContent, new DataSourceInstance(
				sourceUniqueIndex, sourceFileName + "#" + sourceColumnName + "(content)",
				DataSourceTypes.BAZAAR, datasetName));
		
		log.trace("Create Contribution entity");
		Contribution curContribution = contributionService.createTypedContribution(contributionType);
		curContribution.setCurrentRevision(curContent);
		curContribution.setFirstRevision(curContent);
		dataSourceService.addSource(curContribution, new DataSourceInstance(
				sourceUniqueIndex, sourceFileName + "#" + sourceColumnName + "(contrib)",
				DataSourceTypes.BAZAAR, datasetName));
		
		DiscoursePart team_dp = discoursepartService.createOrGetTypedDiscoursePart(
				curDiscourse, teamDpName(group,team), DiscoursePartTypes.TEAM);
		discoursepartService.addContributionToDiscoursePart(curContribution, team_dp);		
	}
	
	static java.util.Date dummyTime = new java.util.Date();   // For undated things, use a date to keep convos sorted
	
	/**
	 * Maps a discussion forum message to DiscourseDB
	 * 
	 * @param when
	 * @param author
	 * @param group
	 * @param team
	 * @param text
	 * @param discourse_name
	 * @param dataset_name
	 * @param source_file_name
	 * @param source_column_name
	 * @param source_unique_index
	 * 
	 * @returns the contribution Id written to the database
	 */
	public Long mapChat(
			String when,
			String author, 
			String group,
			String team,
			String text, 
			String discourse_name,
			String dataset_name,
			String source_file_name, String source_column_name, String source_unique_index) {
		
		if (when == null) {
			when = sdf.format(dummyTime);
			dummyTime = DateUtils.addMinutes(dummyTime,1);
		}
		
		
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourse_name);
		 
		User curUser = userService.createOrGetUser(curDiscourse,  author);
		ContributionTypes mappedType = null;
		
		log.trace("Create Content entity");
		Content curContent = contentService.createContent();
		curContent.setText(text);
		curContent.setAuthor(curUser);
		dataSourceService.addSource(curContent, new DataSourceInstance(
				source_unique_index, source_file_name + "#" + source_column_name+ "(content)",
				DataSourceTypes.BAZAAR, dataset_name));
		
		log.trace("Create Contribution entity");
		Contribution curContribution = contributionService.createTypedContribution(ContributionTypes.POST);
		curContribution.setCurrentRevision(curContent);
		curContribution.setFirstRevision(curContent);
		dataSourceService.addSource(curContribution, new DataSourceInstance(
				source_unique_index, source_file_name + "#" + source_column_name + "(contribution)",
				DataSourceTypes.BAZAAR, dataset_name));
		discussion_source2ddb.put(Long.valueOf(source_unique_index), curContribution.getId());
		
		
		DiscoursePart team_chat = discoursepartService.createOrGetDiscoursePartByDataSource(curDiscourse, 
				group + "_" + team, "xustudty#teamchat", DataSourceTypes.BAZAAR, dataset_name, DiscoursePartTypes.CHATROOM);
		team_chat.setName("Team " + group + "_" + team + " chat");
		DiscoursePart team_dp = discoursepartService.createOrGetTypedDiscoursePart(
				curDiscourse, teamDpName(group,team), DiscoursePartTypes.TEAM);
		discoursepartService.createDiscoursePartRelation(team_dp, team_chat, DiscoursePartRelationTypes.SUBPART);
		discoursepartService.addContributionToDiscoursePart(curContribution, team_chat);
		
		//parse and set creation time for content and contribution
		try{
			Date date = forgiving_date_parse(when);									
			curContent.setStartTime(date);
			curContent.setEndTime(date);
			curContribution.setStartTime(date);
			curContribution.setEndTime(date);									
			if (team_chat.getEndTime() == null || date.after(team_chat.getEndTime())) {
				team_chat.setEndTime(date);
			}
			if (team_chat.getStartTime() == null || date.before(team_chat.getStartTime())) {
				team_chat.setStartTime(date);
			}
		}catch(ParseException e){
			log.error("Could not parse creation time "+ when, e);
		}
		
		return curContribution.getId();
	
	}
	
	
	
	public static String forumDpName(String forum) { return "Discussion forum " + forum; }
	public static String threadDpName(String forum, String thread) { return "Thread in " + forum + ": " + thread; }
	public static String groupDpName(String g) { return "Group " + g; }
	public static String teamDpName(String g, String t) { return "Team " + g + "_" + t; }
	//public static String groupDiscussionName(String g) { return "Group " + g + " forums"; }
	//public static String teamDiscussionName(String g, String t) { return "Team " + g + "_" + t + " threads"; }
	
	/*
	 * Map team and group structure to discoursedb
	 * 
	 * @param discourseName
	 * @param group
	 * @param team
	 * @param dataset
	 * @param source_fn filename in source dataset
	 * @param source_col
	 * @param source_idx
	 * 
	 */
	public void mapTeamAndGroup(String discourseName, String group, String team, String dataset, 
			String source_fn, String source_col, String source_idx)	{
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		DiscoursePart grp_dp = discoursepartService.createOrGetTypedDiscoursePart(
				curDiscourse, groupDpName(group), DiscoursePartTypes.GROUP);
		dataSourceService.addSource(grp_dp, new DataSourceInstance(source_idx, source_fn+"#"+source_col+"(group)", dataset));
		DiscoursePart team_dp = discoursepartService.createOrGetTypedDiscoursePart(
				curDiscourse, teamDpName(group,team), DiscoursePartTypes.TEAM);
		dataSourceService.addSource(team_dp, new DataSourceInstance(source_idx, source_fn+"#"+source_col+"(team)", dataset));
		discoursepartService.createDiscoursePartRelation(grp_dp, team_dp, DiscoursePartRelationTypes.SUBPART);
		/* TOO CONFUSING
		DiscoursePart grp_disc_dp = discoursepartService.createOrGetTypedDiscoursePart(
				curDiscourse, groupDiscussionName(group), DiscoursePartTypes.FOLDER);
		dataSourceService.addSource(grp_disc_dp, new DataSourceInstance(source_idx, source_fn+"#"+source_col+"(group disc)", dataset));
		DiscoursePart team_disc_dp = discoursepartService.createOrGetTypedDiscoursePart(
				curDiscourse, teamDiscussionName(group,team), DiscoursePartTypes.FOLDER);
		dataSourceService.addSource(team_disc_dp, new DataSourceInstance(source_idx, source_fn+"#"+source_col+"(team disc)", dataset));
		discoursepartService.createDiscoursePartRelation(grp_dp, grp_disc_dp, DiscoursePartRelationTypes.SUBPART);
		discoursepartService.createDiscoursePartRelation(team_dp, team_disc_dp, DiscoursePartRelationTypes.SUBPART);
		*/
	}

	
	
	
}