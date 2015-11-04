package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.util;

/**
 * Derived from the TextConverter class which was published in the
 * Sweble example project provided on
 * http://http://sweble.org by the Open Source Research Group,
 * University of Erlangen-NÃ¼rnberg under the Apache License, Version 2.0
 * (http://www.apache.org/licenses/LICENSE-2.0)
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.sweble.wikitext.engine.Page;
import org.sweble.wikitext.engine.PageTitle;
import org.sweble.wikitext.engine.utils.SimpleWikiConfiguration;
import org.sweble.wikitext.lazy.LinkTargetException;
import org.sweble.wikitext.lazy.parser.Bold;
import org.sweble.wikitext.lazy.parser.DefinitionDefinition;
import org.sweble.wikitext.lazy.parser.DefinitionList;
import org.sweble.wikitext.lazy.parser.DefinitionTerm;
import org.sweble.wikitext.lazy.parser.ExternalLink;
import org.sweble.wikitext.lazy.parser.InternalLink;
import org.sweble.wikitext.lazy.parser.Italics;
import org.sweble.wikitext.lazy.parser.LinkTarget;
import org.sweble.wikitext.lazy.parser.LinkTitle;
import org.sweble.wikitext.lazy.parser.Paragraph;
import org.sweble.wikitext.lazy.parser.Section;
import org.sweble.wikitext.lazy.parser.Whitespace;
import org.sweble.wikitext.lazy.parser.XmlElementClose;
import org.sweble.wikitext.lazy.parser.XmlElementEmpty;
import org.sweble.wikitext.lazy.parser.XmlElementOpen;
import org.sweble.wikitext.lazy.preprocessor.Template;

import de.fau.cs.osr.ptk.common.AstVisitor;
import de.fau.cs.osr.ptk.common.ast.AstNode;
import de.fau.cs.osr.ptk.common.ast.NodeList;
import de.fau.cs.osr.ptk.common.ast.Text;

/**
 * A visitor that extracts sections with paragraphs from an article AST.
 *
 * @author Oliver Ferschke
 */
public class SwebleSectionWithParagraphExtractor extends AstVisitor
{
	private final SimpleWikiConfiguration config;

	private List<ExtractedSection> sections;

	private StringBuilder parBuilder = new StringBuilder();
	private List<String> curPars;

	// =========================================================================

	/**
	 * Creates a new visitor that extracts anchors of internal links from a
	 * parsed Wikipedia article using the default Sweble config as defined
	 * in WikiConstants.SWEBLE_CONFIG.
	 */
	public SwebleSectionWithParagraphExtractor()
	{
		SimpleWikiConfiguration config=null;
		try{
			config = new SimpleWikiConfiguration("classpath:/org/sweble/wikitext/engine/SimpleWikiConfiguration.xml");
		}catch(IOException e){
			//TODO logger
			e.printStackTrace();
		}catch(JAXBException e){
			//TODO logger
			e.printStackTrace();
		}
		this.config=config;
	}

	/**
	 * Creates a new visitor that extracts anchors of internal links from a
	 * parsed Wikipedia article.
	 *
	 * @param config the Sweble configuration
	 */
	public SwebleSectionWithParagraphExtractor(SimpleWikiConfiguration config)
	{
		this.config = config;
	}

	@Override
	protected boolean before(AstNode node)
	{
		// This method is called by go() before visitation starts
		sections = new ArrayList<ExtractedSection>();
		curPars = new ArrayList<String>();
		return super.before(node);
	}

	@Override
	protected Object after(AstNode node, Object result)
	{
		return sections;
	}

	// =========================================================================

	public void visit(Page n)
	{
		iterate(n);
	}

	public void visit(Whitespace n)
	{
		parBuilder.append(" ");
	}

	public void visit(Bold n)
	{
		iterate(n);
	}

	public void visit(Italics n)
	{
		iterate(n);
	}

	public void visit(ExternalLink link)
	{
		iterate(link.getTitle());
	}

	public void visit(LinkTitle n)
	{
		iterate(n);
	}

	public void visit(LinkTarget n)
	{
		iterate(n);
	}

	public void visit(InternalLink link)
	{
		try
		{
			PageTitle page = PageTitle.make(config, link.getTarget());
			if (page.getNamespace().equals(config.getNamespace("Category"))) {
				return;
			}else{
				String curLinkTitle="";
				for(AstNode n:link.getTitle().getContent()){
					if(n instanceof Text){
						curLinkTitle = ((Text)n).getContent().trim();
					}
				}
				if(curLinkTitle.isEmpty()){
					parBuilder.append(link.getTarget());
				}else{
					parBuilder.append(curLinkTitle);
				}
			}
		}
		catch (LinkTargetException e)
		{
		}

	}
	public void visit(DefinitionList n){
		iterate(n);
	}

	public void visit(DefinitionTerm n){
		iterate(n);
	}

	public void visit(DefinitionDefinition n){
		iterate(n);
	}

	public void visit(XmlElementOpen n){
	}

	public void visit(XmlElementClose n){
	}

	public void visit(XmlElementEmpty n){
	}

	public void visit(AstNode n)
	{
	}

	public void visit(NodeList n)
	{
		iterate(n);
	}

	public void visit(Paragraph par)
	{
		iterate(par);
		String currentParString = parBuilder.toString().trim(); 
		if(!currentParString.isEmpty()){
			curPars.add(currentParString);			
		}
		parBuilder=new StringBuilder();
	}

	public void visit(Template tmpl) throws IOException
	{
//		iterate(tmpl);
	}

	public void visit(Text n)
	{
		parBuilder.append(n.getContent());
	}

	public void visit(Section sect) throws IOException
	{

		String title = null;

		for(AstNode n:sect.getTitle()){
			if(n instanceof Text){
				title = ((Text)n).getContent();
			}
		}
		iterate(sect);

		sections.add(new ExtractedSection(title,curPars));
		curPars = new ArrayList<String>();
	}

	/**
	 * Wraps title and body text of an extraction section
	 *
	 * @author Oliver Ferschke
	 *
	 */
	public class ExtractedSection
	{
		private String title;
		private List<String> paragraphs = new ArrayList<String>();

		public ExtractedSection(String title, List<String> paragraphs){
			this.title=title;
			this.paragraphs=paragraphs;

		}

		public String getTitle()
		{
			return title;
		}
		public void setTitle(String aTitle)
		{
			title = aTitle;
		}

		public List<String> getParagraphs()
		{
			return paragraphs;
		}

		public void setParagraphs(List<String> paragraphs)
		{
			this.paragraphs = paragraphs;
		}

		public void addParagraph(String par){
			this.paragraphs.add(par);
		}
	}
}
