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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.browsing.controller.BrowsingRestController;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;

public class BrowsingUserResource extends ResourceSupport {
	
	private User user;
	private String username;
	private String email;
	private String country;
	private String ip;
	private String language;
	private String location;
	private String realname;
	private Map<Long,String> inDiscourseParts;
	private List<BrowsingAnnotationResource> annotations;
	
	public BrowsingUserResource(User u) {
		this.user = u;
		this.setUsername(u.getUsername());
		this.setEmail(u.getEmail());
		this.setCountry(u.getCountry());
		this.setIp(u.getIp());
		this.language = u.getLanguage();
		this.location = u.getLocation();
		
		
		if (u.getAnnotations() != null) {
			List<BrowsingAnnotationResource> annos = new LinkedList<BrowsingAnnotationResource>();
			for (AnnotationInstance ai: u.getAnnotations().getAnnotations()) {
				annos.add(new BrowsingAnnotationResource(ai));
			}
			this.setAnnotations(annos);
		}
		

	}
	
	public void fillInDiscoursePartLinks(DiscoursePartService dpsv) {
		Set<DiscoursePart> dps = dpsv.findAllContainingUserRecursively(this.user);
		
		inDiscourseParts = new HashMap<Long,String>();
		int maxelts = 8;
   	    for (DiscoursePart dp : dps) {
   	    	maxelts -= 1;
   	    	if (maxelts > 0) {
	   	    	inDiscourseParts.put(dp.getId(), dp.getName());
	   	    	add(BrowsingRestController.makeLink("/browsing/subDiscourseParts/" + dp.getId(), dp.getName()));
   	    	} else {
   	    		break;
   	    	}
   	    }		
	}
	
	public void filterAnnotations(String annotType) {
		if (!annotType.equals("*")) {
			setAnnotations(this.getAnnotations().stream().filter( bai -> bai.getType().equals(annotType) ).collect(Collectors.toList()));
		}
	}

	public Long _getUserId() {
		return user.getId();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = realname;
	}

	public Map<Long,String> _getInDiscourseParts() {
		return inDiscourseParts;
	}
	public List<String> getInDiscourseParts() {
		return inDiscourseParts.values().stream().collect(Collectors.toList());
	}

	public List<BrowsingAnnotationResource> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(List<BrowsingAnnotationResource> annotations) {
		this.annotations = annotations;
	}


	
	

}
