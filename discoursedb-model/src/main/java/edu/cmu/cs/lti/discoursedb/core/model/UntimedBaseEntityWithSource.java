package edu.cmu.cs.lti.discoursedb.core.model;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

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
public abstract class UntimedBaseEntityWithSource extends UntimedBaseEntity{

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_data_sources")
	private DataSources dataSourceAggregate;
	
}
