package edu.cmu.cs.lti.discoursedb.core.service.user;

import java.util.Optional;

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
	public User createOrGetUserBySourceId(Discourse discourse, String sourceid) {
		Optional<User> curOptUser = userRepo.findAllBySourceId(sourceid).stream()
				.filter(u -> u.getDiscourses().contains(discourse)).findFirst();
		User curUser;
		if (curOptUser.isPresent()) {
			return curOptUser.get();
		} else {
			curUser = new User();
			curUser.setSourceId(sourceid);
			curUser.addDiscourses(discourse);
			return save(curUser);			
		}
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
	public User createOrGetUserByUsername(Discourse discourse, String username) {
		Optional<User> curOptUser = userRepo.findAllByUsername(username).stream()
				.filter(u -> u.getDiscourses().contains(discourse)).findFirst();	
		User curUser;
		if (curOptUser.isPresent()) {
			return curOptUser.get();
		} else {
			curUser = new User();
			curUser.setUsername(username);
			curUser.addDiscourses(discourse);
			return save(curUser);			
		}
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
	public User createOrGetUser(Discourse discourse, String sourceId, String username) {
		Optional<User> curOptUser = userRepo.findBySourceIdAndUsername(sourceId, username);
		User curUser;
		if (curOptUser.isPresent()) {
			curUser = curOptUser.get();
			if(!curUser.getDiscourses().contains(discourse)){
				curUser.addDiscourses(discourse);
			}
		} else {
			curUser = new User();
			curUser.setSourceId(sourceId);
			curUser.setUsername(username);
			curUser.addDiscourses(discourse);
		}
		return userRepo.save(curUser);
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
	
	/**
	 * Calls the save method of the user repository, saves the provided User entity and returns it after the save process
	 * @param user the user entity to save
	 * @return the user entity after the save process
	 */
	public User save(User user){
		return userRepo.save(user);
	}
}
