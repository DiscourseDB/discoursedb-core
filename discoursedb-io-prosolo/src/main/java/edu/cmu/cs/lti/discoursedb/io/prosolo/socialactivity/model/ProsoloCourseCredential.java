package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.model;

import java.util.Date;

/**
 * Wraps entities form the course table in prosolo
 * 
 * @author Oliver Ferschke
 *
 */
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
		
	public ProsoloCourseCredential(Long id, Date created, boolean deleted, String dc_description, String title,
			String creator_type, boolean students_can_add_new_competences, Long maker, boolean published) {
		super();
		this.id = id;
		this.created = created;
		this.deleted = deleted;
		this.dc_description = dc_description;
		this.title = title;
		this.creator_type = creator_type;
		this.students_can_add_new_competences = students_can_add_new_competences;
		this.maker = maker;
		this.published = published;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getCreated() {
		return created;
	}
	public void setCreated(Date created) {
		this.created = created;
	}
	public boolean isDeleted() {
		return deleted;
	}
	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}
	public String getDc_description() {
		return dc_description;
	}
	public void setDc_description(String dc_description) {
		this.dc_description = dc_description;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getCreator_type() {
		return creator_type;
	}
	public void setCreator_type(String creator_type) {
		this.creator_type = creator_type;
	}
	public boolean isStudents_can_add_new_competences() {
		return students_can_add_new_competences;
	}
	public void setStudents_can_add_new_competences(boolean students_can_add_new_competences) {
		this.students_can_add_new_competences = students_can_add_new_competences;
	}
	public Long getMaker() {
		return maker;
	}
	public void setMaker(Long maker) {
		this.maker = maker;
	}
	public boolean isPublished() {
		return published;
	}
	public void setPublished(boolean published) {
		this.published = published;
	}
	
	
	
}
