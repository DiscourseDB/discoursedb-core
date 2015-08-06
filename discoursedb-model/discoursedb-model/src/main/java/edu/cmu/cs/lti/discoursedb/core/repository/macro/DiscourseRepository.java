package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface DiscourseRepository extends CoreBaseRepository<Discourse,Long>{
	
	Discourse findOneByNameAndDescriptor(String name, String descriptor);
	
	List<Discourse> findByName(String name);

	
    
}
