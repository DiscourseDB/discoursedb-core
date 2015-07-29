package edu.cmu.cs.lti.discoursedb.core.repository.user;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;

public interface UserRepository extends CrudRepository<User,Long>{

	List<User> findByRealname(String realname);
	
}
