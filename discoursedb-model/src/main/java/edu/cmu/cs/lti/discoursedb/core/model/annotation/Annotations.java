package edu.cmu.cs.lti.discoursedb.core.model.annotation;

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

import org.springframework.data.rest.core.annotation.RestResource;

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
public class Annotations extends UntimedBaseEntity implements Serializable{

	private static final long serialVersionUID = -4654984158138436217L;

	@Id
	@Column(name="id_annotation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@RestResource(rel="annotationInstances",path="annotationInstances")
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH}, mappedBy="annotationAggregate")
	@JsonIgnore
	private Set<AnnotationInstance> annotations = new HashSet<AnnotationInstance>();
    
	public void addAnnotation(AnnotationInstance annotation) {
		this.annotations.add(annotation);
	}

	public void removeAnnotation(AnnotationInstance annotation) {
		this.annotations.remove(annotation);
	}
}
