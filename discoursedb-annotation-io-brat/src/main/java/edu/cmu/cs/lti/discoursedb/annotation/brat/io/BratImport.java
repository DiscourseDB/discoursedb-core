package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;

/**
 * 
 * @author Oliver Ferschke
 */
@Component
@SpringBootApplication
@ComponentScan(basePackages = {
		"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.brat.io" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratImport.class, BaseConfiguration.class, BratService.class }) })
public class BratImport implements CommandLineRunner {

	@Autowired private BratService importService;

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
		importService.importDataset(args[0]);
	}	

}
