package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.Revision;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionApi;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionIterator;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Context;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContextService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.wikipedia.article.model.ContextTransactionData;

/**
 * 
 * @author Oliver Ferschke
 *
 */
@Component
public class WikipediaContextArticleConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(WikipediaContextArticleConverter.class);

	@Autowired
	private DiscoursePartService discoursePartService;
	@Autowired
	private WikipediaContextArticleConverterService converterService;
	@Autowired
	private ContextService contextService;
	@Autowired
	private DiscourseService discourseService;
	@Autowired
	private ContentService contentService;

	@Override
	public void run(String... args) throws Exception {
		if (args.length != 5) {
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
		logger.info("Start mapping context articles for " + talkPageDPs.size() + " existing Talk pages");

		for (DiscoursePart curTalkPageDP : talkPageDPs) {
			ContextTransactionData contextTransactionData =  converterService.mapContext(curTalkPageDP);

			//retrieve the discourse for the provided TalkPage
			Optional<Discourse> discourse = discourseService.findOne(curTalkPageDP);
			if(!discourse.isPresent()){
				logger.error("Could not retrieve the Discourse for the provided Talk Page DiscoursePart");
				continue;
			}
			
			// get reference to the article for the given Talk page
			Page article = wiki.getPage(curTalkPageDP.getName());
			int articleId = article.getPageId();

			// get primary keys for first and last revision in the window
			List<Timestamp> revTimestamps = revApi.getRevisionTimestampsBetweenTimestamps(articleId, new Timestamp(contextTransactionData.getFirstContent().getTime()), new Timestamp(contextTransactionData.getLastContent().getTime()));
			int firstRevCounter = revApi.getRevision(articleId, revTimestamps.get(0)).getRevisionCounter();
			int lastRevPK = revApi.getRevision(articleId, revTimestamps.get(revTimestamps.size() - 1)).getPrimaryKey();

			// create revision iterator that iterates over the article revisions
			// between the two provided contribution timestamps
			RevisionIterator articleRevIt = new RevisionIterator(revApi.getRevisionApiConfiguration(), revApi.getFirstRevisionPK(articleId), lastRevPK, revApi.getConnection());
			articleRevIt.setShouldLoadRevisionText(false);

			//Process revisions and create content objects.
			//The content objects will represent a doubly linked list and they are eventually associated with the same context
			List<Long> ids = new ArrayList<>(); //keeps track of the previous and first revision 
			while(articleRevIt.hasNext()){
				Revision curArticleRev = articleRevIt.next();
				if(curArticleRev.getRevisionCounter()<firstRevCounter){
					// the RevisionIterator can only begin at the first revision and
					// not an in-between revision, so we have to skip the revisions
					// until the revisioncounter fits the beginning of our target window
					// since we set the iterator to not reconstruct revisions automatically,
					// this shouldn't be expensive
					continue;
				}
				ids.add(converterService.mapRevision(discourse.get().getId(),curArticleRev,article.getTitle().getPlainTitle()));
			}		
			
//TODO		linking the content entities causes problems
			converterService.linkRevisions(ids);
			
			//update reference to first and last content element 
			//start and end time are already created
			Context ctx = contextService.findOne(contextTransactionData.getContextId());
			ctx.setFirstRevision(contentService.findOne(ids.get(0)));
			ctx.setCurrentRevision(contentService.findOne(ids.get(ids.size()-1)));
			
		}

		logger.info("Finished mapping context articles.");

		// manually close the hibernate session for the Wikipedia connection
		// which is not managed by Spring
		WikiHibernateUtil.getSessionFactory(dbconf).close();
	}
}