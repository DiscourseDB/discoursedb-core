package edu.cmu.cs.lti.discoursedb.core.service.user;

import java.util.Optional;

import org.hibernate.cfg.NotYetImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
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
	 * Returns a User object with the given sourceid and a given discourse if it
	 * exists or creates a new User entity with that sourceid.
	 * 
	 * @param discourse
	 *            the discourse in which the user was active
	 * @param sourceid
	 *            sourceId of the requested user
	 * @return the User object with the given sourceid - either retrieved or
	 *         newly created
	 */
	public User createOrGetUserWithSourceId(Discourse discourse, String sourceid) {
		throw new NotYetImplementedException();		
	}
	
	/**
	 * Returns a User object with the given username and a given discourse if it
	 * exists or creates a new User entity with that username.
	 * 
	 * @param discourse
	 *            the discourse in which the user was active
	 * @param username
	 *            username of the requested user
	 * @return the User object with the given username - either retrieved or
	 *         newly created
	 */
	public User createOrGetUserWithUsername(Discourse discourse, String username) {
		throw new NotYetImplementedException();		
	}

	/**
	 * Returns a User object with the given source id and username if it exists
	 * or creates a new User entity with that username, stores it in the
	 * database and returns the entity object.
	 * 
	 * @param sourceId
	 *            the user id assigned by the source system
	 * @param username
	 *            the username of the requested user
	 * @return the User object with the given username and source id- either retrieved or
	 *         newly created
	 */
	public User createOrGetUser(String sourceId, String username) {
		Optional<User> curOptUser = userRepo.findBySourceIdAndUsername(sourceId, username);
		User curUser;
		if (curOptUser.isPresent()) {
			curUser = curOptUser.get();
		} else {
			curUser = new User();
			curUser.setSourceId(sourceId);
			curUser.setUsername(username);
			curUser = userRepo.save(curUser);
		}
		return curUser;
	}

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

	public Optional<User> findBySourceIdAndUsername(String sourceId, String username) {
		return userRepo.findBySourceIdAndUsername(sourceId, username);
	}
}
