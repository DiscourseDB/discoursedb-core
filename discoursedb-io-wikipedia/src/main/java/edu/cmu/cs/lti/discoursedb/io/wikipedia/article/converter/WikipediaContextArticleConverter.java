package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter;

import java.sql.Timestamp;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.hibernate.WikiHibernateUtil;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionApi;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionIterator;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

/**
 * 
 * @author Oliver Ferschke
 *
 */
@Component
public class WikipediaContextArticleConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(WikipediaContextArticleConverter.class);	

	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private WikipediaContextArticleConverterService converterService;

	@Override
	public void run(String... args) throws Exception {
		if(args.length!=5){			
			throw new RuntimeException("Incorrect number of launch parameters.");
		}
		
		logger.trace("Establishing connection to Wikipedia db...");
		DatabaseConfiguration dbconf = new DatabaseConfiguration();
		dbconf.setHost(args[0]);
		dbconf.setDatabase(args[1]);
		dbconf.setUser(args[2]);
		dbconf.setPassword(args[3]);
		dbconf.setLanguage(Language.valueOf(args[4]));
		Wikipedia wiki = new Wikipedia(dbconf);
		RevisionApi revApi = new RevisionApi(dbconf);
		
		List<DiscoursePart> talkPageDPs = discoursePartService.findAllByType(DiscoursePartTypes.TALK_PAGE);
		logger.info("Start mapping context articles for "+talkPageDPs.size()+" existing Talk pages");		

		for(DiscoursePart curTalkPageDP:talkPageDPs){
			
			//determine the timestamp of the first and last contribution of this Talk page
			//this determines the window of article revisions we are going to retrieve
			Timestamp timeOfFirstContrib = null; //TODO  get contribution timestamp
			Timestamp timeOfLastContrib = null; //TODO get contribution timestamp

			//get reference to the article for the given Talk page
			Page article = wiki.getPage(curTalkPageDP.getName());
			int articleId = article.getPageId();
			
			//get primary keys for first and last revision in the window
			List<Timestamp> revTimestamps = revApi.getRevisionTimestampsBetweenTimestamps(article.getPageId(), timeOfFirstContrib, timeOfLastContrib);			
			int firstRevPK = revApi.getRevision(articleId, revTimestamps.get(0)).getPrimaryKey();
			int lastRevPK =  revApi.getRevision(articleId, revTimestamps.get(revTimestamps.size()-1)).getPrimaryKey();

			//create revision iterator that iterates over the article revisions between the two provided contribution timestamps
			RevisionIterator articleRevIt = new RevisionIterator(revApi.getRevisionApiConfiguration(),firstRevPK,lastRevPK, revApi.getConnection());
			
			//start mapping the article revisions 
			converterService.mapContextArticleRevisions(curTalkPageDP, article, articleRevIt);
		}
		
		logger.info("Finished mapping context articles.");

		//manually close the hibernate session for the Wikipedia connection which is not managed by Spring
		WikiHibernateUtil.getSessionFactory(dbconf).close();
	}

}