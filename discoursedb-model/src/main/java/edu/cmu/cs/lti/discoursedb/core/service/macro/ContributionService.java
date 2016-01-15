package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.jpa.impl.JPAQuery;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelationType;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QDiscourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QDiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.QDiscourseToDiscoursePart;
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
	@PersistenceContext private EntityManager entityManager; 
	
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
		Assert.notNull(type);
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
		Assert.notNull(contrib);
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
		Assert.hasText(entitySourceId);
		Assert.hasText(entitySourceDescriptor);
		Assert.hasText(dataSetName);

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
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<Contribution> findAllByType(ContributionTypes type){
		Assert.notNull(type);
		Optional<ContributionType> existingType = contribTypeRepo.findOneByType(type.name());
		if(existingType.isPresent()){
			return contributionRepo.findAllByType(existingType.get());			
		}else{
			return new ArrayList<Contribution>(0);
		}
	}

//	/**
//	 * Returns a list of all contributions for a given discourse
//	 * 
//	 * @param discourse the discourse the contributions need to be associated with
//	 * @return a list of Contributions of the given discourse that potentially might be empty
//	 */
//	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
//	public Iterable<Contribution> findAllByDiscourse(Discourse discourse){
//		Assert.notNull(discourse);
//		return contributionRepo.findAll(ContributionPredicates.contributionHasDiscourse(discourse));			
//	}
	
	/**
	 * Returns a list of all contributions for a given discourse
	 * 
	 * @param discourse the discourse the contributions need to be associated with
	 * @return a list of Contributions of the given discourse that potentially might be empty
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<Contribution> findAllByDiscourse(Discourse curDiscourse){
		Assert.notNull(curDiscourse);
				
		QDiscourse discourse = QDiscourse.discourse; 
		QDiscoursePart discoursePart = QDiscoursePart.discoursePart;
		QContribution contribution = QContribution.contribution;
		QDiscoursePartContribution dpContrib = QDiscoursePartContribution.discoursePartContribution;
		QDiscourseToDiscoursePart dDp = QDiscourseToDiscoursePart.discourseToDiscoursePart;

		//TODO we shouldn't explicitly join on the ids, but make use of the query abstraction
		
		JPQLQuery query = new JPAQuery(entityManager);
		List<Contribution> contribs = 
				query.from(contribution, discourse, discoursePart, dpContrib, dDp)
				.where(contribution.id.eq(dpContrib.contribution.id)
				.and(dpContrib.discoursePart.id.eq(discoursePart.id)
				.and(dDp.discoursePart.id.eq(discoursePart.id)
				.and(dDp.discourse.eq(curDiscourse))))).list(contribution);				
		
		return contribs;			
	}
	
	/**
	 * Returns a list of all contributions for a given DiscoursePart
	 * 
	 * @param discoursePart the discoursePart the contributions need to be associated with
	 * @return a list of Contributions of the given discoursePart that potentially might be empty
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Iterable<Contribution> findAllByDiscoursePart(DiscoursePart discoursePart){
		Assert.notNull(discoursePart);

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
		Assert.notNull(discourse);
		Assert.notNull(type);
		
		Optional<ContributionType> existingType = contribTypeRepo.findOneByType(type.name());
		if(existingType.isPresent()){
			return contributionRepo.findAll(
					ContributionPredicates.contributionHasDiscourse(discourse).and(ContributionPredicates.contributionHasType(type)));			
		}else{
			return new ArrayList<Contribution>(0);
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
		Assert.notNull(sourceContribution);
		Assert.notNull(targetContribution);
		Assert.notNull(type);

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
