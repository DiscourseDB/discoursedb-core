package edu.cmu.cs.lti.discoursedb.core.repository.annotation;

import java.util.List;

import org.springframework.data.rest.core.annotation.RestResource;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface FeatureRepository extends BaseRepository<Feature,Long>{

	List<Feature> findAllByTypeAndValue(String type, String value);
    
    
}
