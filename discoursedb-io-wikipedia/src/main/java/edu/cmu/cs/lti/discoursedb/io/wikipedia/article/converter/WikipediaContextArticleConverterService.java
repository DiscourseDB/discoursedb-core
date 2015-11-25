package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.Revision;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Context;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContextService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContextTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;

/**
 * This service maps pre-segmented TalkPage objects to DiscourseDB entities 
 * 
 * @author Oliver Ferschke
 */
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class WikipediaContextArticleConverterService{

//	private static final Logger logger = LogManager.getLogger(WikipediaContextArticleConverterService.class);
	
	@Autowired private ContentService contentService;
	@Autowired private DiscourseService discourseService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private ContributionService contributionService;
	@Autowired private UserService userService;
	@Autowired private ContextService contextService;
	
	public Object[] mapContext(DiscoursePart curTalkPageDP) throws WikiApiException{
		/*
		 * Load Contributions for the given Talk page and determine the time of
		 * the first and the last contribution.
		 */
		List<Contribution> tpContribs = new ArrayList<>();
		Date timeOfFirstContrib = null;
		Date timeOfLastContrib = null;
		//we need references to all contributions anyway, so we can determine the time of first/last contrib in TP 
		//while we load the contributions rather than making an extra query
		for(DiscoursePart curDiscussionDP:discoursePartService.findChildDiscourseParts(curTalkPageDP, DiscoursePartRelationTypes.TALK_PAGE_HAS_DISCUSSION)){
			for(Contribution contrib:contributionService.findAllByDiscoursePart(curDiscussionDP)){
				if(timeOfFirstContrib==null&&timeOfLastContrib==null){
					timeOfFirstContrib=contrib.getStartTime();
					timeOfLastContrib=contrib.getStartTime();
				}else{
					if(contrib.getStartTime().before(timeOfFirstContrib)){
						timeOfFirstContrib=contrib.getStartTime();
					}
					if(contrib.getStartTime().after(timeOfLastContrib)){
						timeOfLastContrib=contrib.getStartTime();						
					}					
				}
				tpContribs.add(contrib);
			}
		}

		//create context entity for the previously created content entities
		Context curTPContext = contextService.createTypedContext(ContextTypes.ARTICLE);
		curTPContext.setStartTime(timeOfFirstContrib);
		curTPContext.setEndTime(timeOfLastContrib);		
		
		//connect context with all contributions of the corresponding Talk page
		for(Contribution contrib:tpContribs){
			contextService.addContributionToContext(curTPContext, contrib);
		}
		return new Object[]{timeOfFirstContrib, timeOfLastContrib, curTPContext.getId()};
	}

	
	/**
	 * Maps a single revision to a content entity in DiscourseDB.
	 * 
	 * @param discourse the discourse the content is part of (for user generation) 
	 * @param curArticleRev the revision to map
	 * @param articleTitle the title of the article the talk page belongs to
	 * @param curRev a reference to the current content entity to be mapped (is usually null)
	 * @param prevRev a reference to the previous content entity (may be null in the beginning)
	 * @param firstRev a reference to the first content entity (may be null in the beginning)
	 * @throws WikiApiException
	 */
	public Long[] mapRevision(Long discourseId, Revision curArticleRev, String articleTitle, Long prevContentId, Long firstContentId) throws WikiApiException{
		User curUser = userService.createOrGetUser(discourseService.findOne(discourseId), curArticleRev.getContributorName());			
				
		Content curRev = contentService.createContent();
		curRev.setText(curArticleRev.getRevisionText());
		curRev.setAuthor(curUser);
		curRev.setStartTime(curArticleRev.getTimeStamp());
		curRev.setTitle(articleTitle);
		
		//create links between elements of the doubly linked content list
		if(prevContentId!=null){
			Content prevRev = contentService.findOne(prevContentId); //FIXME: this causes the following INFO message: c.m.v2.c3p0.stmt.GooGooStatementCache    : Multiply prepared statement! select content0_.id_content as id_conte1_8_0_......
			prevRev.setEndTime(curRev.getStartTime());
			prevRev.setNextRevision(curRev);
			curRev.setPreviousRevision(prevRev);
		}		
		return new Long[]{curRev.getId(), firstContentId==null?curRev.getId():firstContentId};
	}

}