package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionType;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class ContributionService {

	@Autowired
	private ContributionRepository contributionRepo;
	
	@Autowired
	private ContributionTypeRepository contribTypeRepo;

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
	
	public Contribution save(Contribution contrib){
		return contributionRepo.save(contrib);
	}
	
	public Optional<Contribution> findOneBySourceId(String id){
		return contributionRepo.findOneBySourceId(id);		
	}

}
