package edu.cmu.cs.lti.discoursedb.core.repository.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface UserRepository extends CoreBaseRepository<User,Long>{    
    public Optional<User> findById(@Param("id")Long id);    	
	
    public Long countByRealname(@Param("realname")String realname);
	
	@RestResource(exported = false)
	public List<User> findAllByUsername(@Param("username")String username);
	
    public Page<User> findAllByUsername(@Param("username")String username, Pageable pageable);    

}
