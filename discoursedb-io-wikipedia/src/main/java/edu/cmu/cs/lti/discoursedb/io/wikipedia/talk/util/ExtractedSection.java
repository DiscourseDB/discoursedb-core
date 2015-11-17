package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.util;

import java.util.ArrayList;
import java.util.List;

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
	}