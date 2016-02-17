package edu.cmu.cs.lti.discoursedb.core.repository.user;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.user.ContributionInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface ContributionInteractionRepository extends CoreBaseRepository<ContributionInteraction,Long>{
	Optional<ContributionInteraction> findOneByUserAndContributionAndType(User user, Contribution contribution, String type);	
    
}
