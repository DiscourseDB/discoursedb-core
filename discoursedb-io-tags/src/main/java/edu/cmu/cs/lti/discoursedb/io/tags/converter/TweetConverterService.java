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
	
	public void map(TweetInfo t, String dataSetName, String discourseName) throws ParseException {		
		if (contributionService.findOneByDataSource(t.getId_str(), TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).isPresent()) {
			logger.warn("Tweet " + t.getId_str() + " already in database. Skipping Tweet");
			return;
		}
				
		//create user entity and add it to user table of DiscourseDB
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
	
	public void mapRelation(TweetInfo t, String dataSetName, HashMap<String, ArrayList<String[]>> map) {
		//create REPLY relation between a tweet and its replies
		if(t.getIn_reply_to_status_id_str()!=null) {
			Optional<Contribution> origContribution = 
					contributionService.findOneByDataSource(
							t.getIn_reply_to_status_id_str(),TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);
			if(origContribution.isPresent()) {
				Contribution curContribution = contributionService.findOneByDataSource(t.getId_str(), TweetSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).get();
				contributionService.createDiscourseRelation(origContribution.get(), curContribution, DiscourseRelationTypes.REPLY);
			}
		}
		
		//create RESHARE relation between original tweets and retweets
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
