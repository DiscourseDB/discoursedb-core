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
package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * POJO that wraps a comment to a ProsoloBlogPost
 * 
 * @author oliverf
 *
 */
public class ProsoloBlogComment {
	private String datetime;
	private String author;
	private String content;
	private List<ProsoloBlogComment> comments;

	
	public ProsoloBlogComment() {}

	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

	public List<ProsoloBlogComment> getComments() {
		if(comments==null){
			return new ArrayList<>();
		}else{
			return comments;			
		}
	}

	@SuppressWarnings("unchecked")
	public void setComments(List<Map<String, Object>> comments) {
		if(comments!=null){
			List<ProsoloBlogComment> result = new ArrayList<>();
			for(Object curComment:comments){
				ProsoloBlogComment comment = new ProsoloBlogComment();			
				comment.setDatetime((String)((Map<String, Object>)curComment).get("datetime"));
				comment.setContent((String)((Map<String, Object>)curComment).get("content"));
				comment.setAuthor((String)((Map<String, Object>)curComment).get("author"));
				comment.setComments((List<Map<String, Object>>)((Map<String, Object>)curComment).get("comments"));
				result.add(comment);
			}
			this.comments = result;			
		}
	}
	
}
