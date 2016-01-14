package edu.cmu.cs.lti.discoursedb.io.coursera.converter;

import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.io.coursera.io.CourseraDB;

@Component
public class CourseraConverter implements CommandLineRunner{
	
	private static final Logger logger = LogManager.getLogger(CourseraConverter.class);
	private String dataSetName;
	private String discourseName;
	
	@Autowired 
	CourseraConverterService converterService;
	@Autowired
	private DiscourseService discourseService;
	
	@Override
	public void run(String... args) throws Exception {	
		if (args.length < 2) {
			logger.error("Usage: CourseraConverterApplication <DataSourceType> <DataSetName> </path/to/*-prod.mongo>");
			System.exit(1);
		}
		this.dataSetName = args[0];
		this.discourseName = args[1];

		logger.info("Starting coursera conversion");
		convert();
		logger.info("Coursera conversion completed");
	}
	
	private void convert() throws SQLException {
		
		CourseraDB database = new CourseraDB("localhost", "coursera", "local", "local");
		
		//Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName);
		
		//Phase 1: read through forum data from database and map all entities
		converterService.mapForum(database, dataSetName, discourseName);
		
		//Phase 2: read through thread data from database and map all entities
		converterService.mapThread(database, dataSetName, discourseName);
		
		//Phase 3: read through post data from database and map all entities
		converterService.mapPost(database, dataSetName, discourseName);
		
		//Phase 4: read through comment data from database and map all entities
		converterService.mapComment(database, dataSetName, discourseName);
	}

}
