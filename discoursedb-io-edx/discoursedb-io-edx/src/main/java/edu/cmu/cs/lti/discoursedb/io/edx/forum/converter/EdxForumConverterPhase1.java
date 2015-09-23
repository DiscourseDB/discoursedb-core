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
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseToDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseToDiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
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
	@SuppressWarnings("unused")
	private static final String EDX_COMMENT_THREAD_TYPE = "CommentThread";
	

	/**
	 * Determines whether the ids provided by the source system are unique within discourse db.
	s */
	private static final boolean uniqueSourceIds = true;

	/*
	 * Entity-Repositories for DiscourseDB connection.
	 */

	@Autowired
	private DiscourseRepository discourseRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ContentRepository contentRepository;
	@Autowired
	private ContributionRepository contributionRepository;
	@Autowired
	private DiscoursePartRepository discoursePartRepository;
	@Autowired
	private ContributionTypeRepository contributionTypeRepository;
	@Autowired
	private DiscourseToDiscoursePartRepository discourseToDiscoursePartRepository;
	@Autowired
	private DiscoursePartTypeRepository discoursePartTypeRepository;
	@Autowired
	private DiscoursePartContributionRepository discoursePartContributionRepository;

	@Override
	public void run(String... args) throws Exception {
		if (args.length < 1) {
			logger.error("Missing input file. Must provide </path/to/*-prod.mongo> as launch parameter.");
			System.exit(1);
		}
		String inFileName = args[0];

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
		if(contributionRepository.findOneBySourceId(p.getId()).isPresent()){
			logger.warn("Post " + p.getId()+" already in database. Skipping Post");
			return;
		}
	
		logger.trace("Mapping post " + p.getId());
		
		// ---------- Init Discourse -----------
		logger.trace("Init Discourse entity");

		// In DiscourseDB, the combination of discourse name and descriptor is considered unique.
		// Since edX course ids are unique already, we can use them both as name and descriptor. 
		String courseid = p.getCourseId();

		Optional<Discourse> curOptDiscourse = discourseRepository.findOneByName(courseid);
		Discourse curDiscourse;
		if (curOptDiscourse.isPresent()) {
			curDiscourse=curOptDiscourse.get();
		}else{
			curDiscourse = new Discourse(courseid);
			curDiscourse=discourseRepository.save(curDiscourse);
		}

		// ---------- Init DiscoursePart -----------
		// in edX, we consider the whole forum to be a single DiscoursePart
		
		logger.trace("Init DiscoursePart entity");
		Optional<DiscoursePart> curOPtDiscoursePart = discoursePartRepository.findOneByName(courseid+"_FORUM");
		DiscoursePart curDiscoursePart;
		if(curOPtDiscoursePart.isPresent()){
			curDiscoursePart=curOPtDiscoursePart.get();
		}else{
			curDiscoursePart=new DiscoursePart();
			curDiscoursePart.setName(courseid+"_FORUM");
			//TODO no start time set - what's the start time of this forum?
		}

		//set Type of DiscoursePart
		Optional<DiscoursePartType> optDiscoursePartType = discoursePartTypeRepository.findOneByType(DiscoursePartTypes.FORUM.name());
		DiscoursePartType discoursePartType;
		if(optDiscoursePartType.isPresent()){
			discoursePartType = optDiscoursePartType.get();
		}else{
			discoursePartType = new DiscoursePartType();
			discoursePartType.setType(DiscoursePartTypes.FORUM.name());
			discoursePartType=discoursePartTypeRepository.save(discoursePartType);
		}		
		curDiscoursePart.setType(discoursePartType);
		curDiscoursePart = discoursePartRepository.save(curDiscoursePart);

		// ---------- Connect DiscoursePart with Discourse -----------
		
		Optional<DiscourseToDiscoursePart> curOptdiscourseToDiscoursePart = discourseToDiscoursePartRepository.findOneByDiscourseAndDiscoursePart(curDiscourse, curDiscoursePart);
		if(!curOptdiscourseToDiscoursePart.isPresent()){
			DiscourseToDiscoursePart discourseToDiscoursePart = new DiscourseToDiscoursePart();		
			discourseToDiscoursePart.setDiscourse(curDiscourse);
			discourseToDiscoursePart.setDiscoursePart(curDiscoursePart);
			//TODO no start time set - what's the start time of this forum?
			discourseToDiscoursePart=discourseToDiscoursePartRepository.save(discourseToDiscoursePart);			
		}
		
		// ---------- Init User -----------
		logger.trace("Init User entity");

		//there is a unique constraint on sourceId and username, so we can be sure that this checks out
		//there is a tiny risk that two source platforms could have the same id and username pair for two different users
		Optional<User> curOptUser = userRepository.findBySourceIdAndUsername(p.getAuthorId(),p.getAuthorUsername());
		User curUser;
		if(curOptUser.isPresent()){ 
			curUser=curOptUser.get();
		}else{
			curUser = new User(curDiscourse);
			curUser.setUsername(p.getAuthorUsername());
			curUser.setSourceId(p.getAuthorId());
		}
		curUser = userRepository.save(curUser);

		Optional<Contribution> curOptContribution = contributionRepository.findOneBySourceId(p.getId());
		Contribution curContribution=null;
		if(uniqueSourceIds&&!curOptContribution.isPresent()){		
			// ---------- Create Content -----------
			logger.trace("Create Content entity");
			Content curContent = new Content();
			curContent.setText(p.getBody());
			curContent.setCreationTime(p.getCreatedAt());
			curContent.setAuthor(curUser);
			curContent.setSourceId(p.getId());
			curContent=contentRepository.save(curContent);
			
			// ---------- Create Contribution -----------
			logger.trace("Create Contribution entity");
			curContribution = new Contribution();
			curContribution.setSourceId(p.getId());
			curContribution.setCurrentRevision(curContent);
			curContribution.setFirstRevision(curContent);
			curContribution.setStartTime(p.getCreatedAt());
			curContribution.setUpvotes(p.getUpvoteCount());
	
			//Set Contribution Type
			
			String mappedType = p.getType().equals(EDX_COMMENT_TYPE)?ContributionTypes.POST.name():ContributionTypes.THREAD_STARTER.name();
			Optional<ContributionType> optType = contributionTypeRepository.findOneByType(mappedType);
			ContributionType type;
			if(optType.isPresent()){
				type = optType.get();
			}else{
				type = new ContributionType();
				type.setType(mappedType);
				type=contributionTypeRepository.save(type);
			}		
			curContribution.setType(type);
			
			curContribution = contributionRepository.save(curContribution); 		
		}else{
			curContribution=curOptContribution.get();
		}
		
		Optional<DiscoursePartContribution> curOptDiscoursePartContrib = discoursePartContributionRepository.findOneByContributionAndDiscoursePart(curContribution, curDiscoursePart);
		if(!curOptDiscoursePartContrib.isPresent()){
			DiscoursePartContribution discoursePartContrib = new DiscoursePartContribution();
			discoursePartContrib.setContribution(curContribution);
			discoursePartContrib.setDiscoursePart(curDiscoursePart);
			discoursePartContrib.setStartTime(p.getCreatedAt());	
			curDiscoursePart.addDiscoursePartContribution(discoursePartContrib);
			curContribution.addContributionPartOfDiscourseParts(discoursePartContrib);

			discoursePartContrib=discoursePartContributionRepository.save(discoursePartContrib);
			curDiscoursePart = discoursePartRepository.save(curDiscoursePart); //update discoursePart
			curContribution = contributionRepository.save(curContribution); //update contribution
		}	
		
		logger.trace("Post mapping completed.");
	}

}