package edu.cmu.cs.lti.discoursedb.core.model.annotation;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableBE;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@Entity
@Table(name="annotation_relation")
public class AnnotationRelation extends TypedTimedAnnotatableBE implements Identifiable<Long> {

	@Id
	@Column(name="id_annotation_relation", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("Primary key of the AnnotationRelation")
	private Long id;
	
	@Column(name="first_in_chain", nullable=false)
	@Description("Determines whether this relation is the first link between two annotations in a chain of annotations. In that case, the source of this relation is the first annotation of the chain. The default value is true.")
	private boolean firstInChain = true;
	
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_source")
	@Description("The source annotation in this relation.")
	private AnnotationInstance source;
	
	@OneToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_target")
	@Description("The target annotation in this relation.")
	private AnnotationInstance target;	
}
