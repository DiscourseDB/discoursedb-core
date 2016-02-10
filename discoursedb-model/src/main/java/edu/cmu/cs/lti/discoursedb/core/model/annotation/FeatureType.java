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
@EqualsAndHashCode(callSuper=true, exclude={"features"})
@ToString(callSuper=true, exclude={"features"})
@Entity
@Table(name="feature_type")
@Description("Defines the type of a feature (instance).")
public class FeatureType extends BaseTypeEntity implements Serializable{

	private static final long serialVersionUID = -3343145417294760437L;

	@Id
	@Column(name="id_feature_type", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	@Description("The primary key.")
	private Long id;
	
	@Description("The data type of the associated features. Determines how the feature value should be interpreted (e.g. String, Integer).")
	private String datatype;
	
	@Description("An optional description of the feature type.")
	private String description;
	
	@JsonIgnore
	@OneToMany(fetch=FetchType.LAZY, mappedBy="type")
	@Description("The set of all features with this type.")
	private Set<Feature> features = new HashSet<Feature>();
	
}
