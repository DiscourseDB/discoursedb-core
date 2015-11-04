package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.NotImplementedException;

import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.Revision;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionApi;

public class TalkPage {

	private Revision tpBaseRevision = null;
	public Revision getTpBaseRevision() {
		return tpBaseRevision;
	}

	private List<Topic> topics = null;
	public List<Topic> getTopics(){
		return topics;
	}	

	private ParagraphForwardChecker checker = null;
	private RevisionApi revApi = null;
	private boolean aggregateParagraphs;
	
	/**
	 * @param revApi RevisionApi instance
	 * @param rev talk page revision to process
	 * @param aggregateParagraphs whether to aggregate paragraphs to turns (true) or to consider paragraphs as turns on their own (false)
	 */
	public TalkPage(RevisionApi revApi, int revId, boolean aggregateParagraphs){
		this.revApi=revApi;
		this.aggregateParagraphs=aggregateParagraphs;

		try{
			tpBaseRevision = revApi.getRevision(revId);			
		}catch(WikiApiException e){
			System.err.println("Error checking revisions of origin for paragraphs. Could not process revision. Error accessing Wikipedia database with revision API");
			e.printStackTrace();			
		}
		if(aggregateParagraphs){
			//TODO this mode is not supported
			throw new NotImplementedException("Paragraph aggregation not yet supported");
		}
		_segmentParagraphs();
		_buildTurns();
	}	
	
	/**
	 * @param revApi RevisionApi instance
	 * @param rev talk page revision to process
	 * @param aggregateParagraphs whether to aggregate paragraphs to turns (true) or to consider paragraphs as turns on their own (false)
	 */
	public TalkPage(RevisionApi revApi, Revision rev, boolean aggregateParagraphs){
		this.tpBaseRevision = rev;
		this.revApi=revApi;
		this.aggregateParagraphs=aggregateParagraphs;
		if(aggregateParagraphs){
			//TODO this mode is not supported
			throw new NotImplementedException("Paragraph aggregation not yet supported");
		}
		_segmentParagraphs();
		_buildTurns();
	}
	
	
	public void removeTurnsBeforeTimestamp(Timestamp ts){
		Iterator<Topic> topicIter = topics.iterator();
		while(topicIter.hasNext()){
			Topic t = topicIter.next();
			for(TalkPageParagraph curPar:t.getParagraphs()){
				if(curPar!=null&&curPar.getTimestamp()!=null&&curPar.getTimestamp().before(ts)){
					t.removeParagraph(curPar);
				}
			}
			List<Turn> turns = new ArrayList<Turn>();
			turns.addAll(t.getUserTurns()); 
			for(Turn curTurn:turns){
				if(curTurn!=null&&curTurn.getTimestamp()!=null&&curTurn.getTimestamp().before(ts)){
					t.removeUserTurn(curTurn);
				}				
			}
			if(t.getUserTurns().isEmpty()){
				topicIter.remove();
			}
		}
	}
	
	/**
	 * Segments the TalkPage into paragraphs and extracts meta information for each paragraph
	 */
	private void _segmentParagraphs(){
		try{
			//create a new forward checker for the given revision. 
			//this takes a while, because it builds up a revision cache from the database
			checker = new ParagraphForwardChecker(revApi, tpBaseRevision);

			//segment pages into topics and paragraphs
			TopicExtractor tExt = new TopicExtractor("Talk: "+tpBaseRevision.getArticleID(),tpBaseRevision.getRevisionID());			
			this.topics = tExt.getTopics(tpBaseRevision.getRevisionText());
			
			//extract meta information for paragraphs from revision history
			for(Topic t:topics){
				for(TalkPageParagraph tpp:t.getParagraphs()){
					checker.addMetaInfo(tpp);				
				}
			}
		}catch(WikiApiException e){
			System.err.println("Error checking revisions of origin for paragraphs. Could not process revision. Error accessing Wikipedia database with revision API");			
			e.printStackTrace();
		}		
	}
	
	/**
	 * Aggregates extracted paragraphs to turns.
	 */
	private void _buildTurns(){
		//work through all topics
		for(Topic t:topics){
			int curTurnNumInTopic = 1;
			Set<TalkPageParagraph> pars = t.getParagraphs(); //this is a sorted set of pars
			if(aggregateParagraphs){
				//heuristically aggregate neighboring paragraphs to turns
				//TODO implement				
			}else{
				//no aggregation, so we consider one paragraph = one turn
				for(TalkPageParagraph curPar:pars){
					Turn curTurn = new Turn();
					curTurn.setBegin(curPar.getBegin());
					curTurn.setEnd(curPar.getEnd());
					curTurn.setContributor(curPar.getContributor());
					curTurn.setRevisionId(curPar.getRevisionId());
					curTurn.setIndentAmount(curPar.getIndentAmount());
					curTurn.setText(curPar.getText());
					curTurn.setTimestamp(curPar.getTimestamp());
					curTurn.setTurnNr(curTurnNumInTopic++);
					t.addUserTurn(curTurn);
				}
			}

			//update the newly built turns with reference information
			//might not make too much sense if we didn't aggregate
			new ReferenceExtractor().setReferences(topics); 
			
		}
	}
	
}
