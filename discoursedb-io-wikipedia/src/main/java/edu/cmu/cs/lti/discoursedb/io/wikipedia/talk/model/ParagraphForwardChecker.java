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
						if(!parToRevMap.containsKey(reduceToChars(tpp.getText()))) 
						{ 
							parToRevMap.put(reduceToChars(tpp.getText()),curRev);							
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
	public boolean addMetaInfo(TalkPageParagraph par) throws WikiApiException{
		if(parToRevMap.containsKey(reduceToChars(par.getText()))){
			Revision rev = parToRevMap.get(reduceToChars(par.getText()));
			par.setContributor(rev.getContributorName());
			par.setTimestamp(rev.getTimeStamp());
			par.setRevisionId(rev.getRevisionID());
			return true;
		}
		return false;
	}

	/**
	 * Removes all nonalphanumeric characters from a String including whitespaces
	 * 
	 * @param input
	 * @return
	 */
	private String reduceToChars(String input){
		return input.replaceAll("[^A-Za-z0-9]", "");
	}
	
}
