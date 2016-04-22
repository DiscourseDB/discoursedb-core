package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Represents topic in Wikipedia Discussion page
 * 
 * @author chebotar
 * @author ferschke
 *
 */
public class Topic {
	
	private String title;	// Topic name
	private int begin;		// Begin index of the topic
	private int end;		// End index of the topic
	private String text;	// Topic text
	private Set<TalkPageParagraph> paragraphs; // Paragraphs of this topic
	private List<Turn> turns;			 // Turns of this topic
	
	
	/**
	 * Creates Wikipedia Discussion topic
	 */
	public Topic(){
		// Paragraphs are ordered by their position in the Talk page (begin index)
		this.paragraphs = new TreeSet<TalkPageParagraph>(new Comparator<TalkPageParagraph>() {
			public int compare(TalkPageParagraph o1, TalkPageParagraph o2) {
				return o1.getBegin() > o2.getBegin()?1:-1;
			}
		});
		this.turns = new ArrayList<Turn>();
	}
	

	
	/**
	 * @return the text
	 */
	public String getText() {
		return text;
	}

	/**
	 * @return the text
	 */
	public String getTextWithName() {
		return "=="+title+"==\n"+text;
	}


	/**
	 * @param text the text to set
	 */
	public void setText(String text) {
		this.text = text;
	}



	/**
	 * @return a list of turns
	 */
	public List<Turn> getTurns() {
		return turns;
	}



	/**
	 * @param turn the turn to add
	 */
	public void addTurn(Turn turn) {
		this.turns.add(turn);
	}

	/**
	 * @param turn the turn to remove
	 */
	public void removeTurn(Turn turn) {
		turns.remove(turn);
	}

	
	/**
	 * @param turns the turns to set
	 */
	public void setTurns(List<Turn> turns) {
		this.turns = turns;
	}


	/**
	 * @return the paragraphs
	 */
	public Set<TalkPageParagraph> getParagraphs() {
		return paragraphs;
	}


	/**
	 * @param paragraphs the paragraphs to set
	 */
	public void addParagraph(TalkPageParagraph paragraph) {
		this.paragraphs.add(paragraph);
	}

	/**
	 * @param paragraphs the paragraphs to set
	 */
	public void removeParagraph(TalkPageParagraph paragraph) {
		this.paragraphs.remove(paragraph);
	}


	/**
	 * @return the name
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param name the name to set
	 */
	public void setTitle(String title) {
		this.title = title.trim();
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
	
	
}
