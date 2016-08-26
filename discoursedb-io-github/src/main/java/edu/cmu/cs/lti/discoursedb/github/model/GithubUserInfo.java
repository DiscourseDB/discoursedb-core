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
package edu.cmu.cs.lti.discoursedb.github.model;

import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Simple PoJo to represent <br/>
 * Expects files with the following header:<br/>
 * <code></code><br/>
 * 
 * @author Chris Bogart
 *
 */
public class GithubUserInfo {

	private static final Logger logger = LogManager.getLogger(GithubUserInfo.class);	
	
	
	private String location;
	private String login;
	private String name;
	private String email;
	private String company;
	private String blog;
	private String type;
	private boolean siteAdmin;
	private String bio;
	private int publicRepos;
	private int publicGists;
	private int followers;
	private int following;
	private Date createdAt;
	private Date updatedAt;
	private String error;
	
	public GithubUserInfo(){}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getBlog() {
		return blog;
	}

	public void setBlog(String blog) {
		this.blog = blog;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean isSiteAdmin() {
		return siteAdmin;
	}

	@JsonProperty("site_admin")
	public void setSiteAdmin(String siteAdmin) {
		try {
			this.siteAdmin = Boolean.parseBoolean(siteAdmin.toLowerCase());
		} catch (Exception e) {
			this.siteAdmin = false;
		}
	}

	public String getBio() {
		return bio;
	}

	public void setBio(String bio) {
		this.bio = bio;
	}

	public int getPublicRepos() {
		return publicRepos;
	}

	@JsonProperty("public_repos")
	public void setPublicRepos(String publicRepos) {
		try {
		this.publicRepos = Integer.parseInt(publicRepos);
	} catch (Exception e) {
		this.siteAdmin = false;
	}
	}

	public int getPublicGists() {
		return publicGists;
	}

	@JsonProperty("public_gists")
	public void setPublicGists(String publicGists) {
		try {
		this.publicGists = Integer.parseInt(publicGists);
	} catch (Exception e) {
		this.siteAdmin = false;
	}
	}

	public int getFollowers() {
		return followers;
	}

	public void setFollowers(String followers) {
		try {
		this.followers = Integer.parseInt(followers);
	} catch (Exception e) {
		this.siteAdmin = false;
	}
	}

	public int getFollowing() {
		return following;
	}

	public void setFollowing(String following) {
		try {
		this.following = Integer.parseInt(following);
	} catch (Exception e) {
		this.siteAdmin = false;
	}
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	@JsonProperty("created_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss") //:wXXX")
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}

	public Date getUpdatedAt() {
		return updatedAt;
	}

	@JsonProperty("updated_at")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

	
	
}
