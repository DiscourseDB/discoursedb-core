package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the followed_entity table in prosolo
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class ProsoloFollowedEntity {
	private String dtype;	
	private Long id;
	private Date created;
	private Boolean deleted;
	private String dc_description;
	private String title;
	private Date started_following;
	private Long user;
	private Long followed_node;
	private Long followed_user;
}
