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
package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionContext;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionContextRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.ContextTypes;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class ContributionService {

	private final @NonNull ContributionRepository contributionRepo;
	private final @NonNull ContributionContextRepository contributionContextRepo;
	private final @NonNull DataSourceService dataSourceService;	
	private final @NonNull DiscourseRelationRepository discourseRelationRepo;
	
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
		
		Contribution contrib = new Contribution();
		contrib.setType(type.name());
		return contributionRepo.save(contrib);
	}		
	
	
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
	public Contribution createContribution(){
		Contribution contrib = new Contribution();
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

	public Contribution getOneRelatedContribution(Contribution c) {
		if (c.getSourceOfDiscourseRelations().size() > 0) {
			return c.getSourceOfDiscourseRelations().iterator().next().getTarget();
		} else { 
			return null;
		}
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
	
	
	public Contribution createOrGetByDataSource(String entitySourceId, 
			String entitySourceDescriptor, DataSourceTypes sourceType, String datasetName) {
		Assert.hasText (entitySourceId, "");		
		Assert.hasText(entitySourceDescriptor, "Entity source descriptor cannot be empty");
		Assert.hasText(datasetName, "Dataset name cannot be empty.");

		Optional<Contribution> oc = findOneByDataSource(entitySourceId, entitySourceDescriptor, datasetName);
		Contribution c = null;
		if (oc.isPresent()) {
			c = oc.get();
		} else {
			c = createContribution();
			DataSourceInstance ds = new DataSourceInstance(entitySourceId, entitySourceDescriptor, sourceType, datasetName);
			dataSourceService.addSource(c, ds);
		}
		
		return c;
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
		return contributionRepo.findAllByType(type.name());
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
	 * Returns a list of all contributions initially created by a given user.
	 * This does not contain contributions a user has revised but that were created by another user.
	 * 
	 * @param user that created the contributions
	 * @return a list of Contributions initially created by the provided user
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Iterable<Contribution> findAllByFirstRevisionUser(User user){
		Assert.notNull(user, "User cannot be null.");
		return contributionRepo.findAll(ContributionPredicates.contributionWithFirstRevisionByUser(user));			
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
		
		return contributionRepo.findAll(ContributionPredicates.contributionHasDiscourse(discourse).and(ContributionPredicates.contributionHasType(type)));			
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
	 * Deletes a given contribution entity
	 */
	public void delete(Contribution contrib){
			contributionRepo.delete(contrib);
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
								
		//check if a relation of the given type already exists between the two contributions
		//if so, return it. if not, create new relation, configure it and return it.
		return discourseRelationRepo
				.findOneBySourceAndTargetAndType(sourceContribution, targetContribution, type.name())
				.orElseGet(() -> {
					DiscourseRelation newRelation = new DiscourseRelation();
					newRelation.setSource(sourceContribution);
					newRelation.setTarget(targetContribution);
					newRelation.setType(type.name());
					return discourseRelationRepo.save(newRelation);
					}
				);
	}
	
	
	/**
	 * Creates a new Contribution entity with the provided context type.
	 * 
	 * @param type
	 *            the context type
	 * @return a new Contribution of the given context type that is already saved to the db 
	 */
	public Contribution createTypedContext(ContextTypes type){
		Assert.notNull(type, "Context type cannot be null.s");
		Contribution context = new Contribution();
		context.setType(type.name());
		return contributionRepo.save(context);
	}		
	
	/**
	 * Adds the given contribution "context" as context to the given contribution "contrib".
	 * No start or end date is set for this relation.
	 * 
	 * @param contrib the contribution that receives the new context
	 * @param context the contribution serving as context for the other contribution
	 */
	public ContributionContext addContextToContribution(Contribution contrib, Contribution context){	
		Assert.notNull(context, "Context cannot be null.");
		Assert.notNull(contrib, "Contribution to add to Context cannot be null.");
		
		return contributionContextRepo.findOneByContributionAndContextContribution(contrib, context).orElseGet(()->{
			ContributionContext newContributionContext = new ContributionContext();
			newContributionContext.setContribution(contrib);
			newContributionContext.setContextContribution(context);
			contributionContextRepo.save(newContributionContext);
			return newContributionContext;
		});
	}

	/**
	 * Adds the given content "context" as context to the given contribution "contrib".
	 * No start or end date is set for this relation and no begin or end offset is defined to specify a span within the content.
	 * 
	 * @param contrib the contribution that receives the new context
	 * @param context the content serving as context for the other contribution
	 */
	public ContributionContext addContextToContribution(Contribution contrib, Content context){	
		Assert.notNull(context, "Context cannot be null.");
		Assert.notNull(contrib, "Contribution to add to Context cannot be null.");
		
		return contributionContextRepo.findOneByContributionAndContextContent(contrib, context).orElseGet(()->{
			ContributionContext newContributionContext = new ContributionContext();
			newContributionContext.setContribution(contrib);
			newContributionContext.setContextContent(context);
			contributionContextRepo.save(newContributionContext);
			return newContributionContext;
		});
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
		return contributionRepo.findOne(id);
	}
}
