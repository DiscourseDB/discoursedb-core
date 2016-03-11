package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

/**
 * This converter imports discussion contexts, i.e. the changes in the articles while discussions where happening.
 * NOTE: The database might get really big since every revision is stored in full.
 * 
 * To achieve this, you have to have a DiscourseDB database with imported Talk pages.
 * The converter then retrieves all DiscourseParts of the TALK_PAGE type.
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
	
	/**
	 * Launches the SpringBoot application which runs the converter components in the order provided by the Order annotation.
	 * The launch parameters are passed on to each component.
	 * 
	 * @param args <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length==5,"Usage: WikipediaContextArticleConverterApplication <DB_HOST> <DB> <DB_USER> <DB_PWD> <LANGUAGE>");
        SpringApplication.run(WikipediaContextArticleConverterApplication.class, args);       
	}
}
