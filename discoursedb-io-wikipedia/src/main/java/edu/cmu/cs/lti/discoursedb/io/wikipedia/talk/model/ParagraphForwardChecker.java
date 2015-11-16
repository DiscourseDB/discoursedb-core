package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.util.Map;
import java.util.TreeMap;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiInitializationException;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.Revision;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionApi;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionIterator;

/**
 * Retrieves contributor and timestamp for a given Paragraph by 
 * performing forward checking in the revision history.
 * 
 * @author Oliver Ferschke
 *
 */
public class ParagraphForwardChecker {
	
	//Revision Cache - is built once per provided talk page revision
	private Map<String,Revision> parToRevMap = null;
	private RevisionIterator revIt;
	
	public ParagraphForwardChecker(RevisionApi revApi, Revision searchToRevId) throws WikiInitializationException, WikiApiException{
		parToRevMap = new TreeMap<String,Revision>();
		TopicExtractor topicExtractor = new TopicExtractor();
		
		int firstRevPK = revApi.getFirstRevisionPK(searchToRevId.getArticleID());
		int lastRevPK = searchToRevId.getPrimaryKey();
		this.revIt = new RevisionIterator(revApi.getRevisionApiConfiguration(), firstRevPK, lastRevPK);
		while(revIt.hasNext()){			
			Revision curRev = revIt.next();		
			if(curRev.getContributorId()==null||(curRev.getContributorId()!=null&&curRev.getContributorId()>0&&!revApi.getUserGroups(curRev.getContributorId()).contains("bot"))){
				for(Topic t:topicExtractor.getTopics(curRev.getRevisionText())){
					for(TalkPageParagraph tpp:t.getParagraphs()){					
						if(!parToRevMap.containsKey(tpp.getText())) //we need this check, because we only want the first occurrence and don't want to update the map entry 
						{ 
							parToRevMap.put(tpp.getText(),curRev);							
						}
					}			
				}						
			}
		}	
	}
	
	/**
	 * @param par the paragraph to look for
	 * @return true, if meta info was added. false, otherwise
	 */
	public boolean addMetaInfo(RevisionApi revApi, TalkPageParagraph par) throws WikiApiException{
		if(parToRevMap.containsKey(par.getText())){
			Revision rev = parToRevMap.get(par.getText());
			par.setContributor(rev.getContributorName());
			par.setTimestamp(rev.getTimeStamp());
			par.setRevisionId(rev.getRevisionID());
			par.setContributorIsBot(revApi.getUserGroups(rev.getContributorId()).contains("bot"));
			return true;
		}
		return false;
	}
	
}
