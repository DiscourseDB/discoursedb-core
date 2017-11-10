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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.browsing.controller.BrowsingRestController;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;

public class BrowsingStatsResource extends ResourceSupport {

	private Map<String,Long> discourseParts;
	private long users;
	private Map<String,Long> contributions;
	private List<String> discourses;
	
	public BrowsingStatsResource(DiscourseRepository discourseRepository, 
			DiscoursePartRepository discoursePartRepository, 
			ContributionRepository contributionRepository, 
			UserRepository userRepository) {

		this.discourses = new ArrayList<String>();
		this.discourseParts = new HashMap<String,Long>();
		this.contributions = new HashMap<String,Long>();
		this.users = 0;
		
		discourseRepository.findAll().forEach(d -> this.discourses.add(d.getName()));
		this.users = userRepository.count();
		
		for (Discourse d: discourseRepository.findAll()) {
			this.add(BrowsingRestController.makeLink("/browsing/discourses/" + d.getId(), "Discourse " + d.getName()));			
		}
		discoursePartRepository.countsByType().forEach(c  -> {
			this.discourseParts.put(c[0].toString(), (Long)c[1]);
		});
		contributionRepository.countsByType().forEach(c  -> {
			this.contributions.put(c[0].toString(), (Long)c[1]);
		});
		
	}

	

	public Map<String, Long> getDiscourseParts() {
		return discourseParts;
	}

	public void setDiscourseParts(Map<String, Long> discourseParts) {
		this.discourseParts = discourseParts;
	}

	public long getUsers() {
		return users;
	}

	public void setUsers(long users) {
		this.users = users;
	}

	public Map<String, Long> getContributions() {
		return contributions;
	}

	public void setContributions(Map<String, Long> contributions) {
		this.contributions = contributions;
	}

	public List<String> getDiscourses() {
		return discourses;
	}

	public void setDiscourses(List<String> discourses) {
		this.discourses = discourses;
	}

	
	
	
	
}
