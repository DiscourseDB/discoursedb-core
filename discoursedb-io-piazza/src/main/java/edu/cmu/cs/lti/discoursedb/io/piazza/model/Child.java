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
package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Represents a Content.Child in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Child {

	private Date created;

	private Date updated;

	private String id;
	
	private String uid;

	private String anon;
	
	private String subject;

	private String type;

	@JsonProperty("no_upvotes")
	private int numUpvotes;

	@JsonProperty("no_answer")
	private int numAnswers;

	@JsonProperty("d-bucket")
	private String dBucket;

	@JsonProperty("bucket_name")
	private String bucketName;

	@JsonProperty("bucket_order")
	private int bucketOrder;
	
	@JsonProperty("tag-endorse")
	private List<TagEndorse> tagEndorse;
		
	private List<String> folders;
	
	private List<History> history;
	
	private Config config;
	
	private List<Child> children;
}
