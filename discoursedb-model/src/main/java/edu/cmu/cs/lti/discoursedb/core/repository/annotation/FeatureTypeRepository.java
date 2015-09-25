package edu.cmu.cs.lti.discoursedb.core.repository.annotation;

import java.util.Set;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.FeatureType;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface FeatureTypeRepository extends CoreBaseRepository<FeatureType,Long>{
    
	Set<FeatureType> findAllByType(String type);
}
