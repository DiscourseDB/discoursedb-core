package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model.ProsoloBlogPost;

/**
 * 
 * @author Oliver Ferschke
 *
 */
@Component
public class BlogConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(BlogConverter.class);	
	private boolean dumpWrappedInJsonArray = false;
	@Autowired private DataSourceService dataSourceService;
	@Autowired private BlogConverterService converterService;

	@Override
	public void run(String... args) throws Exception {
		if(args.length<4){
			logger.error("Usage: EdxForumConverterApplication <DiscourseName> <DataSetName> <blogDump> <userMapping> <dumpIsWrappedInJsonArray (optional, default=false)");
			throw new RuntimeException("Incorrect number of launch parameters.");
		}
		final String discourseName=args[0];		

		final String dataSetName=args[1];		
		if(dataSourceService.dataSourceExists(dataSetName)){
			logger.warn("Dataset "+dataSetName+" has already been imported into DiscourseDB. Terminating...");			
			return;
		}
		
		final String forumDumpFileName = args[2];
		File blogDumpFile = new File(forumDumpFileName);
		if (!blogDumpFile.exists() || !blogDumpFile.isFile() || !blogDumpFile.canRead()) {
			logger.error("Forum dump file does not exist or is not readable.");
			throw new RuntimeException("Can't read file "+forumDumpFileName);
		}
		final String userMappingFileName = args[3];
		File userMappingFile = new File(userMappingFileName);
		if (!userMappingFile.exists() || !userMappingFile.isFile() || !userMappingFile.canRead()) {
			logger.error("User mappiong file does not exist or is not readable.");
			throw new RuntimeException("Can't read file "+userMappingFileName);
		}
		final String jsonarray = args[4];
		if(jsonarray!=null&&jsonarray.equalsIgnoreCase(("true"))){
			this.dumpWrappedInJsonArray=true;			
		}
		
		/*
		 * Map data to DiscourseDB
		 */
		
		logger.info("Mapping blog posts and comments to DiscourseDB");
		try(InputStream in = new FileInputStream(blogDumpFile)) {			
			if(dumpWrappedInJsonArray){
				//if the json dump is wrapped in a top-level array
				@SuppressWarnings("unchecked")
				List<ProsoloBlogPost> posts =(List<ProsoloBlogPost>)new ObjectMapper().readValues(new JsonFactory().createParser(in), new TypeReference<List<ProsoloBlogPost>>(){}).next();	
				posts.stream().forEach(p->converterService.mapPost(p, discourseName, dataSetName));						
			}else{
				//if the json dump is NOT wrapped in a top-level array
				Iterator<ProsoloBlogPost> pit =new ObjectMapper().readValues(new JsonFactory().createParser(in), ProsoloBlogPost.class);	
				Iterable<ProsoloBlogPost> iterable = () -> pit;
				StreamSupport.stream(iterable.spliterator(), false).forEach(p->converterService.mapPost(p, discourseName, dataSetName));	
			}
		}	
		
		logger.info("All done.");
	}



}