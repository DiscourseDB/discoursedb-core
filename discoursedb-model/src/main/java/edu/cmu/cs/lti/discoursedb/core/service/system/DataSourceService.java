package edu.cmu.cs.lti.discoursedb.core.service.system;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.IncorrectResultSetColumnCountException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSources;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DataSourceInstanceRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DataSourcesRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service
public class DataSourceService {

	@Autowired
	private DataSourcesRepository dataSourcesRepo;
	@Autowired
	private DataSourceInstanceRepository dataSourceInstanceRepo;

	/**
	 * Retrieves an existing DataSourceInstance
	 * 
	 * NOTE: entitySourceIds are not necessarily unique. If you are not sure, please disambiguate using an entitySourceDescriptor.
	 * The combination of sourceId and sourceDescriptor must be unique for a given dataset. 
	 * 
	 * @param entitySourceId
	 *            the id of the entity in the source system (i.e. how is the instance identified in the source)
	 * @param datasetName
	 *            the name of the dataset, e.g. edx_dalmooc_20150202
	 * @return an optional containing the DataSourceInstance if it exist, empty otherwise
	 * @throws IncorrectResultSetColumnCountException if more than one DataSource with this entitySourceId was found. In that case, you have to disambiguate with an entitySourceDescriptor using {@link #findDataSource(String, String, String)} 
	 */
	public Optional<DataSourceInstance> findDataSource(String entitySourceId, String dataSetName ) throws IncorrectResultSetColumnCountException{
		return Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(entitySourceId).and(
				DataSourcePredicates.hasDataSetName(dataSetName))));
	}

	/**
	 * Retrieves an existing DataSourceInstance
	 * 
	 * @param entitySourceId
	 *            the id of the entity in the source system (i.e. how is the instance identified in the source)
	 * @param entitySourceDescriptor
	 *            the name/descriptor of the field that was used as sourceId (i.e. how can i find the id in the source)
	 * @param datasetName
	 *            the name of the dataset, e.g. edx_dalmooc_20150202
	 * @return an optional containing the DataSourceInstance if it exist, empty otherwise
	 */
	public Optional<DataSourceInstance> findDataSource(String entitySourceId, String entitySourceDescriptor, String dataSetName ){
		return Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(entitySourceId).and(
				DataSourcePredicates.hasDataSetName(dataSetName)).and(
				DataSourcePredicates.hasEntitySourceDescriptor(entitySourceDescriptor))));
	}

	/**
	 * NOTE: entitySourceIds are not necessarily unique. If you are not sure, please disambiguate using an entitySourceDescriptor.
	 * The combination of sourceId and sourceDescriptor must be unique for a given dataset. 
	 * 
	 * @param entitySourceId
	 *            the id of the entity in the source system (i.e. how is the instance identified in the source)
	 * @param type
	 *            the name of the source system (e.g. edX, prosolo, wikipedia)
	 * @param datasetName
	 *            the name of the dataset, e.g. edx_dalmooc_20150202
	 * @return an optional containing the DataSourceInstance if it exist, empty otherwise
	 * @throws IncorrectResultSetColumnCountException if more than one DataSource with this entitySourceId was found. In that case, you have to disambiguate with an entitySourceDescriptor using {@link #findDataSource(String, String, DataSourceTypes, String)} 
	 */
	public Optional<DataSourceInstance> findDataSource(String entitySourceId, DataSourceTypes type, String dataSetName ) throws IncorrectResultSetColumnCountException{
		return Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(entitySourceId).and(
				DataSourcePredicates.hasSourceType(type).and(
				DataSourcePredicates.hasDataSetName(dataSetName)))));
	}

	/**
	 * Retrieves an existing DataSourceInstance
	 * 
	 * @param entitySourceId
	 *            the id of the entity in the source system (i.e. how is the instance identified in the source)
	 * @param entitySourceDescriptor
	 *            the name/descriptor of the field that was used as sourceId (i.e. how can i find the id in the source)
	 * @param type
	 *            the name of the source system (e.g. edX, prosolo, wikipedia)
	 * @param datasetName
	 *            the name of the dataset, e.g. edx_dalmooc_20150202
	 * @return an optional containing the DataSourceInstance if it exist, empty otherwise
	 */
	public Optional<DataSourceInstance> findDataSource(String entitySourceId, String entitySourceDescriptor, DataSourceTypes type, String dataSetName ){
		return Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(entitySourceId).and(
				DataSourcePredicates.hasSourceType(type).and(
				DataSourcePredicates.hasDataSetName(dataSetName).and(
				DataSourcePredicates.hasEntitySourceDescriptor(entitySourceDescriptor))))));
	}
	
	
	/**
	 * Checks whether a dataset with the given dataSetName exists in the DiscourseDB instance
	 * 
	 * @param dataSetName the name of the dataset (e.g. file name or dataset name)
	 * @return true, if any data from that dataset has been imported previously. false, else.
	 */
	public boolean dataSourceExists(String dataSetName){		
		return dataSourceInstanceRepo.count(DataSourcePredicates.hasDataSetName(dataSetName))>0;
	}

	/**
	 * Checks whether a dataset with the given parameters exists in the DiscourseDB instance
	 * 
	 * @param dataSetName the name of the dataset (e.g. file name or dataset name)
	 * @return true, if any data from that dataset has been imported previously. false, else.
	 */
	public boolean dataSourceExists(String sourceId, String sourceIdDescriptor, String dataSetName){		
		return dataSourceInstanceRepo.count(DataSourcePredicates.hasDataSetName(dataSetName).and(DataSourcePredicates.hasEntitySourceDescriptor(sourceIdDescriptor).and(DataSourcePredicates.hasSourceId(sourceId))))>0;
	}

	
	/**
	 * Checks if the provided DataSourceInstance exists in the database.
	 * If so, it returns the instance in the database.
	 * If not, it stores the provided instance in the database and returns the instance after the save process.
	 * 
	 * @param source a DataSourceInstance that might or might not be in the database yet
	 * @return the DataSourceInstance that has been committed to DiscourseDB 
	 */
	public DataSourceInstance createIfNotExists(DataSourceInstance source){
		Optional<DataSourceInstance> instance = Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(source.getEntitySourceId()).and(
				DataSourcePredicates.hasDataSetName(source.getDatasetName()))));
		if(instance.isPresent()){
			return instance.get();
		}else{
			return dataSourceInstanceRepo.save(source);
		}
	}	
	
	public <T extends TimedAnnotatableBaseEntityWithSource> boolean hasSourceId(T entity, String sourceId) {
		return entity.getDataSourceAggregate().getSources().stream()
				.anyMatch(e -> e.getEntitySourceId().equals(sourceId));
	}

	public <T extends UntimedBaseEntityWithSource> boolean hasSourceId(T entity, String sourceId) {
		return entity.getDataSourceAggregate().getSources().stream()
				.anyMatch(e -> e.getEntitySourceId().equals(sourceId));
	}

	/**
	 * Adds a new source to the provided entity.
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param source
	 *            the source to add to the entity
	 */
	public <T extends TimedAnnotatableBaseEntityWithSource> void addSource(T entity, DataSourceInstance source) {
		//in case we get an uncommitted instance, save it first. otherwise, just use it
		source=createIfNotExists(source);
		
		// check if sourceAggregate exists and create if not
		DataSources sourceAggregate = entity.getDataSourceAggregate();
		if (sourceAggregate == null) {
			sourceAggregate = new DataSources();
			sourceAggregate = dataSourcesRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}
		source.setSourceAggregate(sourceAggregate);

		// check if source exists in aggregate and add if not
		if (!sourceAggregate.getSources().contains(source)) {
			entity.getDataSourceAggregate().addSource(source);
		}
	}

	/**
	 * Adds a new source to the provided entity.
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param source
	 *            the source to add to the entity
	 */
	public <T extends UntimedBaseEntityWithSource> void addSource(T entity, DataSourceInstance source) {
		//in case we get an uncommitted instance, save it first. otherwise, just use it
		source=createIfNotExists(source);
		
		// check if sourceAggregate exists and create if not
		DataSources sourceAggregate = entity.getDataSourceAggregate();
		if (sourceAggregate == null) {
			sourceAggregate = new DataSources();
			sourceAggregate = dataSourcesRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}
		source.setSourceAggregate(sourceAggregate);

		// check if source exists in aggregate and add if not
		if (!sourceAggregate.getSources().contains(source)) {
			entity.getDataSourceAggregate().addSource(source);
		}
	}
	
}
