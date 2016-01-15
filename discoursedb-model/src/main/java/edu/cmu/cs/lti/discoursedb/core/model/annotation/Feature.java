package edu.cmu.cs.lti.discoursedb.core.model.annotation;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntity;

@Entity
@Table(name="feature")
public class Feature extends UntimedBaseEntity implements Serializable{

	private static final long serialVersionUID = -5462337134833586687L;

	private long id;
	
	private String value;
	
	private FeatureType type;
	
	private AnnotationInstance annotation;
	
	public Feature(){}

	@Id
	@Column(name="id_feature", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	@SuppressWarnings("unused") //used by hibernate through reflection, but not exposed to users
	private void setId(long id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_feature_type")
	public FeatureType getType() {
		return type;
	}

	public void setType(FeatureType type) {
		this.type = type;
	}

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_annotation_instance")
	public AnnotationInstance getAnnotation() {
		return annotation;
	}

	public void setAnnotation(AnnotationInstance annotation) {
		this.annotation = annotation;
	}
	
}
