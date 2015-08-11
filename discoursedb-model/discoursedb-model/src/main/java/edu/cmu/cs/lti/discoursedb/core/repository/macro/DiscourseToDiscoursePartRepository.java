package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseToDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface DiscourseToDiscoursePartRepository extends CoreBaseRepository<DiscourseToDiscoursePart,Long>{
    
	Optional<DiscourseToDiscoursePart> findOneByDiscourseAndDiscoursePart(Discourse discourse, DiscoursePart discoursePart);

}
