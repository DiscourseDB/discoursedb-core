package edu.cmu.cs.lti.discoursedb.core.service.system;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSources;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DataSourceInstanceRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DataSourcesRepository;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service
public class DataSourceService {
	private static final Logger logger = LogManager.getLogger(DataSourceService.class);	

	@Autowired
	private DataSourcesRepository dataSourcesRepo;
	@Autowired
	private DataSourceInstanceRepository dataSourceInstanceRepo;

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
		return dataSourceInstanceRepo.count(
				DataSourcePredicates.hasDataSetName(dataSetName).and(
				DataSourcePredicates.hasEntitySourceDescriptor(sourceIdDescriptor).and(
				DataSourcePredicates.hasSourceId(sourceId)))) > 0;
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
		String sourceId = source.getEntitySourceId();
		String sourceDescriptor = source.getEntitySourceDescriptor();
		String dataSetName = source.getDatasetName();
		if(sourceId==null||sourceId.isEmpty()||sourceDescriptor==null||sourceDescriptor.isEmpty()||dataSetName==null||dataSetName.isEmpty()){			
			logger.error("You need to set sourceId, sourceDescriptor and dataSetName to create a new DataSourceInstance. Proceeding with incomplete DataSourceInstance ...");			
		}
		Optional<DataSourceInstance> instance = findDataSource(sourceId, sourceDescriptor, dataSetName);
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
	 * Adds a new source to the provided entity.<br/>
	 * Note that the source MUST be unique for the entity. No other entity can be associated with this particular source.
	 * If you want to related multiple DiscourseDB entities with the same source entity, disambiguate the source with the descriptor to make each source unique.
	 * e.g. to map a source post identified by its id to a DiscourseDB contribution and a DiscourseDB content, specify the source descriptors like this:
	 * <code> contribution#post.id</code> and <code>content#post.id</code>. Ideally, the source descriptors should be defined in a source mapping file.
	 * See the edx and prosolo converters for examples.
	 * 
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param source
	 *            the source to add to the entity
	 */
	public <T extends TimedAnnotatableBaseEntityWithSource> void addSource(T entity, DataSourceInstance source) {		
		//the source aggregate is a proxy for the entity
		DataSources sourceAggregate = entity.getDataSourceAggregate();
		if (sourceAggregate == null) {
			sourceAggregate = new DataSources();
			sourceAggregate = dataSourcesRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}
		//connect source aggregate and source
		Optional<DataSourceInstance> existingDataSourceInstance = findDataSource(source.getEntitySourceId(), source.getEntitySourceDescriptor(), source.getDatasetName());
		if(!existingDataSourceInstance.isPresent()){
			source.setSourceAggregate(sourceAggregate);
			source = dataSourceInstanceRepo.save(source);
			sourceAggregate.addSource(source);
		}else if(!existingDataSourceInstance.get().getSourceAggregate().equals(entity.getDataSourceAggregate())){
			//we tried to create an existing DataSourceInstance but add it to another entity
			//this is not allowed, a source may only produce a single entity
			logger.warn("Source already assigned to an existing entity: ("+source.getEntitySourceId()+", "+source.getEntitySourceDescriptor()+", "+source.getDatasetName()+") but must be unique.");				
		}
	}

	/**
	 * Adds a new source to the provided entity.<br/>
	 * Note that the source MUST be unique for the entity. No other entity can be associated with this particular source.
	 * If you want to related multiple DiscourseDB entities with the same source entity, disambiguate the source with the descriptor to make each source unique.
	 * e.g. to map a source post identified by its id to a DiscourseDB contribution and a DiscourseDB content, specify the source descriptors like this:
	 * <code> contribution#post.id</code> and <code>content#post.id</code>. Ideally, the source descriptors should be defined in a source mapping file.
	 * See the edx and prosolo converters for examples.
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param source
	 *            the source to add to the entity
	 */
	public <T extends UntimedBaseEntityWithSource> void addSource(T entity, DataSourceInstance source) {
		//the source aggregate is a proxy for the entity
		DataSources sourceAggregate = entity.getDataSourceAggregate();
		if (sourceAggregate == null) {
			sourceAggregate = new DataSources();
			sourceAggregate = dataSourcesRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}
		//connect source aggregate and source
		Optional<DataSourceInstance> existingDataSourceInstance = findDataSource(source.getEntitySourceId(), source.getEntitySourceDescriptor(), source.getDatasetName());
		if(!existingDataSourceInstance.isPresent()){
			source.setSourceAggregate(sourceAggregate);
			source = dataSourceInstanceRepo.save(source);
			sourceAggregate.addSource(source);
		}else if(!existingDataSourceInstance.get().getSourceAggregate().equals(entity.getDataSourceAggregate())){
			//we tried to create an existing DataSourceInstance but add it to another entity
			//this is not allowed, a source may only produce a single entity
			logger.error("Source already assigned to an existing entity: ("+source.getEntitySourceId()+", "+source.getEntitySourceDescriptor()+", "+source.getDatasetName()+") but must be unique.");				
		}
	}
	
}
