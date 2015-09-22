package edu.cmu.cs.lti.discoursedb.core.service.macro;

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
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionInteractionTypes;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ContributionInteractionRepository contribInteractionRepo;

	@Autowired
	private ContributionInteractionTypeRepository contribInteractionTypeRepo;

	/**
	 * Creates a new Contribution interaction of the provided type and applies
	 * it to the provided user and contribution. A connections to a content
	 * entity is optional and is not established by this method. This is
	 * necessary e.g. for REVERT interactions.
	 * 
	 * @param user
	 *            the user to interact with the provided contribution
	 * @param contrib
	 *            the contribution the provided user interacts with
	 * @param type
	 *            the type of the interaction
	 * @return the ContributionInteraction object after being saved to the
	 *         database
	 */
	public ContributionInteraction createContributionInteraction(User user, Contribution contrib,
			ContributionInteractionTypes type) {
		ContributionInteractionType contribInteractionType = new ContributionInteractionType();
		contribInteractionType.setType(type.name());
		contribInteractionTypeRepo.save(contribInteractionType);
		
		ContributionInteraction contribInteraction = new ContributionInteraction();
		contribInteraction.setContribution(contrib);
		contribInteraction.setUser(user);
		contribInteraction.setType(contribInteractionType);
		return contribInteractionRepo.save(contribInteraction);
	}

	
	public Optional<User> findBySourceIdAndUsername(String sourceId, String username){
		return userRepo.findBySourceIdAndUsername(sourceId, username);
	}
}
