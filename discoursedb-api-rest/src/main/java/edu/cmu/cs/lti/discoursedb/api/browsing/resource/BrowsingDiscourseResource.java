/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Authors: Oliver Ferschke and Chris Bogart
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
package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.browsing.controller.BrowsingRestController;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;

public class BrowsingDiscourseResource extends ResourceSupport {
	
	private String name;
	private Long discourseId;
	private Discourse d;
	private Map<String,Long> countsByType;
		
	public BrowsingDiscourseResource(Discourse d, DiscoursePartRepository dpr) {
		this.d = d;
		this.name = d.getName();
		this.discourseId = d.getId();
		List<Object[]> counts = dpr.countsByTypeAndDiscourseNative(d.getId()); 
		this.countsByType = new HashMap<>();
		counts.forEach(c  -> {
			String typename = c[0].toString();
			this.countsByType.put(typename, ((BigInteger)c[1]).longValue());
			this.add(BrowsingRestController.makeLink("/browsing/discourses/" + d.getId() + "/discoursePartTypes/" + typename, typename));
		});

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getDiscourseId() {
		return discourseId;
	}

	public void setDiscourseId(Long discourseId) {
		this.discourseId = discourseId;
	}

	public Map<String, Long> getCountsByType() {
		return countsByType;
	}

	public void setCountsByType(Map<String, Long> countsByType) {
		this.countsByType = countsByType;
	}

	
	
}
