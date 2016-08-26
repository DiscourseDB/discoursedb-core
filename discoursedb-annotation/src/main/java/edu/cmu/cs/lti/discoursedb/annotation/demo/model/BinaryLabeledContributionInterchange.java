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
package edu.cmu.cs.lti.discoursedb.annotation.demo.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

/**
 * Interchange format for binary labels on arbitray entities
 * 
 * @author Oliver Ferschke
 */
@Data
@JsonPropertyOrder({ "table", "contribId", "contribType", "threadIds","labels", "text" })
public class BinaryLabeledContributionInterchange implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long contribId;

	private String table;

	private String contribType;

	private Set<Long> threadIds = new HashSet<Long>();
	public void addThreadId(Long id) {
		if (id != null) {
			this.threadIds.add(id);
		}
	}

	/**
	 * Change to list if multiple identical labels should be allowed
	 */
	private Set<String> labels = new HashSet<String>();
	public void addLabel(String label) {
		if (label != null) {
			this.labels.add(label);
		}
	}

	private String text;
}
