package edu.cmu.cs.lti.discoursedb.core.model.system;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.CoreBaseEntity;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="data_source_instance")
public class DataSourceInstance extends CoreBaseEntity implements Serializable{

	private static final long serialVersionUID = -6293065846688380816L;

	private long id;
	
	private String sourceId;
	
	private String sourceDescriptor;	
	
	private DataSources sourceAggregate;
	
	DataSourceInstance(){}

	public DataSourceInstance(String sourceId, String sourceDescriptor) {
		setSourceId(sourceId);
		setSourceDescriptor(sourceDescriptor);
	}

	@Id
	@Column(name="id_data_source_instance", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}

	@Column(name="entity_source_id", nullable=false)
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	@Column(name="descriptor", nullable=false)
	public String getSourceDescriptor() {
		return sourceDescriptor;
	}

	public void setSourceDescriptor(String sourceDescriptor) {
		this.sourceDescriptor = sourceDescriptor;
	}
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_sources")
	public DataSources getSourceAggregate() {
		return sourceAggregate;
	}

	public void setSourceAggregate(DataSources sourceAggregate) {
		this.sourceAggregate = sourceAggregate;
	}
	
}
