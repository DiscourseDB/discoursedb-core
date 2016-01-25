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
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="feature")
public class Feature extends UntimedBaseEntity implements Serializable{

	private static final long serialVersionUID = -5462337134833586687L;

	@Id
	@Column(name="id_feature", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	private String value;
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_feature_type")
	private FeatureType type;
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_annotation_instance")
	private AnnotationInstance annotation;
	
}
