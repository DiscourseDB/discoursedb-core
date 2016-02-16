package edu.cmu.cs.lti.discoursedb.core.model.annotation;

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

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonIgnore;

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"annotations"})
@ToString(callSuper=true, exclude={"annotations"})
@Entity
@Table(name="annotation")
@Description("An aggregate that links an entity with a set of annotation instances. Each annotatable aggregate can have one aggregate and each aggregate can link to multiple annotation instances.")
public class Annotations extends UntimedBaseEntity  implements Identifiable<Long>{

	@Id
	@Column(name="id_annotation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key.")
	private Long id;
	
	@RestResource(rel="annotationInstances",path="annotationInstances")
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH}, mappedBy="annotationAggregate")
	@JsonIgnore
	@Description("A set of annotation instances associated with the entity that is represented by this aggregate.")
	private Set<AnnotationInstance> annotations = new HashSet<AnnotationInstance>();
    
	public void addAnnotation(AnnotationInstance annotation) {
		this.annotations.add(annotation);
	}

	public void removeAnnotation(AnnotationInstance annotation) {
		this.annotations.remove(annotation);
	}
}
