package edu.cmu.cs.lti.discoursedb.core.repository.user;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.user.DiscoursePartInteraction;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface DiscoursePartInteractionRepository extends CoreBaseRepository<DiscoursePartInteraction,Long>{
	Optional<DiscoursePartInteraction> findOneByUserAndDiscoursePartAndType(User user, DiscoursePart dp, String type);	
    
}
