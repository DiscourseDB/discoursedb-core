package edu.cmu.cs.lti.discoursedb.core.repository.annotation;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

@RepositoryRestResource(collectionResourceRel = "annotations", path = "annotations")
public interface AnnotationsRepository extends CoreBaseRepository<Annotations,Long>{
    
    
}
