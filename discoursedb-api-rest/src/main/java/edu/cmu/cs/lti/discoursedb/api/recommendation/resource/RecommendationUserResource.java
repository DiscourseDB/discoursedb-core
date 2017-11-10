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
package edu.cmu.cs.lti.discoursedb.api.recommendation.resource;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.api.recommendation.controller.RecommendationRestController;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;

public class RecommendationUserResource extends ResourceSupport {
	
	private String username;
	private String realname;
	private String email;
	private String country;
	
	public RecommendationUserResource(User user) {
		this.setUsername(user.getUsername());
		this.setRealname(user.getRealname());
		this.setEmail(user.getEmail());
		this.setCountry(user.getCountry());
		this.add(linkTo(methodOn(RecommendationRestController.class).sourcesForUser(user.getId())).withRel("userSources"));
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = nullCheck(country);
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = nullCheck(email);
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = nullCheck(username);
	}

	public String getRealname() {
		return realname;
	}

	public void setRealname(String realname) {
		this.realname = nullCheck(realname);
	}
	
	private String nullCheck(String s){
		return s==null?"":s;
	}

}
