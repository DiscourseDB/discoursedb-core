package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscoursePartRepository extends BaseRepository<DiscoursePart,Long>{
    
	Optional<DiscoursePart> findOneByName(String name);
	
	List<DiscoursePart> findAllByName(String name);
	List<DiscoursePart> findAllByType(String type);
}
