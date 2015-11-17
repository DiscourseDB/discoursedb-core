package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.util;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.sweble.wikitext.engine.CompiledPage;
import org.sweble.wikitext.engine.Compiler;
import org.sweble.wikitext.engine.CompilerException;
import org.sweble.wikitext.engine.PageId;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;
import org.sweble.wikitext.lazy.LinkTargetException;

import de.fau.cs.osr.ptk.common.AstVisitor;
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
	
    /**
	 * Extracts sections (without title) from Wikitext.
	 *
	 * @param text article text with wiki markup
	 * @param title article title
	 * @param revision the revision id
	 * @return list of ExtractedSections
	 * @throws CompilerException if the wiki page could not be compiled by the parser
	 */
	@SuppressWarnings("unchecked")
	public static List<ExtractedSection> getSectionsWithSweble(String text, String title, long revision) throws LinkTargetException, CompilerException, FileNotFoundException, JAXBException{
		return (List<ExtractedSection>) parsePage(new SwebleSectionWithParagraphExtractor(), text, title, revision);
	}


	/**
	 * Parses the page with the Sweble parser using a SimpleWikiConfiguration
	 * and the provided visitor.
	 *
	 * @return the parsed page. The actual return type depends on the provided
	 *         visitor. You have to cast the return type according to the return
	 *         type of the go() method of your visitor.
	 * @throws CompilerException if the wiki page could not be compiled by the parser
	 */
	private static Object parsePage(AstVisitor v, String text, String title, long revision) throws LinkTargetException, CompilerException, FileNotFoundException, JAXBException{
		// Use the provided visitor to parse the page
		return v.go(getCompiledPage(text, title, revision).getPage());
	}

	/**
	 * Returns CompiledPage produced by the SWEBLE parser using the
	 * SimpleWikiConfiguration.
	 *
	 * @return the parsed page
	 * @throws LinkTargetException
	 * @throws CompilerException if the wiki page could not be compiled by the parser
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	private static CompiledPage getCompiledPage(String text, String title, long revision) throws LinkTargetException, CompilerException, FileNotFoundException, JAXBException
	{
		SimpleWikiConfiguration config = new SimpleWikiConfiguration("classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");

		PageTitle pageTitle = PageTitle.make(config, title);
		PageId pageId = new PageId(pageTitle, revision);
		// Compile the retrieved page
		Compiler compiler = new Compiler(config);
		return compiler.postprocess(pageId, text, null);
	}



}

