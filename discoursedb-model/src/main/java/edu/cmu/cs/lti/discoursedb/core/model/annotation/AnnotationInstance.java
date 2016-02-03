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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntityWithSource;
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
public class AnnotationInstance extends UntimedBaseEntityWithSource implements Serializable{

	private static final long serialVersionUID = 6699029374236146557L;
    
	@Id
	@Column(name="id_annotation_instance", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@Column(name="begin_offset")
	private int beginOffset;
	
	@Column(name="end_offset")
	private int endOffset;
	
	@Column(name="covered_text")
	private String coveredText;
	
	@ManyToOne
	@JoinColumn(name = "fk_annotation_type")
	private AnnotationType type;	
	
	@ManyToOne 
	@JoinColumn(name = "fk_annotation")
	private Annotations annotationAggregate;
	
	@OneToMany(fetch=FetchType.LAZY,cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH},mappedBy="annotation")
	private Set<Feature> features = new HashSet<Feature>();
		
	public void addFeature(Feature feature) {
		this.features.add(feature);
	}

	public void removeAllFeatures() {
		this.features.clear();
	}
	
}
