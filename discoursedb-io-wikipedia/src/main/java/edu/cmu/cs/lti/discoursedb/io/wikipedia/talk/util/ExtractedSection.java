package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.util;

import java.util.ArrayList;
import java.util.List;

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