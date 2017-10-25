/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
package edu.cmu.cs.lti.discoursedb.core.service.system;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.SourcedBE;
import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableSourcedBE;
import edu.cmu.cs.lti.discoursedb.core.model.TypedSourcedBE;
import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableSourcedBE;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceAggregate;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.system.Dataset;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DataSourceInstanceRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DatasetRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DataSourceAggregateRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class DataSourceService {

	private final @NonNull DataSourceAggregateRepository dataSourceAggregateRepo;
	private final @NonNull DataSourceInstanceRepository dataSourceInstanceRepo;
	private final @NonNull DatasetRepository datasetRepo;
	
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
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<DataSourceInstance> findDataSource(String entitySourceId, String entitySourceDescriptor, String datasetName ){
		Assert.hasText(entitySourceId);
		Assert.hasText(entitySourceDescriptor);
		Assert.hasText(datasetName);

		Long dsid = findOrCreateDataset(datasetName).getDatasetId();
		return Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(entitySourceId).and(
				DataSourcePredicates.hasDatasetId(dsid)).and(
				DataSourcePredicates.hasEntitySourceDescriptor(entitySourceDescriptor))));
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
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<DataSourceInstance> findDataSource(String entitySourceId, String entitySourceDescriptor, long datasetId ){
		Assert.hasText(entitySourceId);
		Assert.hasText(entitySourceDescriptor);
		
		return Optional.ofNullable(dataSourceInstanceRepo.findOne(
				DataSourcePredicates.hasSourceId(entitySourceId).and(
				DataSourcePredicates.hasDatasetId(datasetId)).and(
				DataSourcePredicates.hasEntitySourceDescriptor(entitySourceDescriptor))));
	}	

	/**
	 * Checks whether a dataset with the given dataSetName exists in the DiscourseDB instance
	 * 
	 * @param dataSetName the name of the dataset (e.g. file name or dataset name)
	 * @return true, if any data from that dataset has been imported previously. false, else.
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Dataset findDataset(String datasetName){	
		//TODO: SECURE
		return datasetRepo.findOne(DatasetPredicates.hasName(datasetName));
	}

	/**
	 * Checks whether a data source with the given parameters exists in the DiscourseDB instance
	 * 
	 * 
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public boolean dataSourceExists(String sourceId, String sourceIdDescriptor, String datasetName){		
		Assert.hasText(sourceId);
		Assert.hasText(sourceIdDescriptor);
		Assert.hasText(datasetName);
		
		Long dsid = findOrCreateDataset(datasetName).getDatasetId();
		return dataSourceInstanceRepo.count(
				DataSourcePredicates.hasDatasetId(dsid).and(
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
		Assert.notNull(source);

		String sourceId = source.getEntitySourceId();
		String sourceDescriptor = source.getEntitySourceDescriptor();
		Long datasetId = source.getDatasetId();
		if(sourceId==null||sourceId.isEmpty()||sourceDescriptor==null||sourceDescriptor.isEmpty()){			
			log.error("You need to set sourceId, sourceDescriptor and dataSetName to create a new DataSourceInstance. Proceeding with incomplete DataSourceInstance ...");			
		}
		Optional<DataSourceInstance> instance = findDataSource(sourceId, sourceDescriptor, datasetId);
		if(instance.isPresent()){
			return instance.get();
		}else{
			return dataSourceInstanceRepo.save(source);
		}
	}	

    /**
	 * Retrieves a discourse part that has a source which exactly matches the given parameter.
	 * 
	 * @param entityId the id of the discourse part  
	 * @return an optional DiscoursePart that meets the requested parameters
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<DataSourceInstance> findOne(Long id) {
		return dataSourceInstanceRepo.findOne(id);
	}
	
	
	
	/**
	 * Finds the datasource with the given descriptor for the given entity 
	 * 
	 * @param entity the entity to retrieve the datasource for
	 * @param entitySourceDescriptor the descriptor of the datasource to be retrieved
	 * @return a DataSourceInstance with the given descriptor for the given entity
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TypedTimedAnnotatableSourcedBE> Optional<DataSourceInstance> findDataSource(T entity, String entitySourceDescriptor) {
		Assert.notNull(entity);
		Assert.hasText(entitySourceDescriptor);
		
		return entity.getDataSourceAggregate().getSources().stream().filter(e -> e.getEntitySourceDescriptor().equals(entitySourceDescriptor)).findAny();
	}

	/**
	 * Finds the datasource with the given descriptor for the given entity 
	 * 
	 * @param entity the entity to retrieve the datasource for
	 * @param entitySourceDescriptor the descriptor of the datasource to be retrieved
	 * @return a DataSourceInstance with the given descriptor for the given entity
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TimedAnnotatableSourcedBE> Optional<DataSourceInstance> findDataSource(T entity, String entitySourceDescriptor) {
		Assert.notNull(entity);
		Assert.hasText(entitySourceDescriptor);

		return entity.getDataSourceAggregate().getSources().stream().filter(e -> e.getEntitySourceDescriptor().equals(entitySourceDescriptor)).findAny();
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TypedTimedAnnotatableSourcedBE> boolean hasSourceId(T entity, String sourceId) {
		Assert.notNull(entity);
		Assert.hasText(sourceId);

		return entity.getDataSourceAggregate().getSources().stream()
				.anyMatch(e -> e.getEntitySourceId().equals(sourceId));
	}

	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends SourcedBE> boolean hasSourceId(T entity, String sourceId) {
		Assert.notNull(entity);
		Assert.hasText(sourceId);

		return entity.getDataSourceAggregate().getSources().stream()
				.anyMatch(e -> e.getEntitySourceId().equals(sourceId));
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TypedSourcedBE> boolean hasSourceId(T entity, String sourceId) {
		Assert.notNull(entity);
		Assert.hasText(sourceId);

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
	public <T extends TypedTimedAnnotatableSourcedBE> void addSource(T entity, DataSourceInstance source) {		
		Assert.notNull(entity);
		Assert.notNull(source);

		//the source aggregate is a proxy for the entity
		DataSourceAggregate sourceAggregate = entity.getDataSourceAggregate();
		if (sourceAggregate == null) {
			sourceAggregate = new DataSourceAggregate();
			sourceAggregate = dataSourceAggregateRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}
		//connect source aggregate and source
		Optional<DataSourceInstance> existingDataSourceInstance = null;
		try {
		    existingDataSourceInstance = 
				findDataSource(source.getEntitySourceId(), source.getEntitySourceDescriptor(), source.getDatasetId());
		} catch (NullPointerException e) {
			System.out.println("XYZZY");
		}
		if(!existingDataSourceInstance.isPresent()||existingDataSourceInstance.get().getSourceAggregate()==null){
			source.setSourceAggregate(sourceAggregate);
			source = dataSourceInstanceRepo.save(source);
		}else if(!existingDataSourceInstance.get().getSourceAggregate().equals(sourceAggregate)){
			//we tried to create an existing DataSourceInstance but add it to another entity
			//this is not allowed, a source may only produce a single entity
			log.warn("Source already assigned to an existing entity: ("+source.getEntitySourceId()+", "+source.getEntitySourceDescriptor()+", "+source.getDatasetId()+") but must be unique.");				
		}
		entity.setDatasetId(source.getDatasetId());
	}
	
	/**
	 * Adds a new source to the provided entity.<br/>
	 * Finds or creates the necessary source
	 * 
	 * 
	 */
	public <T extends TypedTimedAnnotatableSourcedBE> void addSource(T entity, String entitySourceId, String entitySourceDescriptor,
			DataSourceTypes sourceType, String datasetName) {		
		
		addSource(entity, createDsIfNotExists(entitySourceId, entitySourceDescriptor, sourceType, datasetName));
	}

	/**
	 * Adds a new source to the provided entity.<br/>
	 * Finds or creates the necessary source
	 * 
	 * 
	 */
	public <T extends TimedAnnotatableSourcedBE> void addSource(T entity, String entitySourceId, String entitySourceDescriptor,
			DataSourceTypes sourceType, String datasetName) {		
		
		addSource(entity, createDsIfNotExists(entitySourceId, entitySourceDescriptor, sourceType, datasetName));
	}

	/**
	 * Adds a new source to the provided entity.<br/>
	 * Finds or creates the necessary source
	 * 
	 * 
	 */
	public <T extends TypedSourcedBE> void addSource(T entity, String entitySourceId, String entitySourceDescriptor,
			DataSourceTypes sourceType, String datasetName) {		
		
		addSource(entity, createDsIfNotExists(entitySourceId, entitySourceDescriptor, sourceType, datasetName));
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
	public <T extends TimedAnnotatableSourcedBE> void addSource(T entity, DataSourceInstance source) {
		Assert.notNull(entity);
		Assert.notNull(source);

		//the source aggregate is a proxy for the entity
		DataSourceAggregate sourceAggregate = entity.getDataSourceAggregate();
		if (sourceAggregate == null) {
			sourceAggregate = new DataSourceAggregate();
			sourceAggregate = dataSourceAggregateRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}
		//connect source aggregate and source
		Optional<DataSourceInstance> existingDataSourceInstance = findDataSource(source.getEntitySourceId(), source.getEntitySourceDescriptor(), source.getDatasetId());
		if(!existingDataSourceInstance.isPresent()||existingDataSourceInstance.get().getSourceAggregate()==null){
			source.setSourceAggregate(sourceAggregate);
			source = dataSourceInstanceRepo.save(source);
		}else if(!existingDataSourceInstance.get().getSourceAggregate().equals(entity.getDataSourceAggregate())){
			//we tried to create an existing DataSourceInstance but add it to another entity
			//this is not allowed, a source may only produce a single entity
			log.error("Source already assigned to an existing entity: ("+source.getEntitySourceId()+", "+source.getEntitySourceDescriptor()+", "+source.getDatasetId()+") but must be unique.");				
		}
		entity.setDatasetId(source.getDatasetId());
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
	public <T extends TypedSourcedBE> void addSource(T entity, DataSourceInstance source) {
		Assert.notNull(entity);
		Assert.notNull(source);

		//the source aggregate is a proxy for the entity
		DataSourceAggregate sourceAggregate = entity.getDataSourceAggregate();
		if (sourceAggregate == null) {
			sourceAggregate = new DataSourceAggregate();
			sourceAggregate = dataSourceAggregateRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}
		//connect source aggregate and source
		Optional<DataSourceInstance> existingDataSourceInstance = findDataSource(source.getEntitySourceId(), source.getEntitySourceDescriptor(), source.getDatasetId());
		if(!existingDataSourceInstance.isPresent()){
			source.setSourceAggregate(sourceAggregate);
			source = dataSourceInstanceRepo.save(source);
		}else if(!existingDataSourceInstance.get().getSourceAggregate().equals(entity.getDataSourceAggregate())){
			//we tried to create an existing DataSourceInstance but add it to another entity
			//this is not allowed, a source may only produce a single entity
			log.error("Source already assigned to an existing entity: ("+source.getEntitySourceId()+", "+source.getEntitySourceDescriptor()+", "+source.getDatasetId()+") but must be unique.");				
		}
		entity.setDatasetId(source.getDatasetId());
	}
	
	
	public DataSourceInstance createDsIfNotExists(String entitySourceId, String entitySourceDescriptor,
			DataSourceTypes sourceType, String datasetName) {
		Dataset dst = findOrCreateDataset(datasetName);
		DataSourceInstance ds = new DataSourceInstance(entitySourceId, entitySourceDescriptor, sourceType, dst.getDatasetId());
		ds = dataSourceInstanceRepo.save(ds);
		return ds;
	}

	
	public Dataset findOrCreateDataset(String datasetName) { return createOrGetDataset(datasetName); }
/*		Assert.notNull(datasetName);
		Optional<Dataset> found = datasetRepo.findOneByDatasetName(datasetName);
		if (!found.isPresent()) {
			Dataset ds = new Dataset(datasetName, datasetName);
			ds = datasetRepo.save(ds);
			ds.setDatasetId(ds.getId());
			ds = datasetRepo.save(ds);
			return ds;
		} else {
			return found.get();
		}
	}*/
	
	/**
	 * Returns a Dataset object with the given name if it exists or creates a
	 * new Dataset entity with that name, stores it in the database and
	 * returns the entity object.
	 * 
	 * @param name
	 *            name of the requested Dataset
	 * @return the Dataset object with the given name - either retrieved or
	 *         newly created
	 */
	public Dataset createOrGetDataset(String name) {
		Assert.hasText(name, "Discourse name cannot be empty");
		return datasetRepo.findOneByDatasetName(name).orElseGet(()->{
			Dataset dst = new Dataset(name);
			dst.setDatasetId(0L);
			dst = datasetRepo.save(dst);
			dst.setDatasetId(dst.getId());
			dst = datasetRepo.save(dst);
			return dst;
		});
	}

	/**
	 * Returns a Dataset object with the given name if it exists 
	 * 
	 * @param name name of the requested discourse
	 * @return an Optional containing the Discourse object with the given name if it exists 
	 */
	public Optional<Dataset> findOneDataset(String name) {
		Assert.hasText(name, "Discourse name cannot be empty");
		return datasetRepo.findOneByDatasetName(name);
	}
	
	
}
