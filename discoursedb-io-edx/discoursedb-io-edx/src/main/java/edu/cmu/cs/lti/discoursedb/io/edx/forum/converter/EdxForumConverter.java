package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;

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
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelationType;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;

/**
 * The EdxForumConverter is the first (and currently only) bean to be launched
 * by the EdxForumConverterApp. (order defined by the Order annotation)
 * 
 * The converter loads the forum json file specified in the arguments of the app
 * and parses the jason into Post objects and maps each post object to
 * DiscourseDB.
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Order(1)
public class EdxForumConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(EdxForumConverter.class);
	private static int postcount = 1;

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
	private DiscourseRelationRepository discourseRelationRepository;
	@Autowired
	private DiscourseRelationTypeRepository discourseRelationTypeRepository;
	@Autowired
	private DiscoursePartRepository discoursePartRepository;
	@Autowired
	private DiscoursePartContributionRepository discoursePartContribRepository;

	@Override
	public void run(String... args) throws Exception {
		if (args.length != 1) {
			logger.error("Missing input file. Must provide input file as launch parameter.");
			System.exit(1);
		}
		String inFileName = args[0];

		File infile = new File(inFileName);
		if (!infile.exists() || !infile.isFile() || !infile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);
		}

		logger.trace("Starting forum conversion");
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
	 * @param p
	 *            the post object to map to DiscourseDB
	 */
	public void map(Post p) {
		logger.trace("Mapping post " + p.getId());

		// ---------- Init Discourse -----------
		logger.trace("Init Discourse entity");

		// In DiscourseDB, the combination of discourse name and descriptor is considered unique.
		// Since edX course ids are unique already, we can use them both as name and descriptor. 
		String courseid = p.getCourseId();

		Optional<Discourse> curOPtDiscourse = discourseRepository.findOneByNameAndDescriptor(courseid, courseid);
		Discourse curDiscourse;
		if (curOPtDiscourse.isPresent()) {
			curDiscourse=curOPtDiscourse.get();
		}else{
			curDiscourse = new Discourse(courseid, courseid);
			discourseRepository.save(curDiscourse);
		}

		// ---------- Init DiscoursePart -----------
		logger.trace("Init DiscoursePart entity");
		Optional<DiscoursePart> curOPtDiscoursePart = discoursePartRepository.findOneByName(courseid);
		DiscoursePart curDiscoursePart;
		if(curOPtDiscourse.isPresent()){
			curDiscoursePart=curOPtDiscoursePart.get();
		}else{
			curDiscoursePart=new DiscoursePart();
			curDiscoursePart.setName(courseid+"_FORUM");
		}
		
		// ---------- Init User -----------
		logger.trace("Init User entity");
		Optional<User> curOptUser = userRepository.findBySourceId(p.getAuthorId());
		User curUser;
		//FIXME the following is still a lame abuse of an Optional, but we can improve this later
		if(curOptUser.isPresent()){ 
			curUser=curOptUser.get();
		}else{
			curUser = new User();
			curUser.setUsername(p.getAuthorUsername());
			userRepository.save(curUser);
		}
			
		// ---------- Create Content -----------
		logger.trace("Create Content entity");
		Content curContent = new Content();
		curContent.setText(p.getBody());
		curContent.setCreationTime(p.getCreatedAt());
		curContent.setAuthor(curUser);
		curContent.setSourceId(p.getId());
		contentRepository.save(curContent);
		
		// ---------- Create Contribution -----------
		logger.trace("Create Contribution entity");
		Contribution curContribution = new Contribution();
		curContribution.setSourceId(p.getId());
		curContribution.setCurrentRevision(curContent);
		curContribution.setUpvotes(p.getUpvoteCount());
		
		//Connect the new contribution with the main forum DiscoursePart (since we don't have any other DiscourseParts)
		Set<DiscoursePartContribution> discoursePartContribs = curDiscoursePart.getDiscoursePartContributions();
		DiscoursePartContribution discoursePartContrib = new DiscoursePartContribution();
		discoursePartContrib.setContribution(curContribution);
		discoursePartContrib.setDiscoursePart(curDiscoursePart);
		discoursePartContrib.setStartTime(p.getCreatedAt());
		discoursePartContribRepository.save(discoursePartContrib);
		discoursePartContribs.add(discoursePartContrib);
		

		// ---------- Create DiscourseRelations -----------		
		logger.trace("Create DiscourseRelation entities");
		
		//If post has a is not a thread starter then create a DiscourseRelation of DESCENDANT type 
		//that connects it with the thread starter 
		Optional<Contribution> curOptParentContributon = contributionRepository.findBySourceId(p.getCommentThreadId());
		if (curOptParentContributon.isPresent()) {
			Contribution curParentContribution = curOptParentContributon.get();
			DiscourseRelation curRelation = new DiscourseRelation();
			curRelation.setSource(curParentContribution);
			curRelation.setTarget(curContribution);

			// We aasign the parent-child type by adding this DiscourseRelation
			// to the set of DESCENDANT TYPES
			Optional<DiscourseRelationType> optPartOfThreadType = discourseRelationTypeRepository
					.findOneByType(DiscourseRelationTypes.DESCENDANT.name());
			DiscourseRelationType partOfThreadType;
			if (optPartOfThreadType.isPresent()) {
				partOfThreadType = optPartOfThreadType.get();
			} else {
				partOfThreadType = new DiscourseRelationType();
				partOfThreadType.setType(DiscourseRelationTypes.DESCENDANT.name());
			}
			Set<DiscourseRelation> rels = partOfThreadType.getDiscourseRelations();
			rels.add(curRelation);
			partOfThreadType.setDiscourseRelations(rels);
			discourseRelationTypeRepository.save(partOfThreadType);			

			curRelation.setType(partOfThreadType);
			discourseRelationRepository.save(curRelation);
		}

		//If post is a reply to another post, then create a DiscourseRelation that connects it with its immediate parent
		Optional<Contribution> curOptCommentedOnContributon = contributionRepository.findBySourceId(p.getParentId());
		if (curOptCommentedOnContributon.isPresent()) {
			Contribution curParentContribution = curOptCommentedOnContributon.get();
			DiscourseRelation curRelation = new DiscourseRelation();
			curRelation.setSource(curParentContribution);
			curRelation.setTarget(curContribution);
			// We asign the parent-child type by adding this DiscourseRelation
			// to the set of DESCENDANT TYPES
			Optional<DiscourseRelationType> optCommentType = discourseRelationTypeRepository
					.findOneByType(DiscourseRelationTypes.REPLY.name());
			DiscourseRelationType commentType;
			if (optCommentType.isPresent()) {
				commentType = optCommentType.get();
			} else {
				commentType = new DiscourseRelationType();
				commentType.setType(DiscourseRelationTypes.REPLY.name());
			}
			Set<DiscourseRelation> rels = commentType.getDiscourseRelations();
			rels.add(curRelation);
			commentType.setDiscourseRelations(rels);
			discourseRelationTypeRepository.save(commentType);			

			curRelation.setType(commentType);
			discourseRelationRepository.save(curRelation);
		}

		//done with everything related to the contribution
		discoursePartRepository.save(curDiscoursePart);
		contributionRepository.save(curContribution);
		
		// TODO represent other properties?

		logger.trace("Post mapping completed.");
	}

}
