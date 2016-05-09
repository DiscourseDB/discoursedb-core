package edu.cmu.cs.lti.discoursedb.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.hateoas.config.EnableEntityLinks;
import org.springframework.scheduling.annotation.EnableAsync;

import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratExport;
import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratImport;
import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratService;
import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;

/**
 * A SpringBootApplication that launches a server that hosts the API.
 *
 */
@SpringBootApplication
@EnableEntityLinks
@EnableAsync
@EntityScan(basePackageClasses = { DiscourseApiStarter.class, Jsr310JpaConverters.class })
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb"},  excludeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratExport.class, BratImport.class }) })
public class DiscourseApiStarter {

	public static void main(String[] args) {
		SpringApplication.run(DiscourseApiStarter.class, args);
	}

}
