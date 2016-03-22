package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Wraps entities form the course table in prosolo
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class ProsoloCourseCredential {

	private Long id;
	private Date created;
	private boolean deleted;
	private String dc_description;
	private String title;
	private String creator_type;
	private boolean students_can_add_new_competences;
	private Long maker;
	private boolean published;
	
}
