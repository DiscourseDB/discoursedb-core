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
import edu.cmu.cs.lti.discoursedb.io.wikipedia.article.model.ContextTransactionData;

/**
 * This service maps pre-segmented TalkPage objects to DiscourseDB entities 
 * 
 * @author Oliver Ferschke
 */
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class WikipediaContextArticleConverterService{

	@Autowired private ContentService contentService;
	@Autowired private DiscourseService discourseService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private ContributionService contributionService;
	@Autowired private UserService userService;
	@Autowired private ContextService contextService;
	
	public ContextTransactionData mapContext(DiscoursePart curTalkPageDP) throws WikiApiException{
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
				if(contrib.getStartTime()!=null){
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
				}
				tpContribs.add(contrib);
			}
		}

		//create context entity for the previously created content entities
		if(timeOfFirstContrib!=null&&timeOfLastContrib!=null){
			Context curTPContext = contextService.createTypedContext(ContextTypes.ARTICLE);
			curTPContext.setStartTime(timeOfFirstContrib);
			curTPContext.setEndTime(timeOfLastContrib);		
			
			//connect context with all contributions of the corresponding Talk page
			for(Contribution contrib:tpContribs){
				contextService.addContributionToContext(curTPContext, contrib);
			}			
			return new ContextTransactionData(timeOfFirstContrib, timeOfLastContrib, curTPContext.getId());
		}else{
			return new ContextTransactionData();
			
		}
	}

	
	/**
	 * Maps a single revision to a content entity in DiscourseDB.
	 * 
	 * @param discourse the discourse the content is part of (for user generation) 
	 * @param curArticleRev the revision to map
	 * @param articleTitle the title of the article the talk page belongs to
	 */
	public Long mapRevision(Long discourseId, Revision curArticleRev, String articleTitle, Long prevRevId){
		User curUser = userService.createOrGetUser(discourseService.findOne(discourseId), curArticleRev.getContributorName());			
				
		Content curRev = contentService.createContent();
		curRev.setText(curArticleRev.getRevisionText());
		curRev.setAuthor(curUser);
		curRev.setStartTime(curArticleRev.getTimeStamp());
		curRev.setTitle(articleTitle);

		if(prevRevId!=null){
			Content prev = contentService.findOne(prevRevId);
			prev.setNextRevision(curRev);
			prev.setEndTime(curRev.getStartTime());
			curRev.setPreviousRevision(prev);
			contentService.save(prev);
		}
		contentService.save(curRev);

		return curRev.getId();
	}
		
	/**
	 * Update references to first and last element 
	 */
	public Context updateContext(Long contextId, Long firstContentId, Long lastContentId){
		Context ctx = contextService.findOne(contextId);
		Content first = contentService.findOne(firstContentId);
		Content last = contentService.findOne(lastContentId);
		ctx.setFirstRevision(first);
		ctx.setCurrentRevision(last);
		ctx.setStartTime(first.getStartTime());
		ctx.setEndTime(last.getEndTime());
		return contextService.save(ctx);
	}

}