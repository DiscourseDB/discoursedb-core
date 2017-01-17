package edu.cmu.cs.lti.discoursedb.io.ravelry.converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.BasicDBObject;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

/**
 * Service for mapping data retrieved from the Twitter4j API to DiscourseDB
 * 
 * @author Oliver Ferschke
 *
 */
@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RavelryConverterService {

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursePartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull AnnotationService annoService;
	private final @NonNull TransactionAspectSupport tas;
	private final @NonNull RavelryConverterServiceHelper helper;
	

	MongoClient mongoClient;
	public String discourseName;
	public String mongoHost;
	public String mongoDbName;
	public String dataSetName;  // Equals mongoDbName
	public String group;

	Map<String,Integer> idhash = new HashMap<String,Integer>();


	public void configure(MongoClient pmongoClient, String pdiscourseName,String pmongoHost,
			String pmongoDbName,String pdataSetName,String pgroup) {
		mongoClient = pmongoClient;
		discourseName = pdiscourseName;
		mongoHost  = pmongoHost;
		mongoDbName = pmongoDbName;
		dataSetName = pdataSetName;
		group = pgroup;
	}
	
	private DataSourceInstance checkSource(String table, String id) {
		String codedId = table + "#" + id;
		String codedTag = "ravelry#" + table;
		if(dataSourceService.dataSourceExists(codedId, codedTag, dataSetName)){
			log.trace(table + "#" + id +" already exists in database. Skipping");
			return null;
		} else {
			return dataSourceService.createIfNotExists(new DataSourceInstance(codedId, codedTag,dataSetName));
		}
	}
	
	public void addGroup() {
		
		if(dataSourceService.dataSourceExists("groups#" + group, "ravelry#groups", dataSetName)){
			return;
		}
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);

		BasicDBObject query = new BasicDBObject();
		query.put("permalink", group);
		mongoClient.getDatabase(mongoDbName).getCollection("groups").find(query)
				.forEach((Block<Document>) d -> {
					
					log.info("Mapping Group "+ group);		
					idhash.put("groups#" + group, d.getInteger("forum_id"));
					DiscoursePart dp = discoursePartService.createOrGetDiscoursePartByDataSource(
							discourse, "groups#" + group, "ravelry#groups", 
							DataSourceTypes.RAVELRY, dataSetName, DiscoursePartTypes.FORUM);
					org.bson.Document forum = (org.bson.Document)d.get("forum");
					dp.setName(forum.getString("name"));
					dp.setStartTime(stringToDate(d.getString("created_at")));
					AnnotationInstance desc = annoService.createTypedAnnotation("group_metdata");
					annoService.addFeature(desc, annoService.createTypedFeature(d.getString("short_description"), "description"));			
					annoService.addFeature(desc, annoService.createTypedFeature(d.getInteger("forum_id").toString(), "forum_id"));			
					annoService.addAnnotation(dp, desc);			

				});
		log.info("end of adding group");
	}
	
	DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss Z");
	Date stringToDate(String s) {
		return Date.from(LocalDateTime.from(f.parse(s)).toInstant(ZoneOffset.UTC));
	}
	
	
	public List<Integer> addGroupsTopics() {
		
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		List<Integer> topics  = new ArrayList();
		DiscoursePart forum = discoursePartService.findOneByDataSource("groups#" + group, "ravelry#groups", dataSetName).get();
		BasicDBObject query = new BasicDBObject();
		query.put("forum_permalink", group);
		mongoClient.getDatabase(mongoDbName).getCollection("topics").find(query)
				.forEach((Block<Document>) d -> {
					int topicid = d.getInteger("id");
					log.info("Mapping Group "+ group + " topic " + topicid );		

					DiscoursePart topic = discoursePartService.createOrGetDiscoursePartByDataSource(
							discourse, "topics#" + topicid, "ravelry#topics", DataSourceTypes.RAVELRY, 
							dataSetName, DiscoursePartTypes.THREAD);
					topics.add(topicid);
					if (topic.getName() == "dummy_name") {
						topic.setName(d.getString("title"));
						topic.setStartTime(stringToDate(d.getString("created_at")));
						
						discoursePartService.createDiscoursePartRelation(forum, topic,
								DiscoursePartRelationTypes.SUBPART);
					}
				});
		log.info("done with adding topics" + topics);
		return topics;
	}
	
	private Pattern projRegex1 = Pattern.compile("www.ravelry.com/projects/([a-zA-Z0-9_-]*)/([a-zA-Z0-9_-]*)");
	private Pattern pattRegex2 = Pattern.compile("www.ravelry.com/patterns/library/([a-zA-Z0-9_-]*)");
	private Pattern pattRegex3 = Pattern.compile("\"/patterns/library/([a-zA-Z0-9_-]*)");
	/*
	 * Returns map of pattern permalink to "pattern" or "project"
	 */
	public Map<String,String> findPatterns(String body_html) {
		Map<String,String> map = new HashMap<String,String>();
		Matcher match = projRegex1.matcher(body_html);
		while(match.find()) {
			map.put(match.group(2), "project");
		}
		match = pattRegex2.matcher(body_html);
		while(match.find()) {
			map.put(match.group(1), "pattern");
		}
		match = pattRegex3.matcher(body_html);
		while(match.find()) {
			map.put(match.group(1), "pattern");
		}
		return map;
	}
	
	
	public Content createOrAddPattern(Discourse discourse, String pattern_permalink) {
		String source = "pattern#" + pattern_permalink;
		Optional<Content> oc = contentService.findOneByDataSourceId(source);
		if (!oc.isPresent()) { 
			Content c = contentService.createOrGetContentByDataSource(discourse, 
				 source, "ravelry#pattern", DataSourceTypes.RAVELRY, dataSetName);
		
			Document pattern = mongoClient.getDatabase(mongoDbName).getCollection("patterns_full")
				.find(new BasicDBObject("permalink",pattern_permalink)).first();
			c.setTitle(pattern_permalink);
			if (pattern != null) {
				c.setTitle(pattern.getString("name"));
				
				/*List<Document> src_infos = (List<Document>)pattern.get("pattern_sources");
				if (!src_infos.isEmpty()) {
					c.setStartTime(stringToDate(src_infos.get(0).getString("created_at")));
				}
				*/
				try {
					List<Document> src_infos = (List<Document>)((List<Document>)pattern.get("printings")).get(0).get("pattern_sources");
					if (!src_infos.isEmpty()) {
						c.setStartTime(stringToDate(src_infos.get(0).getString("created_at")));
					}
				} catch (NullPointerException npe) {
					
				}
				
				try {
					Document auth_info = (Document)pattern.get("pattern_author");
					List<Document> users = (List<Document>)auth_info.get("users");
					String username = users.get(0).getString("username");
					User u = userService.createOrGetUser(discourse, username, "people#" + username,"ravelry#people",
							DataSourceTypes.RAVELRY, dataSetName);
					c.setAuthor(u);
				} catch (NullPointerException npe) {
					
				} catch (IndexOutOfBoundsException ioobe) {
					
				}
			}			
			return c;
		} else {
			return oc.get();
		}
	}

	
	public List<Integer> addPostings(int topic) {
		
		List<Integer> postings  = new ArrayList<Integer>();

		BasicDBObject query = new BasicDBObject();
		query.put("topic_id", topic);
		BasicDBObject sortby = new BasicDBObject("post_number", 1);
		log.info("  Querying postings in topic " + topic);
		progress_counter = 0;
		List<Document> lbd = new ArrayList<>();
		mongoClient.getDatabase(mongoDbName).getCollection("postings").find(query).sort(sortby).forEach((Block<Document>) d -> { lbd.add(d); });
		for (int clump=0; clump<= lbd.size(); clump += 300) {
			log.info("  adding posts " + clump + " through " + Math.min(clump+300,lbd.size()));
			postings.addAll(helper.addPostingGroup(topic, lbd.subList(clump, Math.min(clump+300,lbd.size())),this));
		}
		log.info("done with adding posts to this topic");
		return postings;
	}

	int progress_counter = 0;
	@Deprecated
	public List<Integer> addPostingsOld(int topic) {
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		DiscoursePart thread = discoursePartService.findOneByDataSource("topics#" + topic, "ravelry#topics", dataSetName).get();
		
		List<Integer> postings  = new ArrayList<Integer>();

		BasicDBObject query = new BasicDBObject();
		query.put("topic_id", topic);
		BasicDBObject sortby = new BasicDBObject("post_number", 1);
		log.info("  Querying postings in topic " + topic);
		progress_counter = 0;
		
		mongoClient.getDatabase(mongoDbName).getCollection("postings").find(query).sort(sortby).noCursorTimeout(true)
				.forEach((Block<Document>) d -> {
					progress_counter += 1;
					int post_id = d.getInteger("id");
					postings.add(post_id);

					log.info("    Posting " + post_id + " in topic " + topic + " #" + progress_counter);
					if (progress_counter % 100 == 0) {
						//tas.currentTransactionStatus().flush();
					}
					Optional<Contribution> o_post = contributionService.findOneByDataSource("postings#" + Integer.toString(post_id), "ravelry#postings", dataSetName);
					Contribution post = null;
					if (!o_post.isPresent()) {
						post = contributionService.createTypedContribution(ContributionTypes.POST);

						DataSourceInstance contribSource = dataSourceService.createIfNotExists(
								new DataSourceInstance("postings#" + Integer.toString(post_id),"ravelry#postings",dataSetName));
						contribSource.setSourceType(DataSourceTypes.RAVELRY);
						dataSourceService.addSource(post, contribSource);		
						post.setStartTime(stringToDate(d.getString("created_at")));
						
						//AnnotationInstance url = annoService.createTypedAnnotation("twitter_user_info");
						//annoService.addFeature(userInfo, annoService.createTypedFeature(String.valueOf(tUser.getFavouritesCount()), "favorites_count"));

						Content curContent = contentService.createContent();
						curContent.setText(d.getString("body_html"));
						
						Map<String,String> patterns = findPatterns(d.getString("body_html"));
						for (String pattern: patterns.keySet()) {
							Content pattContent = createOrAddPattern(discourse, pattern);
							ContributionContext cclink = contributionService.addContextToContribution(post, pattContent);
							cclink.setType("MENTION_OF_" + patterns.get(pattern).toString().toUpperCase()); 
						}
						
						//curContent.setAuthor(user);
						curContent.setStartTime(stringToDate(d.getString("created_at")));
						post.setCurrentRevision(curContent);
						post.setFirstRevision(curContent);		
					
						discoursePartService.addContributionToDiscoursePart(post, thread);						

						String username = ((org.bson.Document)d.get("user")).getString("username");
						User user = userService.createOrGetUser(discourse, username, "people#" + username,"ravelry#people", 
								DataSourceTypes.RAVELRY, dataSetName);		
						curContent.setAuthor(user);
					}
					
				});
		log.info("done with adding topics");
		return postings;
	}

	
	
	public String addUser(int post_id) {
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		Contribution posting = contributionService.findOneByDataSource("postings#" + Integer.toString(post_id),"ravelry#postings", dataSetName).get();
		User user = posting.getCurrentRevision().getAuthor();
		mongoClient.getDatabase(mongoDbName).getCollection("people").find(new BasicDBObject("username", user.getUsername()))
			.forEach((Block<Document>) d -> {
				user.setLocation(d.getString("location"));
				user.setRealname(d.getString("first_name"));
				user.addDiscourse(discourse);
				//user.setStartTime(startTime);  only available by scraping ravelry web pages
			});
		return user.getUsername();
	}
/*
	public void addPostingPatternRefs(int post_id) {
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		Contribution posting = contributionService.findOneByDataSource("postings#" + Integer.toString(post_id),"ravelry#postings", dataSetName).get();

			
			
		mongoClient.getDatabase(mongoDbName).getCollection("patterns")
				.find(new BasicDBObject("username", user.getUsername()))
			.forEach((Block<Document>) d -> {
				user.setLocation(d.getString("location"));
				user.setRealname(d.getString("first_name"));
				user.addDiscourse(discourse);
				//user.setStartTime(startTime);  only available by scraping ravelry web pages
			});
		return user.getUsername();
		
	}*/


}
	