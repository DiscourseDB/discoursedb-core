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

import edu.cmu.cs.lti.discoursedb.core.model.BaseEntity;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"annotations"})
@ToString(callSuper=true, exclude={"annotations"})
@Entity
@Table(name="annotation_entity_proxy")
@Description("An proxy for and entity that links an it with a set of annotation instances. Each annotatable entity can have one proxy and each proxy can link to multiple annotation instances.")
public class AnnotationEntityProxy extends BaseEntity  implements Identifiable<Long>{

	@Id
	@Column(name="id_annotation_entity_proxy", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key.")
	private Long id;
	
	@RestResource(rel="annotationInstances",path="annotationInstances")
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE}, mappedBy="annotationEntityProxy")
	@Setter(AccessLevel.PRIVATE) 
	@JsonIgnore
	@Description("A set of annotation instances associated with the entity that is represented by this proxy.")
	private Set<AnnotationInstance> annotations = new HashSet<AnnotationInstance>();

}
