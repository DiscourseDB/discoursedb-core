package edu.cmu.cs.lti.discoursedb.io.wikipedia.util;

import java.util.ArrayList;
import java.util.List;

import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParserFactory;

public class WikitextParseUtils
{

	/**
	 * Parses the Talk page using the JWPL MediaWiki Parser.
	 * 
	 * @param text the talk page text with markup
	 * @return a list of extracted sections that contain each contain a list of paragraphs 
	 */
	public static List<ExtractedSection> getSectionsWithJWPL(String text){
		List<ExtractedSection> sections = new ArrayList<>();
		MediaWikiParserFactory pf = new MediaWikiParserFactory();
		pf.setCalculateSrcSpans(true);
		MediaWikiParser parser = pf.createParser();
		ParsedPage pp = parser.parse(text);
		for(Section sec: pp.getSections()){
			ExtractedSection sect = new ExtractedSection(sec.getTitle(), sec.getParagraphs());
			sect.addNestedLists(sec.getNestedLists());			
			sections.add(sect);
		}
		return sections;		
	}

}

