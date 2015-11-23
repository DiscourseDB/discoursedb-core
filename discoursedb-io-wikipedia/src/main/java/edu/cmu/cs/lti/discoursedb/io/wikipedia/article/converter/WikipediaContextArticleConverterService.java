package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionIterator;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;

/**
 * This service maps pre-segmented TalkPage objects to DiscourseDB entities 
 * 
 * @author Oliver Ferschke
 */
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class WikipediaContextArticleConverterService{

	private static final Logger logger = LogManager.getLogger(WikipediaContextArticleConverterService.class);
	
	@Autowired private DiscourseService discourseService;
	@Autowired private DataSourceService dataSourceService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private ContributionService contributionService;
	@Autowired private ContentService contentService;
	@Autowired private UserService userService;
	
	/**
	 * Maps the provided article revisions to a context and corresponding content entities and associates them with the contributions.
	 * 
	 * @param talkPageDP the DiscoursePart that represents the Talk page
	 * @param article the article that constitutes the context of the provided Talk page
	 * @param articleRevs the revisions of the article that are relevant for the context of this Talk page
	 */
	public void mapContextArticleRevisions(DiscoursePart talkPageDP, Page article, RevisionIterator revIt) throws WikiApiException{
		
	}
}