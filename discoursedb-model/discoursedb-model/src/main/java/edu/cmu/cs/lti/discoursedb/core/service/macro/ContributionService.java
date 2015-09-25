package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionType;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class ContributionService {

	@Autowired
	private ContributionRepository contributionRepo;

	@Autowired
	private DataSourceService dataSourceService;
	
	@Autowired
	private ContributionTypeRepository contribTypeRepo;

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
	 * @param type the type of the source
	 * @param dataSetName the dataset the source id was derived from
	 * @return an optional contribution that meets the requested parameters
	 */
	public Optional<Contribution> findOneByDataSource(String entitySourceId, String dataSetName) {		
		Optional<DataSourceInstance> dataSource = dataSourceService.getDataSource(entitySourceId, dataSetName);
		if(dataSource.isPresent()){
			return Optional.ofNullable(contributionRepo.findOne(
					ContributionPredicates.contributionHasDataSource(dataSource.get())));			
		}else{
			return Optional.empty();
		}
	}
	
	

}
