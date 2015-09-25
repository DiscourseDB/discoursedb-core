package edu.cmu.cs.lti.discoursedb.core.model;

import javax.persistence.CascadeType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSources;

/**
 * Adds source information to to regular timed entities
 * 
 * @author Oliver Ferschke
 *
 */
@MappedSuperclass
public abstract class TimedAnnotatableBaseEntityWithSource extends TimedAnnotatableBaseEntity{

	private DataSources dataSourceAggregate;

	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_data_sources")
	public DataSources getDataSourceAggregate() {
		return dataSourceAggregate;
	}
	
	public void setDataSourceAggregate(DataSources dataSources) {
		this.dataSourceAggregate = dataSources;
	}
		
}
