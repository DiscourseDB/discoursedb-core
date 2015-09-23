package edu.cmu.cs.lti.discoursedb.core.repository.user;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface UserRepository extends CoreBaseRepository<User,Long>{
    
	public Optional<User> findById(Long id);    
	
	public long countByRealname(String realname);
	
	public Optional<User> findBySourceId(String id);
	public Optional<User> findBySourceIdAndUsername(String sourceId, String username);
    public List<User> findAllByUsername(String username);
    public List<User> findAllBySourceId(String sourceId);
}
