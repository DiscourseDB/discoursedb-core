package edu.cmu.cs.lti.discoursedb.core.service.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteractionType;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.user.ContributionInteractionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.ContributionInteractionTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionInteractionTypes;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service
public class UserInteractionService {

	@Autowired
	private ContributionInteractionRepository contribInteractionRepo;

	@Autowired
	private ContributionInteractionTypeRepository contribInteractionTypeRepo;

	
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
	public ContributionInteraction createTypedContributionIteraction(User user, Contribution contrib, ContributionInteractionTypes type){		
		Optional<ContributionInteractionType> existingContribInteractionType = contribInteractionTypeRepo.findOneByType(type.name());
		ContributionInteractionType contribInteractionType = null;
		if(existingContribInteractionType.isPresent()){
			contribInteractionType = existingContribInteractionType.get();
		}else{
			contribInteractionType = new ContributionInteractionType();
			contribInteractionType.setType(type.name());
			contribInteractionType= contribInteractionTypeRepo.save(contribInteractionType);
		}
		
		//TODO can we assume that the created interaction is unique?
		Optional<ContributionInteraction> existingContribInteraction = contribInteractionRepo.findOneByUserAndContributionAndType(user, contrib, contribInteractionType);
		if(existingContribInteraction.isPresent()){
			return existingContribInteraction.get();
		}else{
			ContributionInteraction contribInteraction = new ContributionInteraction();
			contribInteraction.setContribution(contrib);
			contribInteraction.setUser(user);
			contribInteraction.setType(contribInteractionType);
			return contribInteractionRepo.save(contribInteraction);			
		}
	}	
	
	
}
