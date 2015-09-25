package edu.cmu.cs.lti.discoursedb.core.repository.system;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

public interface DataSourceInstanceRepository extends CoreBaseRepository<DataSourceInstance,Long>{
    
	Optional<DataSourceInstance> findOneByEntitySourceIdAndSourceTypeAndDatasetName(String entitySourceId, DataSourceTypes sourceType, String datasetName);

}
