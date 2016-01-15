package edu.cmu.cs.lti.discoursedb.core.service.annotation;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBaseEntityWithSource;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Annotations;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.AnnotationInstanceRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.AnnotationsRepository;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class AnnotationService {

	@Autowired private AnnotationInstanceRepository annoInstanceRepo;
	@Autowired private AnnotationsRepository annoRepo;
	
	/**
	 * Retrieves all annotations for the given entity.
	 * 
	 * This is a convenience method. 
	 * It actually just retrieves content from the entity object, but it performs additional null checks on the annotation aggregate.
	 * 
	 * @param entity the entity to retrieve the datasource for
	 * @return a DataSourceInstance with the given descriptor for the given entity
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TimedAnnotatableBaseEntityWithSource> Set<AnnotationInstance> findAnnotations(T entity) {
		Assert.notNull(entity);		
		Annotations annos = entity.getAnnotations();
		return annos==null?new HashSet<AnnotationInstance>():annos.getAnnotations();
	}

	/**
	 * Retrieves all annotations for the given entity.
	 * 
	 * This is a convenience method. 
	 * It actually just retrieves content from the entity object, but it performs additional null checks on the annotation aggregate.
	 * 
	 * @param entity the entity to retrieve the datasource for
	 * @return a DataSourceInstance with the given descriptor for the given entity
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TimedAnnotatableBaseEntity> Set<AnnotationInstance> findAnnotations(T entity) {
		Assert.notNull(entity);		
		Annotations annos = entity.getAnnotations();
		return annos==null?new HashSet<AnnotationInstance>():annos.getAnnotations();
	}
	

	/**
	 * Adds a new annotation instance to the provided entity.<br/>
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param annotation
	 *            the annotation instance to add to the entity
	 */
	public <T extends TimedAnnotatableBaseEntity> void addAnnotation(T entity, AnnotationInstance annotation) {		
		Assert.notNull(entity);
		Assert.notNull(annotation);

		//the annotations aggregate is a proxy for the entity
		//all annotation instances are connected to the aggregate which is finally connected to the annotated entity
		Annotations annoAggregate = entity.getAnnotations();
		if (annoAggregate == null) {
			annoAggregate=annoRepo.save(new Annotations());
			entity.setAnnotations(annoAggregate);
		}
		annotation.setAnnotationAggregate(annoAggregate);
		annotation = annoInstanceRepo.save(annotation);
		annoAggregate.addAnnotation(annotation);
	}

}
