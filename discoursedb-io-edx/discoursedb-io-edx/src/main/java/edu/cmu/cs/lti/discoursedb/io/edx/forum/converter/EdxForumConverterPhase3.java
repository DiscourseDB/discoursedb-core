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

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvFactory;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelationType;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.UserInfo;

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
 * That is why they are created in a second pass (Phase2)
 * Phase 3 (this class) adds personal information about the user to the database that comes from a different file. 
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Transactional
@Order(3)
public class EdxForumConverterPhase3 implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(EdxForumConverterPhase3.class);
	private static int postcount = 1;
	

	/*
	 * Entity-Repositories for DiscourseDB connection.
	 */

	@Autowired
	private UserRepository userRepository;

	@Override
	public void run(String... args) throws Exception {
		if (args.length != 2) {
			logger.error("Missing input file. Must provide pointer to *-auth_user-prod-analytics.sql file as second parameter.");
			System.exit(1);
		}
		String inFileName = args[1];

		File infile = new File(inFileName);
		if (!infile.exists() || !infile.isFile() || !infile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);
		}

		logger.info("Starting forum conversion Phase 3");
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
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaFor(UserInfo.class).withColumnSeparator('\t').withHeader();
			MappingIterator<UserInfo> it = mapper.readerFor(UserInfo.class).with(schema).readValues(in);
			while (it.hasNextValue()) {
				map(it.next());
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Maps a post to DiscourseDB entities.
	 * 
	 * @param u
	 *            the post object to map to DiscourseDB
	 */
	public void map(UserInfo u) {
		logger.trace("Mapping UserInfo for user" + u.getUsername());
	
		Optional<User> curOptUser = userRepository.findBySourceIdAndUsername(u.getId()+"", u.getUsername());
		if(!curOptUser.isPresent()){
			return;
		}
		User curUser=curOptUser.get();
		if(curUser.getEmail()==null||curUser.getEmail().isEmpty()){
			curUser.setEmail(u.getEmail());
		}

		if(curUser.getRealname()==null||curUser.getRealname().isEmpty()){
			if(!u.getFirst_name().isEmpty()){
				if(!u.getLast_name().isEmpty()){
					curUser.setRealname(u.getFirst_name()+" "+u.getLast_name());
				}else{
					curUser.setRealname(u.getFirst_name());
				}
			}else{
				if(!u.getLast_name().isEmpty()){
					curUser.setRealname(u.getLast_name());
				}				
			}
		}
		
		if(curUser.getCountry()==null||curUser.getCountry().isEmpty()){
			curUser.setCountry(u.getCountry());
		}
		userRepository.save(curUser);
		logger.trace("UserInfo mapping completed for user" + u.getUsername());
	}

}