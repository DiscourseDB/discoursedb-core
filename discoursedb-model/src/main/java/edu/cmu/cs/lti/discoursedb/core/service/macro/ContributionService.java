package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelationType;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class ContributionService {

	@Autowired private ContributionRepository contributionRepo;
	@Autowired private DataSourceService dataSourceService;	
	@Autowired private ContributionTypeRepository contribTypeRepo;
	@Autowired private DiscourseRelationTypeRepository discRelationTypeRepo;
	@Autowired private DiscourseRelationRepository discourseRelationRepo;

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
		Optional<ContributionType> optContribType = contribTypeRepo.findOneByType(type.name());
		ContributionType contribType = null;
		if(optContribType.isPresent()){
			contribType = optContribType.get();
		}else{
			contribType = new ContributionType();
			contribType.setType(type.name());
			contribType= contribTypeRepo.save(contribType);
		}

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
	public Optional<Contribution> findOneByDataSource(String entitySourceId, String entitySourceDescriptor, String dataSetName) {		
		Optional<DataSourceInstance> dataSource = dataSourceService.findDataSource(entitySourceId, entitySourceDescriptor, dataSetName);
		if(dataSource.isPresent()){
			return Optional.ofNullable(contributionRepo.findOne(
					ContributionPredicates.contributionHasDataSource(dataSource.get())));			
		}else{
			return Optional.empty();
		}
	}
	
	/**
	 * Returns a list of all contributions of a given type independent from a Discourse.
	 * 
	 * @param type the contribution type to look for
	 * @return a list of Contributions of the given type that potentially might be empty
	 */
	public List<Contribution> findAllByType(ContributionTypes type){
		Optional<ContributionType> existingType = contribTypeRepo.findOneByType(type.name());
		if(existingType.isPresent()){
			return contributionRepo.findAllByType(existingType.get());			
		}else{
			return new ArrayList<Contribution>(0);
		}
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
		//Retrieve type or create if it doesn't exist in db
		DiscourseRelationType discourseRelationType = null;
		Optional<DiscourseRelationType> existingDiscourseRelationType = discRelationTypeRepo.findOneByType(type.name());
		if(existingDiscourseRelationType.isPresent()){
			discourseRelationType=existingDiscourseRelationType.get();
		}else{
			discourseRelationType = new DiscourseRelationType();
			discourseRelationType.setType(type.name());
			discRelationTypeRepo.save(discourseRelationType);			
		}
		
		//check if a relation of the given type already exists between the two contributions.
		Optional<DiscourseRelation> existingRelation = discourseRelationRepo.findOneBySourceAndTargetAndType(sourceContribution, targetContribution, discourseRelationType);
		if(existingRelation.isPresent()){
			return existingRelation.get();
		}
		
		//create, save and return the new relation
		DiscourseRelation newRelation = new DiscourseRelation();
		newRelation.setSource(sourceContribution);
		newRelation.setTarget(targetContribution);
		newRelation.setType(discourseRelationType);
		return discourseRelationRepo.save(newRelation);
	}
	

}
