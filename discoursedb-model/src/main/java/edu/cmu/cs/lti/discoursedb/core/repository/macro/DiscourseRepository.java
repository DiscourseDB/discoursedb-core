package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscourseRepository extends BaseRepository<Discourse,Long> {
	
	Optional<Discourse> findOneByName(String name);

	
    
}
