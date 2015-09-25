package edu.cmu.cs.lti.discoursedb.core.service.system;

import java.util.Optional;

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
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service
public class DataSourceService {

	@Autowired
	private DataSourcesRepository dataSourcesRepo;
	@Autowired
	private DataSourceInstanceRepository dataSourceInstanceRepo;

	
	public Optional<DataSourceInstance> findDataSource(String entitySourceId, String dataSetName ){
		return Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(entitySourceId).and(
				DataSourcePredicates.hasDataSetName(dataSetName))));
	}
	
	public Optional<DataSourceInstance> findDataSource(String entitySourceId, DataSourceTypes type, String dataSetName ){
		return Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(entitySourceId).and(
				DataSourcePredicates.hasSourceType(type).and(
				DataSourcePredicates.hasDataSetName(dataSetName)))));
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
				DataSourcePredicates.hasSourceType(source.getSourceType()).and(
				DataSourcePredicates.hasDataSetName(source.getDatasetName())))));
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
