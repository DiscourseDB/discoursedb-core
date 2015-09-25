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

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelationType;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
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
 * Phase 1 imports all of the data except for the DiscoursRelations.
 * These relations are created between entities and require the entities to be present in the database.
 * That is why they are created in a second pass (Phase2, this class)
 * Phase 3 adds personal information about the user to the database that comes from a different file. 
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Transactional
@Order(2)
public class EdxForumConverterPhase2 implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(EdxForumConverterPhase2.class);
	private static int postcount = 1;
	
	private String dataSetName;

	/*
	 * Entity-Repositories for DiscourseDB connection.
	 */

	@Autowired
	private ContributionService contributionService;
	@Autowired
	private DiscourseRelationRepository discourseRelationRepository;
	@Autowired
	private DiscourseRelationTypeRepository discourseRelationTypeRepository;

	@Override
	public void run(String... args) throws Exception {
		if(args.length<3){
			logger.error("Usage: EdxForumConverterApplication <DataSourceType> <DataSetName> </path/to/*-prod.mongo>");
			System.exit(1);
		}
		this.dataSetName=args[1];
		String inFileName = args[2];

		File infile = new File(inFileName);
		if (!infile.exists() || !infile.isFile() || !infile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);
		}

		logger.info("Starting forum conversion Phase 2");
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
		logger.trace("Mapping relations for post " + p.getId());
	
		Optional<Contribution> curOptContribution = contributionService.findOneByDataSource(p.getId(),dataSetName);

		Contribution curContribution=curOptContribution.get();
		
		//If post is not a thread starter then create a DiscourseRelation of DESCENDANT type 
		//that connects it with the thread starter 
		Optional<Contribution> curOptParentContributon = contributionService.findOneByDataSource(p.getCommentThreadId(),dataSetName);
		if (curOptParentContributon.isPresent()) {
			Contribution curParentContribution = curOptParentContributon.get();
			DiscourseRelation curRelation = new DiscourseRelation();
			curRelation.setSource(curParentContribution);
			curRelation.setTarget(curContribution);

			// We assign the parent-child type by adding this DiscourseRelation
			// to the set of DESCENDANT TYPES
			Optional<DiscourseRelationType> optPartOfThreadType = discourseRelationTypeRepository.findOneByType(DiscourseRelationTypes.DESCENDANT.name());
			DiscourseRelationType partOfThreadType;
			if (optPartOfThreadType.isPresent()) {
				partOfThreadType = optPartOfThreadType.get();
			} else {
				partOfThreadType = new DiscourseRelationType();
				partOfThreadType.setType(DiscourseRelationTypes.DESCENDANT.name());
				partOfThreadType = discourseRelationTypeRepository.save(partOfThreadType);			
			}
			
			curRelation.setType(partOfThreadType);
			curRelation = discourseRelationRepository.save(curRelation);
			partOfThreadType.addDiscourseRelation(curRelation);
			partOfThreadType = discourseRelationTypeRepository.save(partOfThreadType);
		}
		

		//If post is a reply to another post, then create a DiscourseRelation that connects it with its immediate parent
		Optional<Contribution> curOptPredecessorContributon = contributionService.findOneByDataSource(p.getParentId(),dataSetName);
		if (curOptPredecessorContributon.isPresent()) {
			Contribution curPredecessorContribution = curOptPredecessorContributon.get();
			DiscourseRelation curRelation = new DiscourseRelation();
			curRelation.setSource(curPredecessorContribution);
			curRelation.setTarget(curContribution);
			// We assign the parent-child type by adding this DiscourseRelation
			// to the set of REPLY TYPES
			Optional<DiscourseRelationType> optCommentType = discourseRelationTypeRepository
					.findOneByType(DiscourseRelationTypes.REPLY.name());
			DiscourseRelationType commentType;
			if (optCommentType.isPresent()) {
				commentType = optCommentType.get();
			} else {
				commentType = new DiscourseRelationType();
				commentType.setType(DiscourseRelationTypes.REPLY.name());
			}
			curRelation.setType(commentType);
			curRelation = discourseRelationRepository.save(curRelation);

			commentType.addDiscourseRelation(curRelation);
			commentType = discourseRelationTypeRepository.save(commentType);			
		}		
		
		logger.trace("Post relation mapping completed.");
	}

}