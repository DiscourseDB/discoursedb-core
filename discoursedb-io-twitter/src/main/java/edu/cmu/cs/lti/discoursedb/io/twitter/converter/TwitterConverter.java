package edu.cmu.cs.lti.discoursedb.io.twitter.converter;

import java.io.File;
import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class TwitterConverter implements CommandLineRunner {
	
	private static final Logger logger = LogManager.getLogger(TwitterConverter.class);
	
	@Autowired 
	TwitterConverterService converterService;
	
	@Override
	public void run(String... args) throws ParseException {
		
		if (args.length != 3) {
			logger.error("Usage: TwitterConverterApplication <DiscourseName> <DataSetName> <TwitterDumpPath>");
			return;
		}
		
		String discourseName = args[0];
		String datasetName = args[1];		
		String filePath = args[2];
		
		//check whether the given file exists
		File f = new File(filePath);
		if (!f.exists() || !f.isDirectory() || !f.canRead()) {
			logger.error("Input path is not a directory, does not exist or is not readable.");
			return;
		}
		
		logger.info("Starting Twitter data conversion");
		this.convert(discourseName, datasetName, filePath);
		logger.info("Twitter data conversion completed");
	}
	
	private void convert(String discourseName, String datasetName, String filePath) throws ParseException {
		
		
	}

}
