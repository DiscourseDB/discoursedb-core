package edu.cmu.cs.lti.discoursedb.annotation.lightside.io;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;

/**
 * Imports LightSide annotated data into DiscourseDB  
 * 
 * @author Oliver Ferschke
 */
@Component
@SpringBootApplication
@ComponentScan(basePackages = { "edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.lightside.io" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { LightSideDataImport.class, BaseConfiguration.class, LightSideService.class }) })
public class LightSideDataImport implements CommandLineRunner{

	@Autowired private LightSideService lsService;	
	
	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length ==1, "USAGE: LightSideDataImport <importFile>");
		SpringApplication.run(LightSideDataImport.class, args);
	}

	@Override
	@Transactional 
	public void run(String... args) throws Exception {
		String inputFilePath = args[0];
		File inputFile = new File(inputFilePath);
		Assert.hasText(inputFilePath, "Path to the input file cannot be empty.");
		Assert.isTrue(inputFile.exists(), "The input file doesn't exist.");
		Assert.isTrue(inputFile.isFile(), "The path to the input file points to a directory and not to a file.");
				
		lsService.importAnnotatedData(inputFilePath);
	}
}
