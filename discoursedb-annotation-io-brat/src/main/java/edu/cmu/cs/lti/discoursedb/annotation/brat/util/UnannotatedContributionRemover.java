package edu.cmu.cs.lti.discoursedb.annotation.brat.util;

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
		"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.brat.util" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { UnannotatedContributionRemover.class, BaseConfiguration.class, UtilService.class }) })
public class UnannotatedContributionRemover implements CommandLineRunner {

	@Autowired private UtilService utilService;

	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String... args) {
		Assert.isTrue(args.length > 0, "USAGE: BratThreadImport <list of discourse names>");
		SpringApplication.run(UnannotatedContributionRemover.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		utilService.removeUnannotatedContribs(args);
	}	

}
