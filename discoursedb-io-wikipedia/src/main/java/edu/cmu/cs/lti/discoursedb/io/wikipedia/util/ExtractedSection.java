package edu.cmu.cs.lti.discoursedb.io.wikipedia.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.tudarmstadt.ukp.wikipedia.parser.NestedListContainer;
import de.tudarmstadt.ukp.wikipedia.parser.Paragraph;

/**
	 * Wraps title and body text of an extraction section
	 *
	 * @author Oliver Ferschke
	 *
	 */
	public class ExtractedSection
	{
		private String title;
		private List<Paragraph> paragraphs = new ArrayList<Paragraph>();

		public ExtractedSection(String title, List<Paragraph> paragraphs){
			this.title=title;
			this.paragraphs=paragraphs;

		}
		
		public ExtractedSection(String title, Collection<NestedListContainer> lists){
			this.title=title;
			addNestedLists(lists);

		}

		public String getTitle()
		{
			return title;
		}
		public void setTitle(String aTitle)
		{
			title = aTitle;
		}

		public List<Paragraph> getParagraphs()
		{
			return paragraphs;
		}

		public void setParagraphs(List<Paragraph> paragraphs)
		{
			this.paragraphs = paragraphs;
		}

		public void addParagraph(Paragraph par){
			this.paragraphs.add(par);
		}

		public void addNestedLists(Collection<NestedListContainer> lists){			
			for(NestedListContainer list:lists){
				Paragraph par = new Paragraph();
				par.setText(list.getText());
				par.setSrcSpan(list.getSrcSpan());
				this.paragraphs.add(par);				
			}
		}

		public void addNestedList(NestedListContainer list){			
			Paragraph par = new Paragraph();
			par.setText(list.getText());
			par.setSrcSpan(list.getSrcSpan());
			this.paragraphs.add(par);
		}
	}