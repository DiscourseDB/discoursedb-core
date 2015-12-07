package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This converter tetrieves all DiscourseParts of TALK_PAGE type.
 * For each TALK PAGE DP, it determines the time of the first and last discussion that is associated with the Talk page.
 * It then retrieves all revisions of the associated article within this time window. These article revisions are mapped as content entities associated with a context entity.
 * Each contribution to the discussion is linked to the context entity.
 * 
 * In case no revision activity was recorded within the discussion window, the singe latest article revision is retrieved that was current when the discussion were going on.
 * No context is created for TALK PAGE entities without any contributions.
 * 
 * Usage: WikipediaContextArticleConverterApplication <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>
 * 
 * @author Oliver Ferschke
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = {"edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.io.wikipedia.article"})
public class WikipediaContextArticleConverterApplication {
	
	private static final Logger logger = LogManager.getLogger(WikipediaContextArticleConverterApplication.class);

	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args 
	 */
	public static void main(String[] args) {
		if(args.length!=5){
			logger.error("Usage: WikipediaContextArticleConverterApplication <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>");
			return;
		}
        SpringApplication.run(WikipediaContextArticleConverterApplication.class, args);       
	}
}
