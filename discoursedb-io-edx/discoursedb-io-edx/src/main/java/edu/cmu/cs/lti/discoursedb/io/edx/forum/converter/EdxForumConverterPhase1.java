package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;

/**
 * This converter loads the forum json file specified in the arguments of the app
 * and parses the json into Post objects and maps each post object to
 * DiscourseDB.
 * 
 * Many of the relations between entities are actually modeled in the form of relation tables
 * which allows us to keep track of the time window in which the relation was active.
 * However, this also entails that we need to explicitly instantiate these relations - i.e. 
 * we have to create a "relationship-entity" which makes the code more verbose.
 * 
 * The conversion is split into three phases.
 * Phase 1 (this class) imports all of the data except for the DiscoursRelations.
 * These relations are created between entities and require the entities to be present in the database.
 * That is why they are created in a second pass (Phase2)
 * Phase 3 adds personal information about the user to the database that comes from a different file. 
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Transactional
@Order(1)
public class EdxForumConverterPhase1 implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(EdxForumConverterPhase1.class);
	private static int postcount = 1;
	private static final String EDX_COMMENT_TYPE = "Comment";
	
	private DataSourceTypes dataSourceType;
	private String dataSetName;

	/*
	 * Entity-Repositories for DiscourseDB connection.
	 */

	@Autowired
	private DiscourseService discourseService;
	@Autowired
	private UserService userService;
	@Autowired
	private DataSourceService dataSourceService;
	@Autowired
	private ContentService contentService;
	@Autowired
	private ContributionService contributionService;
	@Autowired
	private DiscoursePartService discoursePartService;

	@Override
	public void run(String... args) throws Exception {
		if(args.length<3){
			logger.error("Usage: EdxForumConverterApplication <DataSourceType> <DataSetName> </path/to/*-prod.mongo>");
			System.exit(1);
		}
		try{
			this.dataSourceType = DataSourceTypes.valueOf(args[0]);
		}catch(Exception e){
			StringBuilder types = new StringBuilder();
			for(DataSourceTypes type : DataSourceTypes.values()){
				if(types.length()==0){types.append(",");}
				types.append(type.name());
			}
			logger.error("Invalid DataSourceType: "+args[1]+". Valid values: "+types.toString());
			logger.error("");
			System.exit(1);
		}

		this.dataSetName=args[1];
		String inFileName = args[2];

		File infile = new File(inFileName);
		if (!infile.exists() || !infile.isFile() || !infile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);
		}

		logger.info("Starting forum conversion Phase 1");
		this.convert(inFileName);
	}

	/**
	 * Stream-reads the json input and binds each post in the dataset to an
	 * object that is passed on to the mapper.
	 * 
	 * @param filename
	 *            of json file that contains forum data
	 * @throws IOException
	 *             in case the inFile could not be read
	 * @throws JsonParseException
	 *             in case the json was malformed and couln't be parsed
	 */
	public void convert(String inFile) throws JsonParseException, JsonProcessingException, IOException {
		final InputStream in = new FileInputStream(inFile);
		try {
			for (Iterator<Post> it = new ObjectMapper().readValues(new JsonFactory().createParser(in), Post.class); it
					.hasNext();) {
				logger.debug("Retrieving post number " + postcount++);
				Post curPost = it.next();
				map(curPost);
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Maps a post to DiscourseDB entities.
	 * 
	 * Phase 1 maps everything except for DiscourseRelations (which connect existing Contribtions)
	 * 
	 * @param p
	 *            the post object to map to DiscourseDB
	 */
	public void map(Post p) {				
		if(contributionService.findOneByDataSource(p.getId(),dataSetName).isPresent()){
			logger.warn("Post " + p.getId()+" already in database. Skipping Post");
			return;
		}
	
		logger.trace("Mapping post " + p.getId());
		
		logger.trace("Init Discourse entity");
		String courseid = p.getCourseId();
		Discourse curDiscourse = discourseService.createOrGetDiscourse(courseid);

		logger.trace("Init DiscoursePart entity");
		// in edX, we consider the whole forum to be a single DiscoursePart		
		DiscoursePart curDiscoursePart = discoursePartService.createOrGetTypedDiscoursePart(curDiscourse,courseid+"_FORUM",DiscoursePartTypes.FORUM);
		
		logger.trace("Init User entity");
		User curUser  = userService.createOrGetUser(curDiscourse,p.getAuthorUsername());
		dataSourceService.addSource(curUser, new DataSourceInstance(p.getAuthorId(),dataSourceType, dataSetName));

		// ---------- Create Contribution and Content -----------
		//Check if contribution exists already. This could only happen if we import the same dump multiple times.
		Optional<Contribution> existingContribution = contributionService.findOneByDataSource(p.getId(),dataSetName);
		Contribution curContribution=null;
		if(!existingContribution.isPresent()){		
			ContributionTypes mappedType = p.getType().equals(EDX_COMMENT_TYPE)?ContributionTypes.POST:ContributionTypes.THREAD_STARTER;

			logger.trace("Create Content entity");
			Content curContent = contentService.createContent();
			curContent.setText(p.getBody());
			curContent.setStartTime(p.getCreatedAt());
			curContent.setAuthor(curUser);
			dataSourceService.addSource(curContent, new DataSourceInstance(p.getId(),dataSourceType,dataSetName));
			
			logger.trace("Create Contribution entity");
			curContribution = contributionService.createTypedContribution(mappedType);
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			curContribution.setStartTime(p.getCreatedAt());
			curContribution.setUpvotes(p.getUpvoteCount());
			dataSourceService.addSource(curContribution, new DataSourceInstance(p.getId(),dataSourceType,dataSetName));
		}else{
			curContribution=existingContribution.get();
		}

		//Add contribution to DiscoursePart
		discoursePartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
				
		logger.trace("Post mapping completed.");
	}

}