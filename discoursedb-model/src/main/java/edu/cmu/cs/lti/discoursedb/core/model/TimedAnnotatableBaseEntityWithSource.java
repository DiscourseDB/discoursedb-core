package edu.cmu.cs.lti.discoursedb.core.model;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSources;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Adds source information to to regular timed entities
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@EqualsAndHashCode(callSuper=true)
@MappedSuperclass
public abstract class TimedAnnotatableBaseEntityWithSource extends TimedAnnotatableBaseEntity{

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_data_sources")
	private DataSources dataSourceAggregate;
		
}
