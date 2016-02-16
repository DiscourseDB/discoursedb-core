package edu.cmu.cs.lti.discoursedb.core.model;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import org.springframework.data.rest.core.annotation.Description;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSources;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Adds source information to to regular untimed entities
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true, exclude={"dataSourceAggregate"})
@ToString(callSuper=true, exclude={"dataSourceAggregate"})
@MappedSuperclass
public abstract class UntimedBaseEntityWithSource extends BaseEntity{

	@ManyToOne(cascade={CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH,CascadeType.DETACH}) 
	@JoinColumn(name = "fk_data_sources")
	@Description("An aggregate that contains links to all data sources associated with this entity.")
	private DataSources dataSourceAggregate;
	
}
