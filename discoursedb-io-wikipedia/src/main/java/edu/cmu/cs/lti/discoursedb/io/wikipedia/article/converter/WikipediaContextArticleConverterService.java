/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.converter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.Revision;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContextTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.wikipedia.article.model.ContextTransactionData;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * This service maps article revisions to DiscourseDB content entities and connects them via Context entities to 
 * existing Talk page contributions. 
 * 
 * @author Oliver Ferschke
 */
@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class WikipediaContextArticleConverterService{

	private final @NonNull ContentService contentService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull DiscoursePartService discoursePartService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull UserService userService;
	
	/**
	 * Creates a context for a DiscoursePart that represents a Talk page
	 * 
	 * @param curTalkPageDP a DiscoursePart of the TalkPage type
	 * @return a ContextTransactionData object that contains meta information about the created context
	 * @throws WikiApiException in case there was an error accessing the Wikipedia database
	 */
	public ContextTransactionData mapContext(DiscoursePart curTalkPageDP) throws WikiApiException{
		Assert.notNull(curTalkPageDP, "DiscoursePart for representing page cannot be null.");
		
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
			Contribution curTPContext = contributionService.createTypedContext(ContextTypes.ARTICLE);
			curTPContext.setStartTime(timeOfFirstContrib);
			curTPContext.setEndTime(timeOfLastContrib);		
			
			//connect context with all contributions of the corresponding Talk page
			for(Contribution contrib:tpContribs){
				contributionService.addContextToContribution(contrib,curTPContext);
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
	 * @param prevRevId the id of the previous revision. Might be null if there was no previous revision.
	 */
	public Long mapRevision(Long discourseId, Revision curArticleRev, String articleTitle, Long prevRevId){
		Assert.notNull(discourseId, "Discourse id cannot be null");
		Assert.isTrue(discourseId>0, "Discourse id has to be a positive number");
		Assert.notNull(curArticleRev, "Article revision cannot be null.");
		Assert.hasText(articleTitle, "Article title cannot be empty.");
		//Note: prevRevId is allowed to be null (in case of the first revision)
				
		Content curRev = contentService.createContent();
		curRev.setText(curArticleRev.getRevisionText());
		curRev.setStartTime(curArticleRev.getTimeStamp());
		curRev.setTitle(articleTitle);

		//there seem to be cases where the user name is null, so we can only assign a user if we have the name
		String curUserName = curArticleRev.getContributorName();
		if(curUserName!=null&&!curUserName.isEmpty()){
			discourseService.findOne(discourseId).ifPresent(discourse -> 
				curRev.setAuthor(userService.createOrGetUser(discourse, curArticleRev.getContributorName())));
		}

		//in case there was a previous revision, retrieve it and connect the Content entity
		if(prevRevId!=null){
			contentService.findOne(prevRevId).ifPresent(prev->{
				prev.setNextRevision(curRev);
				prev.setEndTime(curRev.getStartTime());
				curRev.setPreviousRevision(prev);
				contentService.save(prev);				
			});
		}
		contentService.save(curRev);

		return curRev.getId();
	}
		
	/**
	 * Update references to first and last element 
	 */
	public void updateContext(Long contextId, Long firstContentId, Long lastContentId){
		Assert.notNull(contextId, "Context id cannot be null.");
		Assert.isTrue(contextId>0, "Context id has to be a positive number.");

		contributionService.findOne(contextId).ifPresent(ctx->{
			contentService.findOne(firstContentId).ifPresent(first->{
				ctx.setFirstRevision(first);
				ctx.setStartTime(first.getStartTime());				
			});
			contentService.findOne(lastContentId).ifPresent(last->{
				ctx.setCurrentRevision(last);
				ctx.setEndTime(last.getEndTime());				
			});
			contributionService.save(ctx);
		});
	}

}