package edu.cmu.cs.lti.discoursedb.core.repository.annotation;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

//@RepositoryRestResource(collectionResourceRel = "annotations2", path = "annotations2")
public interface AnnotationInstanceRepository extends BaseRepository<AnnotationInstance,Long>{
    
    
}
