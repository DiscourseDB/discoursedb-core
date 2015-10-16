package edu.cmu.cs.lti.discoursedb.core.model.system;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.SelectBeforeUpdate;

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

@Entity
@SelectBeforeUpdate 
@DynamicUpdate
@DynamicInsert
@Table(name="data_source_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "entity_source_id","entity_source_descriptor", "source_type","dataset_name" }) )
public class DataSourceInstance extends UntimedBaseEntity implements Serializable{

	private static final long serialVersionUID = -6293065846688380816L;

	private long id;
	
	private String entitySourceId;
	
	private String entitySourceDescriptor;
	
	private DataSourceTypes sourceType;	
	
	private String datasetName;	
	
	private DataSources sourceAggregate;
	
	DataSourceInstance(){}

	/**
	 * Creates a new DataSourceInstance for the entity with the source id
	 * "entitySourceId" based on the dataset with the provided name
	 * "datasetName" which is an instance of the source with the provided name
	 * "sourceType".
	 * 
	 * @param entitySourceId
	 *            the id of the entity in the source system
	 * @param entitySourceId
	 *            the name/descriptor of the field that was used as sourceId 
	 * @param datasetName
	 *            the name of the dataset, e.g. edx_dalmooc_20150202
	 */
	public DataSourceInstance(String entitySourceId, String entitySourceDescriptor, String datasetName) {
		setEntitySourceId(entitySourceId);
		setDatasetName(datasetName);
		setEntitySourceDescriptor(entitySourceDescriptor);
	}
	
	/**
	 * Creates a new DataSourceInstance for the entity with the source id
	 * "entitySourceId" based on the dataset with the provided name
	 * "datasetName" which is an instance of the source with the provided name
	 * "sourceType".
	 * 
	 * @param entitySourceId
	 *            the id of the entity in the source system
	 * @param entitySourceId
	 *            the name/descriptor of the field that was used as sourceId 
	 * @param sourceType
	 *            the name of the source system (e.g. edX, prosolo, wikipedia)
	 * @param datasetName
	 *            the name of the dataset, e.g. edx_dalmooc_20150202
	 */
	public DataSourceInstance(String entitySourceId, String entitySourceDescriptor, DataSourceTypes sourceType, String datasetName) {
		setEntitySourceId(entitySourceId);
		setSourceType(sourceType);
		setDatasetName(datasetName);
		setEntitySourceDescriptor(entitySourceDescriptor);
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
	
	@Column(name="entity_source_id", nullable=false, updatable=false)
	public String getEntitySourceId() {
		return entitySourceId;
	}

	public void setEntitySourceId(String entitySourceId) {
		this.entitySourceId = entitySourceId;
	}
	
	@Column(name="entity_source_descriptor", nullable=true, updatable=false)
	public String getEntitySourceDescriptor() {
		return entitySourceDescriptor;
	}

	public void setEntitySourceDescriptor(String entitySourceDescriptor) {
		this.entitySourceDescriptor = entitySourceDescriptor;
	}

	@Enumerated(EnumType.STRING)
	@Column(name="source_type")
	public DataSourceTypes getSourceType() {
		return sourceType;
	}

	public void setSourceType(DataSourceTypes sourceType) {
		this.sourceType = sourceType;
	}

	@Column(name="dataset_name")
	public String getDatasetName() {
		return datasetName;
	}

	public void setDatasetName(String datasetName) {
		this.datasetName = datasetName;
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
