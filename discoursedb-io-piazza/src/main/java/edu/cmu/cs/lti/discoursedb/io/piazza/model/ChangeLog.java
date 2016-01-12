package edu.cmu.cs.lti.discoursedb.io.piazza.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a Content.ChangeLog in a Piazza dump.
 * 
 * @author Oliver Ferschke
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeLog {

	private Date when;
	
	private String data;
	
	private String uid;
	
	private String to;
	
	private String anon;
	
	private String type;

	public Date getWhen() {
		return when;
	}

	public void setWhen(Date when) {
		this.when = when;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getAnon() {
		return anon;
	}

	public void setAnon(String anon) {
		this.anon = anon;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
