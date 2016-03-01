package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseToDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscourseToDiscoursePartRepository extends BaseRepository<DiscourseToDiscoursePart,Long>{
    
	Optional<DiscourseToDiscoursePart> findOneByDiscourseAndDiscoursePart(Discourse discourse, DiscoursePart discoursePart);
	List<DiscourseToDiscoursePart> findByDiscourse(Discourse discourse);
	

}
