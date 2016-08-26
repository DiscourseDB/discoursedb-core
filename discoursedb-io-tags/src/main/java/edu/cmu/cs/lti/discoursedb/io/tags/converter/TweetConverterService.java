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
package edu.cmu.cs.lti.discoursedb.io.tags.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.tags.model.TweetInfo;
import edu.cmu.cs.lti.discoursedb.io.tags.model.TweetSourceMapping;

/**
 * This TweetConverterService class contains a method that maps TweetInfo objects to DiscourseDB entities
 * and a method that builds relation between the created DisocurseDB entities.
 * 
 * @author Haitian Gong
 *
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
public class TweetConverterService {

	@Autowired
	private DataSourceService dataSourceService;
	@Autowired
	private DiscourseService discourseService;
	@Autowired
	private UserService userService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private ContributionService contributionService;
	
	private static final Logger logger = LogManager.getLogger(TweetConverterService.class);

	private final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd hh:mm:ss '+0000' YYYY");
	
	/**
	 * Maps a single TweetInfo object to DiscourseDB entities
	 * Contribution, Content and User entities are created in DiscourseDB during mapping 
	 * 
	 * @param t              a TweetInfo object
	 * @param dataSetName    the name of the dataSet
	 * @param discourseName  the name of the discourse 
	 */
	
	
	public void mapTweet(TweetInfo t, String dataSetName, String discourseName) throws ParseException {
		
		
		if (contributionService.findOneByDataSource(t.getId_str(), TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).isPresent()) {
			logger.warn("Tweet " + t.getId_str() + " already in database. Skipping Tweet");
			return;
		}
		
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		User curUser = userService.createOrGetUser(curDiscourse, t.getFrom_user());
		curUser.setLanguage(t.getUser_lang());
		dataSourceService.addSource(curUser, new DataSourceInstance(t.getFrom_user_id_str(), TweetSourceMapping.FROM_USER_ID_STR_TO_USER,DataSourceTypes.TAGS, dataSetName));
		
		
		Optional<Contribution> existingContribution = contributionService.findOneByDataSource(t.getId_str(),TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);
		Contribution curContribution = null;
		if (!existingContribution.isPresent()) {
			ContributionTypes mappedType = ContributionTypes.TWEET;

			logger.trace("Create Content entity");
			Content curContent = contentService.createContent();
			curContent.setText(t.getText());
			if(t.getCreated_at()!=null) {
				java.util.Date date = sdf.parse(t.getCreated_at());
				java.sql.Date sdate = new java.sql.Date(date.getTime());
				curContent.setStartTime(sdate);
			}
			curContent.setAuthor(curUser);
			dataSourceService.addSource(curContent, new DataSourceInstance(t.getId_str(), TweetSourceMapping.ID_STR_TO_CONTENT, DataSourceTypes.TAGS, dataSetName));

			logger.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(mappedType);
			curContribution.setCurrentRevision(curContent);
			if(t.getCreated_at()!=null) {
				java.util.Date date = sdf.parse(t.getCreated_at());
				java.sql.Date sdate = new java.sql.Date(date.getTime());
				curContribution.setStartTime(sdate);
			}
			curContribution.setFirstRevision(curContent);
			
			dataSourceService.addSource(curContribution, new DataSourceInstance(t.getId_str(), TweetSourceMapping.ID_STR_TO_CONTRIBUTION, DataSourceTypes.TAGS, dataSetName));
		}
	}
	
	/**
	 * Builds relationship between Contribution entities created by the mapTweet method.
	 * Two types of DiscourseRelation are created: REPLY and RESHARE 
	 * 
	 * @param t              a TweetInfo object
	 * @param dataSetName    the name of the dataSet
	 * @param map            a HashMap that stores that stores the relations between text and its Tweet id and author
	 */
	
	
	public void mapRelation(TweetInfo t, String dataSetName, HashMap<String, ArrayList<String[]>> map) {
		
		//build REPLY DiscourseRelation between replies and the tweet they reply to
		
		if(t.getIn_reply_to_status_id_str()!=null) {
			Optional<Contribution> origContribution = 
					contributionService.findOneByDataSource(
							t.getIn_reply_to_status_id_str(),TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);
			if(origContribution.isPresent()) {
				Contribution curContribution = contributionService.findOneByDataSource(t.getId_str(), TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).get();
				contributionService.createDiscourseRelation(origContribution.get(), curContribution, DiscourseRelationTypes.REPLY);
			}
		}
		
		//build RESHARE DiscoursePart relation between original tweets and retweets
		
		String text = t.getText();
		if(text.contains("RT @")) {
			System.out.println(text);
			if(text.indexOf('@')<0||text.indexOf(':')<0) return;
			String name = text.substring(text.indexOf('@', text.indexOf("RT"))+1, text.indexOf(':', text.indexOf("RT")));  
			text = text.substring(text.indexOf(':', text.indexOf("RT"))+2);
			if(map.containsKey(text)) {
				for(int i=0;i<map.get(text).size();i++) {
					if(map.get(text).get(i)[1].equals(name)) {
						Optional<Contribution> origContribution = 
								contributionService.findOneByDataSource(
										map.get(text).get(i)[0],TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);
						if(origContribution.isPresent()) {
							Contribution curContribution = contributionService.findOneByDataSource(t.getId_str(), TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).get();
							contributionService.createDiscourseRelation(origContribution.get(), curContribution, DiscourseRelationTypes.RESHARE);
							System.out.println("Success");
						}
					}
				}
			}
		}
	}
	
}
