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
	
	@Column(name="covered_text")
	@Description("The text between begin_offset and end_offset.")
	private String coveredText;
		
	@ManyToOne 
	@JoinColumn(name = "fk_annotation")
	@Description("The aggregate entity that aggregares all annotations belonging to the associated/annotated entity.")
	private AnnotationAggregate annotationAggregate;
	
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH},mappedBy="annotation")
	@Description("A set of features that represent the payload of this annotation.")
	private Set<Feature> features = new HashSet<Feature>();
		
	public void addFeature(Feature feature) {
		this.features.add(feature);
	}

	public void removeAllFeatures() {
		this.features.clear();
	}
	
}
