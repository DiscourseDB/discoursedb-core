package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.io.StringReader;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.HasWord;
import edu.stanford.nlp.process.DocumentPreprocessor;

/**
 * Represents User Turn in Wikipedia Discussion page
 * 
 * @author chebotar
 * @author ferschke
 *
 */
@SuppressWarnings("unused")
public class Turn {
	
	private String contributor; 	// User Turn contributor
	private Timestamp timestamp;	// User Turn timestamp
	private long revisionId;		// id of revision of origin
	private String[] signaturepatternsPTB = new String[]{"-- Signed by","<small> -- Preceding","<span style=\"font-size: smaller;\" class=\"autosigned\">","-LSB- -LSB- Special","-- -LSB- -LSB- Special","-LSB- -LSB- User","-LRB- -LSB- -LSB- Special"}; 
	private String[] signaturepatterns = new String[]{"::--[[User","--[[User","[[User","<small>—Preceding [[Wikipedia:Signatures|unsigned]] comment"}; 

	private int begin;				// Begin index of this Turn
	private int end;				// End index of this Turn
	private int indentAmount;		// Indent Amount of this Turn (Indent amount of the first Turn part if 
									// UserTurns are not splitted on different indentation)
	
	private int turnNr;				// Turn number in its Topic
	
	private Turn nextPart;		// Next part of this Turn if partial UserTurns are combined
	private Turn previousPart;	// Previous part of this Turn if partial UserTurns are combined
	private Turn reference;		// Reference of this Turn if it references other Turn
	
	private String text;			// Text of this user turn

	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	public String getTextWithoutSignature(){
		String tmp = text;
		for(String pattern:signaturepatternsPTB){
			if(tmp.contains(pattern)){
				tmp= tmp.split(pattern)[0];					
			}
		}
		return tmp;
		
	}
	
	/**
	 * 
	 * @return list of sentences representing the turn text
	 */
	public List<String> getSentences(boolean removeSignatures) {
		List<String> sentences = new ArrayList<>();
		DocumentPreprocessor prep = new DocumentPreprocessor(new StringReader(text));
		
		for (List<HasWord> sentence : prep) {			
			StringBuilder sb = new StringBuilder();
	        for (HasWord word : sentence) {
	        	CoreLabel cl = (CoreLabel) word;
	            sb.append(cl.get(CoreAnnotations.OriginalTextAnnotation.class));
	            sb.append(' ');
	        }
			String resSentence =sb.toString().trim();
			if(removeSignatures){
				for(String pattern:signaturepatternsPTB){
					if(resSentence.contains(pattern)){
						resSentence= resSentence.split(pattern)[0];					
					}
				}
				
			}
	        if(!resSentence.trim().isEmpty()&&resSentence.matches(".*[a-zA-Z]+.*")){
				sentences.add(resSentence);	        	
	        }
		}
		
		return sentences;
	}


	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}


	/**
	 * @return the reference
	 */
	public Turn getReference() {
		return reference;
	}


	/**
	 * @param reference the reference to set
	 */
	public void setReference(Turn reference) {
		this.reference = reference;
	}


	/**
	 * @return the nextPart
	 */
	public Turn getNextPart() {
		return nextPart;
	}


	/**
	 * @param nextPart the nextPart to set
	 */
	public void setNextPart(Turn nextPart) {
		this.nextPart = nextPart;
	}


	/**
	 * @return the previousPart
	 */
	public Turn getPreviousPart() {
		return previousPart;
	}


	/**
	 * @param previousPart the previousPart to set
	 */
	public void setPreviousPart(Turn previousPart) {
		this.previousPart = previousPart;
	}

	/**
	 * @return the contributor
	 */
	public String getContributor() {
		return contributor;
	}


	/**
	 * @param contributor the contributor to set
	 */
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}


	/**
	 * @return the timestamp
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}


	/**
	 * @param timestamp the timestamp to set
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}


	/**
	 * @return the begin
	 */
	public int getBegin() {
		return begin;
	}


	/**
	 * @param begin the begin to set
	 */
	public void setBegin(int begin) {
		this.begin = begin;
	}


	/**
	 * @return the end
	 */
	public int getEnd() {
		return end;
	}


	/**
	 * @param end the end to set
	 */
	public void setEnd(int end) {
		this.end = end;
	}


	/**
	 * @return the indentAmount
	 */
	public int getIndentAmount() {
		return indentAmount;
	}


	/**
	 * @param indentAmount the indentAmount to set
	 */
	public void setIndentAmount(int indentAmount) {
		this.indentAmount = indentAmount;
	}


	/**
	 * @return the turnNr starting at 1
	 */
	public int getTurnNr() {
		return turnNr;
	}


	/**
	 * @param turnNr the turnNr to set
	 */
	public void setTurnNr(int turnNr) {
		this.turnNr = turnNr;
	}

	public long getRevisionId() {
		return revisionId;
	}

	public void setRevisionId(long revisionId) {
		this.revisionId = revisionId;
	}
	public boolean isValid(){
		return  getContributor() != null && 
				getTimestamp() != null && 
				!getContributor().isEmpty();
	}

}
