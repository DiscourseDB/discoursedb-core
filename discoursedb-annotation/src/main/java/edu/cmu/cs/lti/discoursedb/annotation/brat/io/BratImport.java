package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
import lombok.extern.log4j.Log4j;

/**
 * 
 * @author Oliver Ferschke
 */
@Log4j
@Component
@SpringBootApplication
@ComponentScan(basePackages = {
		"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.brat.io" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratImport.class, BaseConfiguration.class, BratImportService.class }) })
public class BratImport implements CommandLineRunner {

	@Autowired private BratImportService importService;

	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length == 1, "USAGE: BratThreadImport <inputFolder>");
		SpringApplication.run(BratImport.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String inputFolder = args[0];

		File dir = new File(inputFolder);
		// retrieve all files that end with ann, strip off the extension and
		// save the file name without extension in a list
		List<String> baseFileNames = Arrays.stream(dir.listFiles((d, name) -> name.endsWith(".ann"))).map(f -> f.getName().split(".ann")[0]).collect(Collectors.toList());
		
		for (String baseFileName : baseFileNames) {
			File annFile = new File(inputFolder, baseFileName + ".ann");
			File offsetFile = new File(inputFolder, baseFileName + ".offsets");
			File versionsFile = new File(inputFolder, baseFileName + ".versions");
			
			log.info("Starting import of "+baseFileName);
			importService.importThread(baseFileName, annFile, offsetFile, versionsFile);
			log.trace("Finished import of "+baseFileName);
		}
	}


}
