package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface DiscourseRepository extends CoreBaseRepository<Discourse,Long> {
	
	Optional<Discourse> findOneByNameAndDescriptor(String name, String descriptor);
	
	List<Discourse> findAllByName(String name);

	
    
}
