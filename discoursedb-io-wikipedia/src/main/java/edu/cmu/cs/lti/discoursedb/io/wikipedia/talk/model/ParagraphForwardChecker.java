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
		TopicExtractor topicExtractor = new TopicExtractor("Talk: "+searchToRevId.getArticleID(),searchToRevId.getRevisionID());
		
		int firstRevPK = revApi.getFirstRevisionPK(searchToRevId.getArticleID());
		int lastRevPK = searchToRevId.getPrimaryKey();
		this.revIt = new RevisionIterator(revApi.getRevisionApiConfiguration(), firstRevPK, lastRevPK);
		while(revIt.hasNext()){			
			Revision curRev = revIt.next();		
			if(curRev.getContributorId()==null||(curRev.getContributorId()!=null)&&curRev.getContributorId()>0&&!revApi.getUserGroups(curRev.getContributorId()).contains("bot")){
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
	 * @param para the paragraph to look for
	 * @return true, if meta info was added. false, otherwise
	 */
	public boolean addMetaInfo(TalkPageParagraph para) throws WikiApiException{
		if(parToRevMap.containsKey(para.getText())){
			Revision rev = parToRevMap.get(para.getText());
			para.setContributor(rev.getContributorName());
			para.setTimestamp(rev.getTimeStamp());
			para.setRevisionId(rev.getRevisionID());
			return true;
		}
		return false;
	}
	
}
