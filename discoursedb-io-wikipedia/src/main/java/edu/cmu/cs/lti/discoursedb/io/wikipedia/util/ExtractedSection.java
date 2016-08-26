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