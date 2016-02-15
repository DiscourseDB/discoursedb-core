package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelationType;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class ContributionService {

	private final @NonNull ContributionRepository contributionRepo;
	private final @NonNull DataSourceService dataSourceService;	
	private final @NonNull ContributionTypeRepository contribTypeRepo;
	private final @NonNull DiscourseRelationTypeRepository discRelationTypeRepo;
	private final @NonNull DiscourseRelationRepository discourseRelationRepo;
	private final @NonNull @PersistenceContext EntityManager entityManager; 
	
	/**
	 * Retrieves existing or creates a new ContributionType entity with the
	 * provided type. It then creates a new empty Contribution entity and
	 * connects it with the type. Both changed/created entities are saved to
	 * DiscourseDB and the empty typed Contribution is returned. It then adds
	 * the new empty Contribution to the db and returns the object.
	 * 
	 * @param type
	 *            the value for the ContributionTyep
	 * @return a new empty Contribution that is already saved to the db and
	 *         connected with its requested type
	 */
	public Contribution createTypedContribution(ContributionTypes type){
		Assert.notNull(type, "Contribution type cannot be null.");
		
		ContributionType contribType = contribTypeRepo.findOneByType(type.name()).orElseGet(()->{
			ContributionType newType = new ContributionType();
			newType.setType(type.name());
			return contribTypeRepo.save(newType);
			}
		);

		Contribution contrib = new Contribution();
		contrib.setType(contribType);
		return contributionRepo.save(contrib);
	}		
	
	/**
	 * Saves the provided entity to the db using the save method of the corresponding repository
	 * 
	 * @param contrib the entity to save
	 * @return the possibly altered entity after the save process 
	 */
	public Contribution save(Contribution contrib){
		Assert.notNull(contrib, "Contribution to save cannot be null.");
		return contributionRepo.save(contrib);
	}

	
	/**
	 * Retrieves a contribution that has a source which exactly matches the given DataSource parameters.
	 * 
	 * @param entitySourceId the source id of the contribution  
	 * @param entitySourceDescriptor the entitySourceDescriptor
	 * @param dataSetName the dataset the source id was derived from
	 * @return an optional contribution that meets the requested parameters
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<Contribution> findOneByDataSource(String entitySourceId, String entitySourceDescriptor, String dataSetName) {
		Assert.hasText(entitySourceId, "Entity source id cannot be empty.");
		Assert.hasText(entitySourceDescriptor, "Entity source descriptor cannot be empty");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");

		return dataSourceService.findDataSource(entitySourceId, entitySourceDescriptor, dataSetName)
				.map(s -> Optional.ofNullable(contributionRepo.findOne(ContributionPredicates.contributionHasDataSource(s))))
				.orElse(Optional.empty());
	}
	
	/**
	 * Returns a list of all contributions of a given type independent from a Discourse.
	 * 
	 * @param type the contribution type to look for
	 * @return a list of Contributions of the given type that potentially might be empty
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<Contribution> findAllByType(ContributionTypes type){
		Assert.notNull(type, "Type cannot be null.");		

		return contribTypeRepo.findOneByType(type.name()).map(t -> contributionRepo.findAllByType(t))
				.orElse(Collections.emptyList());

	}

	/**
	 * Returns a list of all contributions for a given discourse
	 * 
	 * @param discourse the discourse the contributions need to be associated with
	 * @return a list of Contributions of the given discourse that potentially might be empty
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Iterable<Contribution> findAllByDiscourse(Discourse discourse){
		Assert.notNull(discourse, "Discourse cannot be null.");
		return contributionRepo.findAll(ContributionPredicates.contributionHasDiscourse(discourse));			
	}
	
	
	/**
	 * Returns a list of all contributions for a given DiscoursePart
	 * 
	 * @param discoursePart the discoursePart the contributions need to be associated with
	 * @return a list of Contributions of the given discoursePart that potentially might be empty
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Iterable<Contribution> findAllByDiscoursePart(DiscoursePart discoursePart){
		Assert.notNull(discoursePart, "DiscoursePart cannot be null.");
		return contributionRepo.findAll(ContributionPredicates.contributionHasDiscoursePart(discoursePart));			
	}
	
	/**
	 * Returns a list of all contributions of a given type that are associated with the given discourse
	 * 
	 * @param discourse the discourse the contributions need to be associated with
	 * @param type the contribution type to look for
	 * @return a list of Contributions of the given type and discourse that potentially might be empty
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Iterable<Contribution> findAllByType(Discourse discourse, ContributionTypes type){
		Assert.notNull(discourse, "Discourse cannot be null");
		Assert.notNull(type, "Type cannot be null");
		
		Optional<ContributionType> existingType = contribTypeRepo.findOneByType(type.name());
		if(existingType.isPresent()){
			return contributionRepo.findAll(
					ContributionPredicates.contributionHasDiscourse(discourse).and(ContributionPredicates.contributionHasType(type)));			
		}else{
			return Collections.emptyList();
		}
	}
	
	/**
	 * Returns a list of all contributions in the database no matter what type they or what discourse they are part of
	 * 
	 * @return a list of all contributions in the database
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Iterable<Contribution> findAll(){
			return contributionRepo.findAll();
	}
	
	/**
	 * Creates a new DiscourseRelation of the given type between the two provided contributions.
	 * Depending on the type, the relation might be directed or not. This information should be given in the type definition.
	 * e.g. a REPLY relation would be interpreted as the target(child) being the reply to the source(parent).
	 * 
	 * If a DiscourseRelation of the given type already exists between the two contributions (taking into account the direction of the relation),
	 * then the existing relation is returned. 
	 * DiscourseDB does not enforce the uniqueness of these relations by default, but enforcing it in this service method will cater to most of the use cases we will see.
	 * 
	 * @param sourceContribution the source or parent contribution of the relation
	 * @param targetContribution the target or child contribution of the relation
	 * @param type the DiscourseRelationType
	 * @return a DiscourseRelation between the two provided contributions with the given type that has already been saved to the database 
	 */
	public DiscourseRelation createDiscourseRelation(Contribution sourceContribution, Contribution targetContribution, DiscourseRelationTypes type) {
		Assert.notNull(sourceContribution, "Source contribution cannot be null.");
		Assert.notNull(targetContribution, "Target contribution cannot be null.");
		Assert.notNull(type, "Relation type cannot be null.");

		//Retrieve type or create if it doesn't exist in db
		DiscourseRelationType discourseRelationType = discRelationTypeRepo.findOneByType(type.name()).orElseGet(()->{
			DiscourseRelationType newType = new DiscourseRelationType();
			newType.setType(type.name());
			return discRelationTypeRepo.save(newType);
			}
		);
								
		//check if a relation of the given type already exists between the two contributions
		//if so, return it. if not, create new relation, configure it and return it.
		return discourseRelationRepo
				.findOneBySourceAndTargetAndType(sourceContribution, targetContribution, discourseRelationType)
				.orElseGet(() -> {
					DiscourseRelation newRelation = new DiscourseRelation();
					newRelation.setSource(sourceContribution);
					newRelation.setTarget(targetContribution);
					newRelation.setType(discourseRelationType);
					return discourseRelationRepo.save(newRelation);
					}
				);
	}
	
	/**
	 * Returns a Contribution given it's primary key
	 * 
	 * @param Long id the primary key of the contribution
	 * @return an Optional that contains the contribution if it exists
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Optional<Contribution> findOne(Long id){
		Assert.notNull(id, "ID cannot be null.");
		Contribution contrib = contributionRepo.findOne(id);
		return contrib==null?Optional.empty():Optional.of(contrib);		
	}
}
