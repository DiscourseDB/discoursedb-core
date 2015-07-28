package edu.cmu.cs.lti.discoursedb.annotation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
	
    private Set<AnnotationInstance> annotations=new HashSet<AnnotationInstance>();
	
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

	@Column(name="is_entity_annotation")
	public boolean isEntityAnnotation() {
		return isEntityAnnotation;
	}

	public void setEntityAnnotation(boolean isEntityAnnotation) {
		this.isEntityAnnotation = isEntityAnnotation;
	}

	@OneToMany(fetch=FetchType.LAZY, cascade=CascadeType.ALL, mappedBy="type")
	public Set<AnnotationInstance> getAnnotations() {
		return annotations;
	}

	public void setAnnotations(Set<AnnotationInstance> annotations) {
		this.annotations = annotations;
	}
	
}
