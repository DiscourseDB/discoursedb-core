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
package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;
import edu.cmu.cs.lti.discoursedb.io.wikipedia.util.ExtractedSection;
import edu.cmu.cs.lti.discoursedb.io.wikipedia.util.WikitextParseUtils;

/**
 * Extracts topics and its paragraphs from Wikipedia Discussion page
 * 
 * @author Oliver Ferschke
 *
 */
public class TopicExtractor {

	public TopicExtractor(){
	}
	
	public List<Topic> getTopics(Page p) throws WikiApiException{
		return getTopics(p.getText());
	}	

	/**
	 * Extracts topics and paragraphs from the discussionPage
	 * @param discussionPageText
	 * @return list of topics
	 * @throws WikiApiException
	 */
	public List<Topic> getTopics(String pageText) throws WikiApiException{
		List<Topic> result = new ArrayList<Topic>();	
		try{
			List<ExtractedSection> sections = WikitextParseUtils.getSectionsWithJWPL(pageText);
			
			for(ExtractedSection section : sections){ // Each section can be a topic
				if(section.getTitle()!=null&&!section.getTitle().isEmpty()){
					Topic topic = new Topic();			
					topic.setTitle(section.getTitle());
														
					for(Paragraph curParsedPar : section.getParagraphs()){
						TalkPageParagraph talkPagePar = new TalkPageParagraph();
						talkPagePar.setText(curParsedPar.getText());
						talkPagePar.setBegin(curParsedPar.getSrcSpan().getStart());
						talkPagePar.setEnd(curParsedPar.getSrcSpan().getEnd());
						talkPagePar.setIndentAmount(countCharsAtStart(curParsedPar.getText(),':'));
						topic.addParagraph(talkPagePar);
						
					}
					topic.setText(combinePars(section.getParagraphs()));
					result.add(topic);					
				}
			}
		}catch(Exception ex){
			System.err.println("Topic extraction error while extracting discussion page: "+ex);
			ex.printStackTrace();
		}
		return result;				
		
	}
	
	/**
	 * Counts char c at the begin of text
	 * 
	 * @param text text to analyze
	 * @param c character to count 
	 * @return number of occurrences of char c at the beginning of string text.
	 */
	public static int countCharsAtStart(String text, char c){
		for(int charCount = 0; charCount<text.length(); charCount++){
			if(text.charAt(charCount) != c) {
				return charCount;
			}
		}
		return 0;
	}
	
	
	/**
	 * Concatenates the provided paragraphs into a single String. 
	 * 
	 * @param parList
	 * @return
	 */
	private String combinePars(List<Paragraph> parList){
		StringBuffer buf = new StringBuffer();
		for(Paragraph par:parList){
			buf.append(par.getText());
			buf.append(System.lineSeparator());
		}
		return buf.toString();
	}
}
