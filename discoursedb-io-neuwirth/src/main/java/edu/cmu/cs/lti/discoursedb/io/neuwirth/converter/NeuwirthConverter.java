package edu.cmu.cs.lti.discoursedb.io.neuwirth.converter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class NeuwirthConverter implements CommandLineRunner{
	
	private static final Logger logger = LogManager.getLogger(NeuwirthConverter.class);
	
	@Autowired private NeuwirthConverterService converterService;
	
	@Override
	public void run(String... args) throws IOException, ParseException {
		
		if (args.length != 2) {
			logger.error("Usage: NeuwirthConverterApplication <DatasetName> </path/to/datafolder>");
			return;
		}
		
		String dataSetName = args[0];
		String folderPath = args[1];
		
		// check whether the path is a directory path
		
		File foler = new File(folderPath);
		if(!foler.isDirectory()) {
			logger.error("Input file does not exist or is not readable.");
			return;
		}
		
		logger.info("Starting neuwirth data conversion");
		convert(dataSetName, folderPath);
		logger.info("Neuwirth data conversion completed");
		
	}
	
	private void convert(String dataSetName, String folderPath) throws IOException, ParseException {
		
		File folder = new File(folderPath);
		for(File f : folder.listFiles()) {
			System.out.println("<<<<<<Processing: "+f.getName()+" Start>>>>>>");
			String filePath = "src/resources/"+f.getName();
			converterService.mapFile(dataSetName, filePath, f.getName());
			System.out.println("<<<<<<Processing: "+f.getName()+" Finish>>>>>>");
		}
		
		return;
	}
	
}