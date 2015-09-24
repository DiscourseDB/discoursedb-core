package edu.cmu.cs.lti.discoursedb.core.service.system;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.UntimedBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSources;
import edu.cmu.cs.lti.discoursedb.core.repository.system.DataSourcesRepository;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service
public class DataSourceService {
	
	@Autowired
	private DataSourcesRepository dataSourcesRepo;

	public <T extends TimedAnnotatableBaseEntityWithSource> boolean hasSourceId(T entity, String sourceId){
		return entity.getDataSourceAggregate().getSources().stream().anyMatch(e->e.getEntitySourceId().equals(sourceId));
	}
	
	public <T extends UntimedBaseEntityWithSource> boolean hasSourceId(T entity, String sourceId){
		return entity.getDataSourceAggregate().getSources().stream().anyMatch(e->e.getEntitySourceId().equals(sourceId));
	}
	
	/**
	 * Adds a new source to the provided entity.
	 * 
	 * @param entity the entity to add a new source to
	 * @param source the source to add to the entity
	 */
	public <T extends TimedAnnotatableBaseEntityWithSource> void addSource(T entity, DataSourceInstance source){
		//check if sourceAggregate exists and create if not
		DataSources sourceAggregate = entity.getDataSourceAggregate();
		if(sourceAggregate==null){
			sourceAggregate=new DataSources();
			sourceAggregate=dataSourcesRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}

		//check if source exists in aggregate and add if not
		if(!sourceAggregate.getSources().contains(source)){
			entity.getDataSourceAggregate().addSource(source);					
		}
	}

	/**
	 * Adds a new source to the provided entity.
	 * 
	 * @param entity the entity to add a new source to
	 * @param source the source to add to the entity
	 */
	public <T extends UntimedBaseEntityWithSource> void addSource(T entity, DataSourceInstance source){
		//check if sourceAggregate exists and create if not
		DataSources sourceAggregate = entity.getDataSourceAggregate();
		if(sourceAggregate==null){
			sourceAggregate=new DataSources();
			sourceAggregate=dataSourcesRepo.save(sourceAggregate);
			entity.setDataSourceAggregate(sourceAggregate);
		}

		//check if source exists in aggregate and add if not
		if(!sourceAggregate.getSources().contains(source)){
			entity.getDataSourceAggregate().addSource(source);					
		}
	}

	
}
