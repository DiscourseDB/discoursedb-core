package edu.cmu.cs.lti.discoursedb.core.repository.user;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.model.user.UserRelation;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface UserRelationRepository extends BaseRepository<UserRelation,Long>{
	Optional<UserRelation> findOneBySourceAndTargetAndType(User source, User target, String type);	

}
