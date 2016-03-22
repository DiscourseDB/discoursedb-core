package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * Wraps entities from the user table in prosolo. 
 * Comments are based on discussions with the prosolo developers.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class ProsoloUser {
	private long id;
	private Date created;
	private String deleted;
	private String dc_description;
	private String title;
	private String avatar_url;
	private String lastname;
	private Double latitude;	
	private String location_name;
	private Double longitude;
	private String name;
	private String password;
	private Integer password_length;
	private String position;
	private String profile_url;
	private String sytem;
	private String user_type;
	private String email;
	private String user_user_organization;
}
