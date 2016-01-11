package edu.cmu.cs.lti.discoursedb.io.edx.forum.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.Post;
import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.UserInfo;

/**
 * This converter loads the forum json file specified in the arguments of the app
 * and parses the json into Post objects and maps each post object to
 * DiscourseDB.
 * 
 * Many of the relations between entities are actually modeled in the form of relation tables
 * which allows us to keep track of the time window in which the relation was active.
 * However, this also entails that we need to explicitly instantiate these relations - i.e. 
 * we have to create a "relationship-entity".
 * 
 * The conversion is split into three phases.
 * Phase 1 imports all of the data except for the DiscoursRelations.
 * These relations are created between entities and require the entities to be present in the database.
 * That is why they are created in a second pass (Phase2)
 * Phase 3 adds personal information about the user to the database that comes from a different file. 
 * 
 * @author Oliver Ferschke
 *
 */
@Component
@Order(1)
public class EdxForumConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(EdxForumConverter.class);	

	@Autowired private DataSourceService dataSourceService;
	@Autowired private EdxForumConverterService converterService;

	@Override
	public void run(String... args) throws Exception {
		if(args.length<2){
			logger.error("Usage: EdxForumConverterApplication <DataSetName> </path/to/*-prod.mongo> </path/to/*-auth_user-prod-analytics.sql> (optional)");
			throw new RuntimeException("Incorrect number of launch parameters.");
		}
		final String dataSetName=args[0];		
		if(dataSourceService.dataSourceExists(dataSetName)){
			logger.warn("Dataset "+dataSetName+" has already been imported into DiscourseDB. Terminating...");			
			return;
		}
		
		final String forumDumpFileName = args[1];
		File forumDumpFile = new File(forumDumpFileName);
		if (!forumDumpFile.exists() || !forumDumpFile.isFile() || !forumDumpFile.canRead()) {
			logger.error("Forum dump file does not exist or is not readable.");
			throw new RuntimeException("Can't read file "+forumDumpFileName);
		}
		
		//optionally read user mapping file
		File userMappingFile = null;
		if(args.length>2){
			final String userMappingFileName = args[2];
			userMappingFile = new File(userMappingFileName);
			if (!userMappingFile.exists() || !userMappingFile.isFile() || !userMappingFile.canRead()) {
				logger.error("User mappiong file does not exist or is not readable.");
				throw new RuntimeException("Can't read file "+userMappingFileName);
			}			
		}
		
		/*
		 * *** Start processing dumps. ***
		 * 
		 * We need to read the forum dump twice. 
		 * First to create entities in the database and then to create 
		 * (Discourse)-relationships between the entities.
		 */
		
		//Phase 1: read through input file once and map all entities

		logger.info("Phase 1: Mapping forum posts and related entities to DiscourseDB");
		try(InputStream in = new FileInputStream(forumDumpFile)) {
			Iterator<Post> pit =new ObjectMapper().readValues(new JsonFactory().createParser(in), Post.class);	
			Iterable<Post> iterable = () -> pit;
			StreamSupport.stream(iterable.spliterator(), false).forEach(p->converterService.mapEntities(p, dataSetName));		
		}	
		
		
		//Phase 2: read through input file a second time and map all entity relationships
		logger.info("Phase 2: Mapping DiscourseRelations");
		try(InputStream in = new FileInputStream(forumDumpFile)) {
				Iterator<Post> pit =new ObjectMapper().readValues(new JsonFactory().createParser(in), Post.class);	
				Iterable<Post> iterable = () -> pit;
				StreamSupport.stream(iterable.spliterator(), false).forEach(p->converterService.mapRelations(p, dataSetName));		
		}	
	
		//Optional Phase 3: read user mapping file and add map user info
		if(userMappingFile!=null){			
			logger.info("Phase 3: Adding additional user information");		
			try(InputStream in = new FileInputStream(userMappingFile);) {
				CsvMapper mapper = new CsvMapper();
				CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator('\t');
				MappingIterator<UserInfo> it = mapper.readerFor(UserInfo.class).with(schema).readValues(in);
				while (it.hasNextValue()) {
					converterService.mapUser(it.next());
				}
			}	
		}
		
		logger.info("All done.");
	}



}