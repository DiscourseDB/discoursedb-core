package edu.cmu.cs.lti.discoursedb.core.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

/**
 * Adds source information to to regular timed entities
 * 
 * @author Oliver Ferschke
 *
 */
@MappedSuperclass
public abstract class TimedAnnotatableBaseEntityWithSource extends TimedAnnotatableBaseEntity{

	private String sourceId;
	@Column(name="source_id")
	public String getSourceId() {
		return sourceId;
	}
	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

//	private DataSources dataSources;
//
//	@ManyToOne(cascade=CascadeType.ALL) 
//	@JoinColumn(name = "fk_data_sources")
//	public DataSources getDataSources() {
//		return dataSources;
//	}
//
//	public void setDataSources(DataSources dataSources) {
//		this.dataSources = dataSources;
//	}
//	
}
