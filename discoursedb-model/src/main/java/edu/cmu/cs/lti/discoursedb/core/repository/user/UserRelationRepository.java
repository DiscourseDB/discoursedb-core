package edu.cmu.cs.lti.discoursedb.core.repository.user;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.model.user.UserRelation;
import edu.cmu.cs.lti.discoursedb.core.model.user.UserRelationType;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface UserRelationRepository extends CoreBaseRepository<UserRelation,Long>{
	Optional<UserRelation> findOneBySourceAndTargetAndType(User source, User target, UserRelationType type);	

}
