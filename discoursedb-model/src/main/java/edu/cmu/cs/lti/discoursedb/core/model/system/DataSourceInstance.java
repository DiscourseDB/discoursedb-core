package edu.cmu.cs.lti.discoursedb.core.model.system;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.springframework.hateoas.Identifiable;

import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@EqualsAndHashCode(callSuper=true)
@NoArgsConstructor
@Entity
@Table(name = "data_source_instance", uniqueConstraints = @UniqueConstraint(columnNames = { "entity_source_id",
		"entity_source_descriptor", "dataset_name" }) , indexes = {
				@Index(name = "sourceId_descriptor_Index", columnList = "entity_source_id,entity_source_descriptor"),
				@Index(name = "sourceDescriptorIndex", columnList = "entity_source_descriptor"),
				@Index(name = "sourceIdIndex", columnList = "entity_source_id") })
public class DataSourceInstance extends UntimedBaseEntity implements Identifiable<Long> {

	@Id
	@Column(name="id_data_source_instance", nullable=false)
    @GeneratedValue(strategy = GenerationType.AUTO)
	@Setter(AccessLevel.PRIVATE) 
	private Long id;
	
	@Column(name="entity_source_id", nullable=false, updatable=false)
	private String entitySourceId;
	
	@Column(name="entity_source_descriptor", nullable=true, updatable=false)
	private String entitySourceDescriptor;
	
	@Enumerated(EnumType.STRING)
	@Column(name="source_type")
	private DataSourceTypes sourceType;	
	
	@Column(name="dataset_name")
	private String datasetName;	
	
	@ManyToOne(cascade=CascadeType.ALL) 
	@JoinColumn(name = "fk_sources")
	private DataSources sourceAggregate;
	
	/**
	 * Creates a new DataSourceInstance for the entity with the source id
	 * "entitySourceId" based on the dataset with the provided name
	 * "datasetName" which is an instance of the source with the provided name
	 * "sourceType".
	 * 
	 * @param entitySourceId
	 *            the id of the entity in the source system (i.e. how is the instance identified in the source)
	 * @param entitySourceDescriptor
	 *            the name/descriptor of the field that was used as sourceId (i.e. how can i find the id in the source)
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
	 *            the id of the entity in the source system (i.e. how is the instance identified in the source)
	 * @param entitySourceDescriptor
	 *            the name/descriptor of the field that was used as sourceId (i.e. how can i find the id in the source)
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
	
}
