package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.wikipedia.api.Page;
import de.tudarmstadt.ukp.wikipedia.api.exception.WikiApiException;
import edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.util.ExtractedSection;
import edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.util.WikitextParseUtils;

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
//			List<ExtractedSection> sections = WikitextParseUtils.getSections(pageText, title, revisionId);
			
			for(ExtractedSection section : sections){ // Each section can be a topic
				if(section.getTitle()!=null&&!section.getTitle().isEmpty()){
					Topic topic = new Topic();			
					topic.setTitle(section.getTitle());
														
					for(String curParContent : section.getParagraphs()){
						TalkPageParagraph par = new TalkPageParagraph();
						par.setText(curParContent);
						par.setIndentAmount(countCharsAtStart(curParContent,':'));
						topic.addParagraph(par);
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
	private String combinePars(List<String> parList){
		StringBuffer buf = new StringBuffer();
		for(String par:parList){
			buf.append(par);
			buf.append(System.lineSeparator());
		}
		return buf.toString();
	}
}
