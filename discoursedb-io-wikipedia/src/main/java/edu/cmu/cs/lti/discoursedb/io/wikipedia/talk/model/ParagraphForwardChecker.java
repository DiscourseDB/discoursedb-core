package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.sql.Timestamp;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.wikipedia.Wiki;

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
	
	private static final Logger logger = LogManager.getLogger(ParagraphForwardChecker.class);

	
	//Revision Cache - is built once per provided talk page revision
	private Map<String,Revision> parToRevMap = null;
	private RevisionIterator revIt;
	
	/**
	 * Creates a new ParagraphForwardChecker for the given Talk page revision using the JWPL Revision Iterator.
	 * It builds a TreeMap that maps paragraphs to the revision in which the paragraph first appeared.
	 * 
	 * @param revApi a connection to the revision database used to initialize the RevisionIterator
	 * @param searchToRevId the search limit for the revision
	 * @throws WikiInitializationException in case the database cannot be accessed
	 * @throws WikiApiException in case the database cannot be accessed
	 */
	public ParagraphForwardChecker(RevisionApi revApi, Revision searchToRevId) throws WikiInitializationException, WikiApiException{
		parToRevMap = new TreeMap<String,Revision>();
		TopicExtractor topicExtractor = new TopicExtractor();
		
		int firstRevPK = revApi.getFirstRevisionPK(searchToRevId.getArticleID());
		int lastRevPK = searchToRevId.getPrimaryKey();
		this.revIt = new RevisionIterator(revApi.getRevisionApiConfiguration(), firstRevPK, lastRevPK, revApi.getConnection());
		double revisionsToProcess = searchToRevId.getRevisionCounter();
		double curRevNumber = 1;
		int logPercent = 0; 
       	logger.debug("Processing "+revisionsToProcess+"");
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
   	        int curPercent=(int)Math.round(((curRevNumber++)/revisionsToProcess)*100);   	        
   	        if(curPercent%10==0&&curPercent!=logPercent){
   	        	System.out.println(curPercent+ "% of "+revisionsToProcess+" revisions processed");
   	        	logPercent=curPercent;
   	        }
		}	
	}

	/**
	 * Alternative ParagraphForwardChecker using the MediaWiki API.
	 * 
	 * It builds a TreeMap that maps paragraphs to the revision in which the paragraph first appeared.
	 * 
	 * @param revApi a connection to the revision database used to initialize the RevisionIterator
	 * @param searchToRevId the search limit for the revision
	 * @throws WikiInitializationException in case the database cannot be accessed
	 * @throws WikiApiException in case the database cannot be accessed
	 */
	public ParagraphForwardChecker(String pageTitle) throws Exception{
		parToRevMap = new TreeMap<String,Revision>();
		TopicExtractor topicExtractor = new TopicExtractor();
		Wiki wiki = null;
		wiki = new Wiki("en.wikipedia.org"); 
		// wiki.login("", ""); // no login for reading necessary
		Wiki.Revision[] revs = wiki.getPageHistory(pageTitle);
		double revisionsToProcess = revs.length;
		double curRevNumber = 1;
		int logPercent = 0; 
       	logger.debug("Processing "+revisionsToProcess+"");
		for(Wiki.Revision rev:revs){					
				for(Topic t:topicExtractor.getTopics(rev.getText())){
					for(TalkPageParagraph tpp:t.getParagraphs()){					
						if(!parToRevMap.containsKey(reduceToChars(tpp.getText()))) 
						{ 
							Revision revProxy = new Revision(0);
							revProxy.setContributorName(rev.getUser());
							revProxy.setTimeStamp(new Timestamp(rev.getTimestamp().getTimeInMillis()));
							revProxy.setComment(rev.getSummary());
							revProxy.setRevisionID((int)rev.getRevid());
							parToRevMap.put(reduceToChars(tpp.getText()),revProxy);							
						}
					}			
			}
   	        int curPercent=(int)Math.round(((curRevNumber++)/revisionsToProcess)*100);   	        
   	        if(curPercent%10==0&&curPercent!=logPercent){
   	        	System.out.println(curPercent+ "% of "+revisionsToProcess+" revisions processed");
   	        	logPercent=curPercent;
   	        }
		}
		wiki.logout();		
	}


	/**
	 * Retrieves meta information from the revision of origin of the given
	 * paragraph by looking up the paragraph text in the map generated by the
	 * forwardchecker.
	 * 
	 * @param par
	 *            the paragraph to look for
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
