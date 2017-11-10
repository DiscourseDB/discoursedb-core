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

