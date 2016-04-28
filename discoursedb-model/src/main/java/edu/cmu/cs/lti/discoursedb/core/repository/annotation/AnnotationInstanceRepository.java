package edu.cmu.cs.lti.discoursedb.core.repository.annotation;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface AnnotationInstanceRepository extends BaseRepository<AnnotationInstance,Long>{
	@Query("SELECT a.id FROM AnnotationInstance a") 
	List<Long> findAllIds();   

    
}
