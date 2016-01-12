package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Content.TagGood in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TagGood {

	private boolean admin;
	
	@JsonProperty("facebook-id")
	private String facebookId;
	
	private String email;
	
	private String photo;
	
	private boolean us;
	
	private String id;
	
	private String role;
	
	private String name;

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getFacebookId() {
		return facebookId;
	}

	public void setFacebookId(String facebookId) {
		this.facebookId = facebookId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhoto() {
		return photo;
	}

	public void setPhoto(String photo) {
		this.photo = photo;
	}

	public boolean isUs() {
		return us;
	}

	public void setUs(boolean us) {
		this.us = us;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
