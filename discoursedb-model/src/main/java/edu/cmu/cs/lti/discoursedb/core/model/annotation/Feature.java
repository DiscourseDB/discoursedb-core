package edu.cmu.cs.lti.discoursedb.core.model.annotation;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.cmu.cs.lti.discoursedb.core.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"annotation"})
@ToString(callSuper=true, exclude={"annotation"})
@Entity
@Table(name="feature")
@Description("Represents a feature (instance) which holds the payload of an annotation instance.")
public class Feature extends BaseEntity implements Identifiable<Long>{

	@Id
	@Column(name="id_feature", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key.")
	private Long id;
	
	@Description("The feature value.")
	private String value;
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_feature_type")
	@Description("The type associated with this feature.")
	private FeatureType type;
	
	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "fk_annotation_instance")
	@Description("The annotation instance assocaited with this feature.")
	private AnnotationInstance annotation;
	
}
