package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;

@Transactional
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class EdxForumConverter  implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(EdxForumConverter.class);
	private static int postcount = 1;

	/*
	 * Entity-Repositories for DiscourseDB connection.
	 */

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private DiscourseRepository discourseRepo;

	@Autowired
	private ContributionRepository contributionRepo;

	@Autowired
	private ContentRepository contentRepo;

	@Autowired
	private DiscoursePartRepository discoursePartRepo;

	public static void main(String[] args) {
		SpringApplication.run(EdxForumConverter.class, args);
	}

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
		EdxForumConverter converter = new EdxForumConverter();
		converter.convert(inFileName);
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
			for (Iterator<Post> it = new ObjectMapper().readValues(new JsonFactory().createParser(in), Post.class); it.hasNext();) {
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

		// edX course ids are unique, but in DiscourseDB the combination of
		// discourse name and descriptor are unique.
		// So, we use the course id both as name and descriptor.
		String courseid = p.getCourseId();
		Discourse curDiscourse = discourseRepo.findOneByNameAndDescriptor(courseid, courseid);
		if (curDiscourse == null) {
			curDiscourse = new Discourse(courseid, courseid);
			discourseRepo.save(curDiscourse);
		}

		// ---------- Init DiscoursePart -----------
		logger.trace("Init DiscoursePart entity");

		// ---------- Init User -----------
		logger.trace("Init User entity");

		// ---------- Create Content -----------
		logger.trace("Create Content entity");

		// ---------- Create Contribution -----------
		logger.trace("Create Contribution entity");

		// TODO represent other properties

		
		logger.trace("Post mapping completed.");
	}

}
