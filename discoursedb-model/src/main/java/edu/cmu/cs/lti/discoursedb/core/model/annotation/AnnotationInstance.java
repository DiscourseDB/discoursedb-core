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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.springframework.data.rest.core.annotation.Description;
import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.TypedSourcedBE;
import edu.cmu.cs.lti.discoursedb.core.model.system.SystemUser;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true, exclude={"annotationAggregate"})
@ToString(callSuper=true, exclude={"annotationAggregate"})
@Entity
@Table(name="annotation_instance")
@Description("A single instance of an annotation")
public class AnnotationInstance extends TypedSourcedBE implements Identifiable<Long>{
    
	@Id
	@Column(name="id_annotation_instance", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("Primary key")
	private Long id;
	
	@Column(name="begin_offset")
	@Description("Begin offset that indicates the start index of the span of text of a content entity to which the annotation instance applies. Can be ingored in the case of an entity annotation.")
	private int beginOffset;
	
	@Column(name="end_offset")
	@Description("End offset that indicates the end index of the span of text of a content entity to which the annotation instance applies. Can be ingored in the case of an entity annotation.")
	private int endOffset;
	
	@Column(name="covered_text", columnDefinition="TEXT")
	@Description("The text between begin_offset and end_offset.")
	private String coveredText;
		
	@ManyToOne 
	@JoinColumn(name = "fk_annotation_entity_proxy")
	@Description("The aggregate entity that aggregates all annotations belonging to the associated/annotated entity.")
	private AnnotationEntityProxy annotationEntityProxy;
	
	@ManyToOne 
	@JoinColumn(name = "fk_annotator")
	@Description("The user who created this annotation instance.")
	private SystemUser annotator;
	
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.REMOVE},mappedBy="annotation")
	@Description("A set of features that represent the payload of this annotation.")
	@Setter(AccessLevel.PRIVATE) 
	private Set<Feature> features = new HashSet<Feature>();
	
    @OneToMany(mappedBy="source")
	@Description("A set of relations that associate this annotation with other annotations. This set contains only those relations of which the present annotation is the source.")
	@Setter(AccessLevel.PRIVATE) 
	private Set<AnnotationRelation> sourceOfAnnotationRelations = new HashSet<AnnotationRelation>();

    @OneToMany(mappedBy="target")
	@Description("A set of relations that associate this annotation with other annotations. This set contains only those relations of which the present annotation is the target.")
	@Setter(AccessLevel.PRIVATE) 
	private Set<AnnotationRelation> targetOfAnnotationRelations = new HashSet<AnnotationRelation>();
		
}
