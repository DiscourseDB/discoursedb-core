package edu.cmu.cs.lti.discoursedb.core.repository.annotation;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationRelation;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface AnnotationRelationRepository extends BaseRepository<AnnotationRelation,Long>{

	Optional<AnnotationRelation> findOneBySourceAndTargetAndType(AnnotationInstance source, AnnotationInstance Target, String type);	

    
}
