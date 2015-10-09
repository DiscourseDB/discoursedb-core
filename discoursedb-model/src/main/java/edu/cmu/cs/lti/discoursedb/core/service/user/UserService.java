package edu.cmu.cs.lti.discoursedb.core.service.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteractionType;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.user.ContributionInteractionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.ContributionInteractionTypeRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionInteractionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private DataSourceService dataSourceService;

	@Autowired
	private ContributionInteractionRepository contribInteractionRepo;

	@Autowired
	private ContributionInteractionTypeRepository contribInteractionTypeRepo;

    public Optional<User> findUserByDiscourseAndSourceIdAndSourceType(Discourse discourse, String sourceId, DataSourceTypes type) {
		return Optional.ofNullable(userRepo.findOne(
						UserPredicates.hasDiscourse(discourse).and(
						UserPredicates.hasSourceId(sourceId)).and(
						UserPredicates.hasDataSourceType(type))));
    }

    public Optional<User> findUserByDiscourseAndSourceIdAndDataSet(Discourse discourse, String sourceId, String dataSetName) {
		return Optional.ofNullable(userRepo.findOne(
						UserPredicates.hasDiscourse(discourse).and(
						UserPredicates.hasSourceId(sourceId)).and(
						UserPredicates.hasDataSet(dataSetName))));
    }

    public Optional<User> findUserByDiscourseAndSourceId(Discourse discourse, String sourceId) {
		return Optional.ofNullable(userRepo.findOne(
						UserPredicates.hasDiscourse(discourse).and(
						UserPredicates.hasSourceId(sourceId))));
    }

    public Optional<User> findUserBySourceIdAndUsername(String sourceId, String username) {
		return Optional.ofNullable(userRepo.findOne(
						UserPredicates.hasSourceId(sourceId).and(
						UserPredicates.hasUserName(username))));
    }

    public Iterable<User> findUsersBySourceId(String sourceId) {
		return userRepo.findAll(UserPredicates.hasSourceId(sourceId));
    }
	
	/**
	 * Returns a User object with the given username and a given discourse if it
	 * exists or creates a new User entity with that username and that discourse.
	 * 
	 * @param discourse
	 *            the discourse in which the user was active
	 * @param username
	 *            username of the requested user
	 * @return the User object with the given username - either retrieved or
	 *         newly created
	 */
	public User createOrGetUser(Discourse discourse, String username) {
		Optional<User> existingUser = Optional.ofNullable(userRepo.findOne(
				UserPredicates.hasDiscourse(discourse).and(
				UserPredicates.hasUserName(username))));	
		User curUser;
		if (existingUser.isPresent()) {
			return existingUser.get();
		} else {
			curUser = new User(discourse);
			curUser.setUsername(username);
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
	public User createOrGetUser(Discourse discourse, String username, String sourceId, DataSourceTypes dataSourceType, String dataSetName) {
		Optional<User> existingUser = findUserByDiscourseAndSourceIdAndDataSet(discourse, sourceId, dataSetName);
		User curUser;
		if (existingUser.isPresent()) {
			curUser = existingUser.get();
		} else {
			curUser = new User(discourse);
			curUser.setUsername(username);
			curUser = userRepo.save(curUser);
			dataSourceService.addSource(curUser, new DataSourceInstance(sourceId,dataSourceType,dataSetName));	
		}
		return curUser;
	}

	/**
	 * Creates a new ContributionInteraction of the provided type and applies
	 * it to the provided user and contribution. A connection to a content
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
	 *         database. If it already existed, the existing entity will be retrieved and returned.
	 */
	public ContributionInteraction createContributionInteraction(User user, Contribution contrib, ContributionInteractionTypes type) {

		//Retrieve type or create if it doesn't exist in db
		ContributionInteractionType contribInteractionType =null;
		Optional<ContributionInteractionType> existingContribInteractionType = contribInteractionTypeRepo.findOneByType(type.name());
		if(existingContribInteractionType.isPresent()){
			contribInteractionType=existingContribInteractionType.get();
		}else{
			contribInteractionType = new ContributionInteractionType();
			contribInteractionType.setType(type.name());
			contribInteractionTypeRepo.save(contribInteractionType);			
		}

		//Retrieve ContributionInteraction or create if it doesn't exist in db
		Optional<ContributionInteraction> existingContribInteraction =  contribInteractionRepo.findOneByUserAndContributionAndType(user, contrib, contribInteractionType.getType());		
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

	
	/**
	 * This method is a convenience method to build a single real name from a first and a last name (each of which might be empty)
	 * 
	 * Sets the real name of the given user based on a given first and last name.
	 * Either first or last name (or both) may be empty - the real name is assembled accordingly.
	 * If a realname was already set before, no operation is performed. The name is not updated.
	 * 
	 * 
	 * @param user the user to update
	 * @param firstName the first name of the user
	 * @param lastName the last name of the user
	 * @return the user with updated or unchanged realname
	 */
	public User setRealname(User user, String firstName, String lastName){
		if(user.getRealname()==null||user.getRealname().isEmpty()){
			if(firstName.isEmpty()){
				if(!lastName.isEmpty()){
					user.setRealname(lastName);
				}
			}else{
				if(lastName.isEmpty()){
					user.setRealname(firstName);
				}else{
					user.setRealname(firstName+" "+lastName);
				}						
			}						
			return save(user);
		}
		return user;
	}
	
	/**
	 * Calls the save method of the user repository, saves the provided User entity and returns it after the save process
	 * @param user the user entity to save
	 * @return the user entity after the save process
	 */
	public User save(User user){
		return userRepo.save(user);
	}

	/**
	 * Removes user from all their discourses and then deletes it by calling
	 * the delete method in the user repository.
	 *  
	 * @param user the user entity to delete
	 */
	public void delete(User user){				
		for(Discourse d:user.getDiscourses()){
			user.removeDiscourse(d);
		}
		userRepo.delete(user);
	}

}
