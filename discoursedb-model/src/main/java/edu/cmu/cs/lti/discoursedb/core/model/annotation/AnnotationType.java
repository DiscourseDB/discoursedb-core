package edu.cmu.cs.lti.discoursedb.core.model.annotation;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.cmu.cs.lti.discoursedb.core.model.BaseTypeEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"annotations"})
@ToString(callSuper=true, exclude={"annotations"})
@Entity
@Table(name="annotation_type")
@Description("Defines the type of an annotation instance.")
public class AnnotationType extends BaseTypeEntity implements Serializable{

	private static final long serialVersionUID = 9194247332380412321L;

	@Id
	@Column(name="id_annotation_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key.")
	private Long id;
	
	private String description;
	
	@Column(name="is_entity_annotation")
	@Description("Determines wheter the assocaited annotation instaces refers to an entity or text.")
	private boolean isEntityAnnotation;
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.LAZY, mappedBy="type")
	@Description("A set of annotation instances of this type.")
    private Set<AnnotationInstance> annotations=new HashSet<AnnotationInstance>();

}
