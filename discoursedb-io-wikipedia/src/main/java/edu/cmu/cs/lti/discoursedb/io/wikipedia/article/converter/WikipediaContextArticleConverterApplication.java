package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * This starter class launches the components necessary for importing an Wikipedia articles that constitute the context for existing Talk page discussions.
 * 
 * That is , the converter retrieves Talk page discussions, retrieves the corresponding article from the provided JWPL database and stores the article revisions as the context to the discussion.
 * 
 * @author Oliver Ferschke
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
