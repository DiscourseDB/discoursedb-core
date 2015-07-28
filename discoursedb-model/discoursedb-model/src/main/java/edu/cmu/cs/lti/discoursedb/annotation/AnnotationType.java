package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="annotation_type")
public class AnnotationType implements Serializable{

	private static final long serialVersionUID = 9194247332380412321L;

	private long id;
	
	private String name;
	
	private String description;
	
	private boolean isEntityAnnotation;
	
	public AnnotationType(){}

	@Id
	@Column(name="id_annotation_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isEntityAnnotation() {
		return isEntityAnnotation;
	}

	public void setEntityAnnotation(boolean isEntityAnnotation) {
		this.isEntityAnnotation = isEntityAnnotation;
	}
	
}
