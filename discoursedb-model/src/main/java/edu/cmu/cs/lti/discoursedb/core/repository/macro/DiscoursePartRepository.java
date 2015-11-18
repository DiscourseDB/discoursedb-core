package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartType;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface DiscoursePartRepository extends CoreBaseRepository<DiscoursePart,Long>{
    
	Optional<DiscoursePart> findOneByName(String name);
	
	List<DiscoursePart> findAllByName(String name);
	List<DiscoursePart> findAllByType(DiscoursePartType type);
}
