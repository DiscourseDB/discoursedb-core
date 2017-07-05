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
package edu.cmu.cs.lti.discoursedb.core.service.user;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.security.core.context.SecurityContextHolder;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.DiscoursePartInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.model.user.UserRelation;
import edu.cmu.cs.lti.discoursedb.core.repository.system.SystemUserRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.ContributionInteractionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.DiscoursePartInteractionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionInteractionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartInteractionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.UserRelationTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class UserService {

	private final @NonNull UserRepository userRepo;
	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserRelationRepository userRelationRepo;
	private final @NonNull ContributionInteractionRepository contribInteractionRepo;
	private final @NonNull DiscoursePartInteractionRepository discoursePartInteractionRepo;
	private final @NonNull DiscoursePartService discoursePartService;

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Optional<User> findUserByDiscourseAndSourceIdAndSourceType(Discourse discourse, String sourceId,
			DataSourceTypes type) {
		Assert.notNull(discourse, "The discourse cannot be null.");
		Assert.hasText(sourceId, "The sourceId cannot be empty.");
		Assert.notNull(type, "You have to provide a datasource type.");

		return Optional.ofNullable(userRepo.findOne(UserPredicates.hasDiscourse(discourse)
				.and(UserPredicates.hasSourceId(sourceId)).and(UserPredicates.hasDataSourceType(type))));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Optional<User> findUserByDiscourseAndSourceIdAndDataSet(Discourse discourse, String sourceId,
			String dataSetName) {
		Assert.notNull(discourse, "The discourse cannot be null.");
		Assert.hasText(sourceId, "The sourceId cannot be empty.");
		Assert.hasText(dataSetName, "The dataset name cannot be empty.");

		return Optional.ofNullable(userRepo.findOne(UserPredicates.hasDiscourse(discourse)
				.and(UserPredicates.hasSourceId(sourceId)).and(UserPredicates.hasDataSet(dataSetName))));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Optional<User> findUserByDiscourseAndSourceId(Discourse discourse, String sourceId) {
		Assert.notNull(discourse, "The discourse cannot be null.");
		Assert.hasText(sourceId, "The sourceId cannot be empty.");

		return Optional.ofNullable(
				userRepo.findOne(UserPredicates.hasDiscourse(discourse).and(UserPredicates.hasSourceId(sourceId))));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Optional<User> findUserBySourceIdAndUsername(String sourceId, String username) {
		Assert.hasText(sourceId, "The sourceId cannot be empty.");
		Assert.hasText(username, "The username cannot be empty.");

		return Optional.ofNullable(
				userRepo.findOne(UserPredicates.hasSourceId(sourceId).and(UserPredicates.hasUserName(username))));
	}

	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Optional<User> findUserByDiscourseAndUsername(Discourse discourse, String username) {
		Assert.notNull(discourse, "The discourse cannot be null.");
		Assert.hasText(username, "The username cannot be empty.");

		return Optional.ofNullable(
				userRepo.findOne(UserPredicates.hasDiscourse(discourse).and(UserPredicates.hasUserName(username))));
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Iterable<User> findUsersBySourceId(String sourceId) {
		Assert.hasText(sourceId, "The sourceId cannot be empty.");

		return userRepo.findAll(UserPredicates.hasSourceId(sourceId));
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public Iterable<User> findUsersByDiscourse(Discourse discourse) {
		Assert.notNull(discourse, "The discourse cannot be null.");
		return userRepo.findAll(UserPredicates.hasDiscourse(discourse));
	}

	/**
	 * Returns a User object with the given username and a given discourse if it
	 * exists or creates a new User entity with that username and that
	 * discourse.
	 * 
	 * @param discourse
	 *            the discourse in which the user was active
	 * @param username
	 *            username of the requested user
	 * @return the User object with the given username - either retrieved or
	 *         newly created
	 */
	public User createOrGetUser(Discourse discourse, String username) {
		Assert.notNull(discourse, "Discourse cannot be null.");
		Assert.hasText(username, "Username cannot be empty.");

		return Optional.ofNullable(userRepo.findOne(UserPredicates.hasDiscourse(discourse).and(UserPredicates.hasUserName(username)))).
				orElseGet(() -> {
					User curUser = new User(discourse);
					curUser.setUsername(username);
					return save(curUser);
					}
				);
		}
	
	
	
	/**
	 * Returns a User object with the given source id and username if it exists
	 * or creates a new User entity with that username, stores it in the
	 * database and returns the entity object.
	 * 
	 * @param sourceId
	 *            the user id assigned by the source system
	 * @param sourceDescriptor
	 *            defines where the sourceId is defined in the source data (e.g.
	 *            "user.id" if it is a user table with field id)
	 * @param username
	 *            the username of the requested user
	 * @return the User object with the given username and source id- either
	 *         retrieved or newly created
	 */
	public User createOrGetUser(Discourse discourse, String username, String sourceId, String sourceIdDescriptor,
			DataSourceTypes dataSourceType, String dataSetName) {
		Assert.notNull(discourse, "Discourse cannot be null");
		Assert.hasText(username, "Username cannot be empty.");
		Assert.hasText(sourceId, "SourceId cannot be empty.");
		Assert.hasText(sourceIdDescriptor, "SourceId descriptor cannot be empty.");
		Assert.notNull(dataSourceType, "You have to provide a datasource type.");
		Assert.hasText(dataSetName, "Dataset name cannot be empty.");

		return findUserByDiscourseAndSourceIdAndDataSet(discourse, sourceId, dataSetName).orElseGet(()->{
			User curUser = new User(discourse);
			curUser.setUsername(username);
			curUser = userRepo.save(curUser);
			dataSourceService.addSource(curUser,
					new DataSourceInstance(sourceId, sourceIdDescriptor, dataSourceType, dataSetName));
			return curUser;
			}
		);
	}

	/**
	 * Creates a new ContributionInteraction of the provided type and applies it
	 * to the provided user and contribution. A connection to a content entity
	 * is optional and is not established by this method. This is necessary e.g.
	 * for REVERT interactions.
	 * 
	 * @param user
	 *            the user to interact with the provided contribution
	 * @param contrib
	 *            the contribution the provided user interacts with
	 * @param type
	 *            the type of the interaction
	 * @return the ContributionInteraction object after being saved to the
	 *         database. If it already existed, the existing entity will be
	 *         retrieved and returned.
	 */
	public ContributionInteraction createContributionInteraction(User user, Contribution contrib,
			ContributionInteractionTypes type) {
		Assert.notNull(user, "User cannot be null.");
		Assert.notNull(contrib, "Contribution cannot be null.");
		Assert.notNull(type, "You have to provive a ContributionInteraction type.");

		// Retrieve ContributionInteraction or create if it doesn't exist in db
		return contribInteractionRepo
				.findOneByUserAndContributionAndType(user, contrib, type.name()).orElseGet(()->{
					ContributionInteraction contribInteraction = new ContributionInteraction();
					contribInteraction.setContribution(contrib);
					contribInteraction.setUser(user);
					contribInteraction.setType(type.name());
					return contribInteractionRepo.save(contribInteraction);
					}
				);
	}

	/**
	 * Creates a new DiscoursePartInteraction of the provided type and applies
	 * it to the provided user and discoursepart.
	 * 
	 * @param user
	 *            the user to interact with the provided contribution
	 * @param dp
	 *            the discoursepart the provided user interacts with
	 * @param type
	 *            the type of the interaction
	 * @return the DiscoursePartInteraction object after being saved to the
	 *         database. If it already existed, the existing entity will be
	 *         retrieved and returned.
	 */
	public DiscoursePartInteraction createDiscoursePartInteraction(User user, DiscoursePart dp,
			DiscoursePartInteractionTypes type) {
		Assert.notNull(user, "User cannot be null.");
		Assert.notNull(dp, "DiscoursePart cannot be null.");
		Assert.notNull(type, "You have to provide a DiscoursePartInteraction type.");

		// Retrieve ContributionInteraction or create if it doesn't exist in db
		return discoursePartInteractionRepo
				.findOneByUserAndDiscoursePartAndType(user, dp, type.name()).orElseGet(()->{
					DiscoursePartInteraction dpInteraction = new DiscoursePartInteraction();
					dpInteraction.setDiscoursePart(dp);
					dpInteraction.setUser(user);
					dpInteraction.setType(type.name());
					return discoursePartInteractionRepo.save(dpInteraction);
					}
				);
	}

	/**
	 * Creates a new UserRelation of the provided type and applies it to the
	 * provided source and target user.
	 * 
	 * @param sourceUser
	 *            the user that establishes the relation (e.g. follower))
	 * @param targetUser
	 *            the user that is the target of the relation (e.g. followee))
	 * @param type
	 *            the type of the relation
	 * @return the UserRelation object after being saved to the database. If it
	 *         already existed, the existing entity will be retrieved and
	 *         returned.
	 */
	public UserRelation createUserRelation(User sourceUser, User targetUser, UserRelationTypes type) {
		Assert.notNull(sourceUser, "Source user cannot be null.");
		Assert.notNull(targetUser, "Target user cannot be null.");
		Assert.notNull(type, "Type cannot be null.");

		// Retrieve UserRelation or create if it doesn't exist in db
		return userRelationRepo.findOneBySourceAndTargetAndType(sourceUser,
				targetUser, type.name()).orElseGet(()->{
					UserRelation userRelation = new UserRelation();
					userRelation.setSource(sourceUser);
					userRelation.setTarget(targetUser);
					userRelation.setType(type.name());
					return userRelationRepo.save(userRelation);
					}
				);
	}

	/**
	 * This method is a convenience method to build a single real name from a
	 * first and a last name (each of which might be empty)
	 * 
	 * Sets the real name of the given user based on a given first and last
	 * name. Either first or last name (or both) may be empty - the real name is
	 * assembled accordingly. If a realname was already set before, no operation
	 * is performed. The name is not updated.
	 * 
	 * 
	 * @param user
	 *            the user to update
	 * @param firstName
	 *            the first name of the user
	 * @param lastName
	 *            the last name of the user
	 * @return the user with updated or unchanged realname
	 */
	public User setRealname(User user, String firstName, String lastName) {
		Assert.notNull(user, "User cannot be null.");

		if (firstName == null)
			firstName = "";
		if (lastName == null)
			lastName = "";

		if (user.getRealname() == null || user.getRealname().isEmpty()) {
			if (firstName.isEmpty()) {
				if (!lastName.isEmpty()) {
					user.setRealname(lastName);
				}
			} else {
				if (lastName.isEmpty()) {
					user.setRealname(firstName);
				} else {
					user.setRealname(firstName + " " + lastName);
				}
			}
			return save(user);
		}
		return user;
	}

	/**
	 * Calls the save method of the user repository, saves the provided User
	 * entity and returns it after the save process
	 * 
	 * @param user
	 *            the user entity to save
	 * @return the user entity after the save process
	 */
	public User save(User user) {
		Assert.notNull(user, "User cannot be null.");

		return userRepo.save(user);
	}

	/**
	 * Removes user from all their discourses and then deletes it by calling the
	 * delete method in the user repository.
	 * 
	 * @param user
	 *            the user entity to delete
	 */
	public void delete(User user) {
		Assert.notNull(user, "User cannot be null.");

		for (Discourse d : user.getDiscourses()) {
			user.removeDiscourse(d);
		}
		userRepo.delete(user);
	}

	/**
	 * Retrieves all users with the given user name. 
	 * Usernames are unique within a discourse, but there might be multiple users with the same username across discourses.
	 * 
	 * @param username
	 *            the username of the users to retrieve.

	 * 
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
	public List<User> findUserByUsername(String username) {
		Assert.hasText(username, "Username cannot be empty.");
		return userRepo.findAllByUsername(username);
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public Set<User> findUsersUnderDiscoursePart(DiscoursePart dp) {
		Set<DiscoursePart> seed = new HashSet<DiscoursePart>();
		seed.add(dp);
		Set<DiscoursePart> dps = discoursePartService.findDescendentClosure(seed, Optional.empty());
		Set<User> results = new HashSet<User>();
		for (DiscoursePart dp2: dps) {
			for (DiscoursePartContribution dpc: dp2.getDiscoursePartContributions()) {
				results.add(dpc.getContribution().getCurrentRevision().getAuthor());
			}
		}
		return results;
	}

	/**
	 * Get the set of all usernames that do NOT contain a particular annotation
	 * type
	 * 
	 * TODO NOTE OF: This should be implemented as a QueryDSL query that directly retrieves
	 * 		the desired users. Retrieving all users an then narrowing down might cause
	 * 		performance issues   ((Fixed??))
	 * 
	 * @param badAnnotation
	 * @return a set of user names
	 */
	@Transactional(propagation = Propagation.REQUIRED, readOnly = true)
    public List<User> findUsersWithoutAnnotation(String badAnnotation) {
    	Assert.hasText(badAnnotation);
    	return userRepo.findAllWithoutAnnotation(badAnnotation);
    	/*
        Set<User> unannotated = new HashSet<User>();
        for(User user : userRepo.findAll()) {
                boolean addme = true;
                AnnotationEntityProxy ag = user.getAnnotations();
                if (ag != null) {
                        Set<AnnotationInstance> sai = ag.getAnnotations();
                        if (sai != null) {
                                for (AnnotationInstance ai : sai) {
                                        if (ai.getType() == badAnnotation) { addme = false; break; }
                                }
                        }
                }
                if (addme) { unannotated.add(user); }
        }
        return unannotated;
        */
    }


    @Transactional(propagation= Propagation.REQUIRED, readOnly=true)
    public Optional<User> findOne(Long id) {
            return userRepo.findOne(id);
    }

}
