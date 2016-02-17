package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface DiscoursePartRelationRepository extends CoreBaseRepository<DiscoursePartRelation, Long> {
	Optional<DiscoursePartRelation> findOneBySourceAndTargetAndType(DiscoursePart source, DiscoursePart Target, String type);
	List<DiscoursePartRelation> findAllBySourceAndType(DiscoursePart source, String type);

}
