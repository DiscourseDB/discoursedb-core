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
package edu.cmu.cs.lti.discoursedb.core.service.annotation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.TimedAnnotatableBE;
import edu.cmu.cs.lti.discoursedb.core.model.TypedTimedAnnotatableBE;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationEntityProxy;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationRelation;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.AnnotationEntityProxyRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.AnnotationInstanceRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.AnnotationRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.annotation.FeatureRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.system.SystemUserRepository;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.system.SystemUserService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.AnnotationRelationTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;


@Log4j
@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class AnnotationService {

	private final @NonNull AnnotationInstanceRepository annoInstanceRepo;
	private final @NonNull AnnotationEntityProxyRepository annoRepo;
	private final @NonNull ContributionService contribService;
	private final @NonNull FeatureRepository featureRepo;
	private final @NonNull AnnotationRelationRepository annoRelRepo;
	private final @NonNull SystemUserService userSvc;
	
	/**
	 * Retrieves all annotations for the given entity.
	 * 
	 * This is a convenience method. 
	 * It actually just retrieves annotations from the entity object, but it performs additional null checks on the annotation aggregate.
	 * 
	 * @param entity the entity to retrieve the annotations for
	 * @return a set of AnnotationInstances for the given entity
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TimedAnnotatableBE> Set<AnnotationInstance> findAnnotations(T entity) {
		Assert.notNull(entity,"Entity cannot be null. Provide an annotated entity.");		
		AnnotationEntityProxy annos = entity.getAnnotations();
		System.out.println("Finding annotations for user " + userSvc.getSystemUser());
		return annos==null?new HashSet<AnnotationInstance>():annoRepo.findAllMyAnnotations(annos);
//		return annos==null?new HashSet<AnnotationInstance>():annos.getAnnotations();
	}
	
	/**
	 * Retrieves all annotations for the given entity.
	 * 
	 * This is a convenience method. 
	 * It actually just retrieves annotations from the entity object, but it performs additional null checks on the annotation aggregate.
	 * 
	 * @param entity the entity to retrieve the annotations for
	 * @return a set of AnnotationInstances for the given entity
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public <T extends TypedTimedAnnotatableBE> Set<AnnotationInstance> findAnnotations(T entity) {
		Assert.notNull(entity,"Entity cannot be null. Provide an annotated entity.");		
		AnnotationEntityProxy annos = entity.getAnnotations();
		System.out.println("Finding annotations for user " + userSvc.getSystemUser());
		return annos==null?new HashSet<AnnotationInstance>():annoRepo.findAllMyAnnotations(annos);
//		return annos==null?new HashSet<AnnotationInstance>():annos.getAnnotations();
	}
	
	/**
	 * Creates a new AnnotationInstance and associates it with an AnnotationType that matches the provided String.
	 * If an annotationtype with the provided String already exists, it will be reused.
	 * 
	 * @param type
	 *            the value for the AnnotationType
	 * @param sysUser 
	 * @return a new empty AnnotationInstance that is already saved to the db and
	 *         connected with its requested type
	 */
	public AnnotationInstance createTypedAnnotation(String type){
		Assert.hasText(type,"Type cannot be empty. Provide an annotation type or create untyped AnnotationInstance.");
		
		SystemUser sysUser = userSvc.getSystemUser().orElse(null);
				
		AnnotationInstance annotation = new AnnotationInstance();
		annotation.setType(type);
		annotation.setAnnotator(sysUser);
		return annoInstanceRepo.save(annotation);
	}	
	
	/**
	 * Creates a new Feature with the provided value.
	 * No type is assigned.
	 * 
	 * @param value
	 *            the feature value
	 * @return a new empty Feature that is already saved to the db and
	 *         connected with its requested type
	 */
	public Feature createFeature(String value){
		Assert.hasText(value,"Feature value cannot be empty.");
		
		Feature feature = new Feature();		
		feature.setValue(value);
		return featureRepo.save(feature);
	}	
	
	/**
	 * Creates a new Feature and associates it with a FeatureType that matches the provided String.
	 * If an FeatureType with the provided String already exists, it will be reused.
	 * 
	 * @param value
	 *            the feature value
	 * @param type
	 *            the value for the FeatureType
	 * @return a new empty Feature that is already saved to the db and
	 *         connected with its requested type
	 */
	public Feature createTypedFeature(String value, String type){
		Assert.hasText(value,"Feature value cannot be empty.");
		Assert.hasText(type,"Type cannot be empty. Provide a feature type or create untype feature.");
		
		Feature feature = createTypedFeature(type);		
		feature.setValue(value);		
		return featureRepo.save(feature);
	}	

	/**
	 * Creates a new Feature and associates it with a FeatureType that matches the provided String.
	 * If an FeatureType with the provided String already exists, it will be reused.
	 * 
	 * @param type
	 *            the value for the FeatureType
	 * @return a new empty Feature that is already saved to the db and
	 *         connected with its requested type
	 */
	public Feature createTypedFeature(String type){
		Assert.hasText(type,"Type cannot be empty. Provide a feature type or create untyped feature.");
		
		Feature feature = new Feature();
		feature.setType(type);
		return featureRepo.save(feature);
	}	
	
	/**
	 * Adds a new annotation instance to the provided entity.<br/>
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param annotation
	 *            the annotation instance to add to the entity
	 */
	public <T extends TypedTimedAnnotatableBE> void addAnnotation(T entity, AnnotationInstance annotation) {		
		Assert.notNull(entity,"Entity cannot be null. Provide an annotatable entity.");
		Assert.notNull(annotation, "Annotation cannot be null.");

		//the annotations aggregate is a proxy for the entity
		//all annotation instantimeAnnotatableBaseEntityRepo.ces are connected to the aggregate which is finally connected to the annotated entity
		AnnotationEntityProxy annoProxy = entity.getAnnotations();
		if (annoProxy == null) {
			annoProxy=annoRepo.save(new AnnotationEntityProxy());
			entity.setAnnotations(annoProxy);
		}
		annotation.setAnnotationEntityProxy(annoProxy);
		annotation = annoInstanceRepo.save(annotation);
	}
	
	/**
	 * Adds a new annotation instance to the provided entity.<br/>
	 * 
	 * @param entity
	 *            the entity to add a new source to
	 * @param annotation
	 *            the annotation instance to add to the entity
	 */
	public <T extends TimedAnnotatableBE> void addAnnotation(T entity, AnnotationInstance annotation) {		
		Assert.notNull(entity,"Entity cannot be null. Provide an annotatable entity.");
		Assert.notNull(annotation, "Annotation cannot be null.");

		//the annotations aggregate is a proxy for the entity
		//all annotation instantimeAnnotatableBaseEntityRepo.ces are connected to the aggregate which is finally connected to the annotated entity
		AnnotationEntityProxy annoProxy = entity.getAnnotations();
		if (annoProxy == null) {
			annoProxy=annoRepo.save(new AnnotationEntityProxy());
			entity.setAnnotations(annoProxy);	
		}
		annotation.setAnnotationEntityProxy(annoProxy);
		annotation = annoInstanceRepo.save(annotation);
	}

	/**
	 * Deletes an annotation from DiscourseDB
	 * 
	 * @param id
	 *            id of the annotation instance to delete
	 */
	public void deleteAnnotation(Long id) {		
		Assert.notNull(id,"Annotation id cannot be null.");
		Assert.isTrue(id>0,"Annotation id has to be a positive number.");
		annoInstanceRepo.findOne(id).ifPresent(annotation -> deleteAnnotation(annotation));
	}

	
	/**
	 * Deletes a feature from DiscourseDB
	 * 
	 * @param id
	 *            id of the feature to delete
	 */
	public void deleteFeature(Long id) {		
		Assert.notNull(id,"Feature id cannot be null.");
		Assert.isTrue(id>0,"Feature id has to be a positive number.");
		featureRepo.delete(id);			
	}

	/**
	 * Deletes an annotation from DiscourseDB
	 * 
	 * @param annotation
	 *            the annotation instance to delete
	 */
	public void deleteAnnotation(AnnotationInstance annotation) {		
		Assert.notNull(annotation,"Annotation to delete cannot be null.");
		Set<Feature> features = annotation.getFeatures();
		if(features!=null&&!features.isEmpty()){
			featureRepo.delete(annotation.getFeatures());			
		}
		annoInstanceRepo.delete(annotation);
	}

	/**
	 * Deletes an annotation from DiscourseDB
	 * 
	 * @param annotation
	 *            the annotation instance to add to delete
	 */
	public void deleteAnnotations(Iterable<AnnotationInstance> annotations) {		
		Assert.notNull(annotations, "Annotation iterable cannot be null.");

		List<Feature> featuresToDelete = new ArrayList<>();
		for(AnnotationInstance anno:annotations){
			Set<Feature> features = anno.getFeatures();
			if(features!=null&&!features.isEmpty()){
				featuresToDelete.addAll(features);
			}
		}
		featureRepo.delete(featuresToDelete);
		annoInstanceRepo.delete(annotations);
	}
	
	/**
	 * Checks whether the given enity has an annotation of the given type.
	 * 
	 * @param entity
	 *            the entity to check for annotations
	 * @param type
	 * 			  the annotation type to check for
	 */
	public <T extends TypedTimedAnnotatableBE> boolean hasAnnotationType(T entity, String type) {		
		Assert.notNull(entity,"Entity cannot be null. Provide an annotated entity.");
		Assert.hasText(type,"Type cannot be empty. Provide an annotation type.");		
		return entity.getAnnotations().getAnnotations().stream().filter(e -> e.getType()!=null).anyMatch(e -> e.getType().equalsIgnoreCase(type));		
	}
	
	/**
	 * Checks whether the given enity has an annotation of the given type.
	 * 
	 * @param entity
	 *            the entity to check for annotations
	 * @param type
	 * 			  the annotation type to check for
	 */
	public <T extends TimedAnnotatableBE> boolean hasAnnotationType(T entity, String type) {		
		Assert.notNull(entity,"Entity cannot be null. Provide an annotated entity.");
		Assert.hasText(type,"Type cannot be empty. Provide an annotation type.");		
		return entity.getAnnotations().getAnnotations().stream().filter(e -> e.getType()!=null).anyMatch(e -> e.getType().equalsIgnoreCase(type));		
	}
	
	/**
	 * Adds a new annotation instance to the provided entity.
	 * 
	 * @param annotation
	 *            the annotation to which the feature should be added
	 * @param feature
	 *            the new feature to add
	 */
	public void addFeature(AnnotationInstance annotation, Feature feature) {		
		Assert.notNull(annotation, "Annotation cannot be null.");
		Assert.notNull(feature, "Feature cannot be null.");		
		feature.setAnnotation(annotation);
	}

	/**
	 * Finds all annotations that have a feature matching a type=value pair
	 * 
	 * @param type  The feature type to search for
	 * @param value The value of the feature
	 * @return a List of annotations
	 */
	public List<AnnotationInstance> findAnnotationsByFeatureTypeAndValue(String type, String value) {
	        Assert.hasText(type,"Type cannot be empty. Provide an annotation type or create untyped AnnotationInstance.");
	
	        List<Feature> features = featureRepo.findAllByTypeAndValue(type, value);
	        List<AnnotationInstance> annotations = new ArrayList<AnnotationInstance>();
	        for(Feature f : features) {
	                annotations.add(f.getAnnotation());
	        }
	        return annotations;
	}
	
	/**
	 * Finds all annotations that have a feature matching a type=value pair
	 * 
 	 * FIXME: this is not very generic and also the way the annotations are retrieved should be revised
	 * 
	 * @param dp the discoursepart that contains the contributions which hold the annotations 
	 * @param sysUser 
	 * @return a List of annotations
	 */
	public List<AnnotationInstance> findContributionAnnotationsByDiscoursePart(DiscoursePart dp) {
	        Assert.notNull(dp,"DiscoursePart cannot be null.");
	        List<AnnotationInstance> annos = new ArrayList<>();
	        for(Contribution contrib: contribService.findAllByDiscoursePart(dp)){
	        	if(contrib.getAnnotations()!=null){
	        		//Only if they're readable by sysUser
		        	annos.addAll(this.findAnnotations(contrib));	        		
	        	}
	        }
	        return annos;
	}

	/**
	 * Finds all annotations on content entities that are current revisions of any contribution in the given DiscoursePart
	 * 
	 * FIXME: this is not very generic and also the way the annotations are retrieved should be revised
	 * 
	 * @param dp the discoursepart that contains the contributions of which we extract the currentRevision which hold the annotations 
	 * @return a List of annotations
	 */
	public List<AnnotationInstance> findCurrentRevisionAnnotationsByDiscoursePart(DiscoursePart dp) {
	        Assert.notNull(dp,"DiscoursePart cannot be null.");
	        List<AnnotationInstance> annos = new ArrayList<>();
	        for(Contribution contrib: contribService.findAllByDiscoursePart(dp)){
	        	Content curRevision = contrib.getCurrentRevision();
	        	if(curRevision.getAnnotations()!=null){
	        		//Only if they're readable by sysUser
		        	annos.addAll(this.findAnnotations(curRevision));	        		
	        	}
	        }
	        return annos;
	}	

	public Optional<AnnotationInstance> findOneAnnotationInstance(Long id){
		Optional<AnnotationInstance> oai = annoInstanceRepo.findOne(id); 
		if (oai.isPresent()) {
			if (oai.get().getAnnotator() == null || oai.get().getAnnotator().equals(userSvc.getSystemUser().orElse(null))) {
				return oai;
			} else {
				return Optional.empty();
			}
		} else {
			return oai;
		}
	}
	
	public Optional<Feature> findOneFeature(Long id){
		return featureRepo.findOne(id);
	}

	public List<Long> findAllAnnotationInstanceIds(){
		return annoInstanceRepo.findAllIds();
	}
	public List<Long> findAllFeatureIds(){
		return featureRepo.findAllIds();
	}
	
	public void saveAnnotationInstance(AnnotationInstance anno){
		Assert.notNull(anno, "AnnotationInstance cannot be null.");
		annoInstanceRepo.save(anno);		
	}
	public void saveFeature(Feature feature){
		Assert.notNull(feature, "Feature cannot be null.");
		featureRepo.save(feature);		
	}
	
	/**
	 * Creates a new DiscourseRelation of the given type between the two provided contributions.
	 * Depending on the type, the relation might be directed or not. This information should be given in the type definition.
	 * e.g. a REPLY relation would be interpreted as the target(child) being the reply to the source(parent).
	 * 
	 * If a AnnotationRelation of the given type already exists between the two annotations (taking into account the direction of the relation),
	 * then the existing relation is returned. 
	 * DiscourseDB does not enforce the uniqueness of these relations by default, but enforcing it in this service method will cater to most of the use cases we will see.
	 * 
	 * @param sourceAnnotation the source or parent annotation of the relation
	 * @param targetAnnotation the target or child annotation of the relation
	 * @param type the AnnotationRelationType
	 * @param isHeadOfAnnoChain determines whether this is the first relation in a chain of multiple relations. In that case, the sourceAnnotation of this relation is the source of this chain.
	 * @return a AnnotationRelation between the two provided annotations with the given type that has already been saved to the database 
	 */
	public AnnotationRelation createAnnotationRelation(AnnotationInstance sourceAnnotation, AnnotationInstance targetAnnotation, AnnotationRelationTypes type, boolean isHeadOfAnnoChain) {
		Assert.notNull(sourceAnnotation, "Source annotation cannot be null.");
		Assert.notNull(targetAnnotation, "Target annotation cannot be null.");
		Assert.notNull(type, "Relation type cannot be null.");
								
		//check if a relation of the given type already exists between the two annotations
		//if so, return it. if not, create new relation, configure it and return it.
		return annoRelRepo
				.findOneBySourceAndTargetAndType(sourceAnnotation, targetAnnotation, type.name())
				.orElseGet(() -> {
					AnnotationRelation newRelation = new AnnotationRelation();
					newRelation.setSource(sourceAnnotation);
					newRelation.setTarget(targetAnnotation);
					newRelation.setType(type.name());
					newRelation.setHeadOfChain(isHeadOfAnnoChain);
					return annoRelRepo.save(newRelation);
					}
				);
	}
}
