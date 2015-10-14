package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelationType;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface DiscourseRelationRepository extends CoreBaseRepository<DiscourseRelation,Long>{

	Optional<DiscourseRelation> findOneBySourceAndTargetAndType(Contribution source, Contribution Target, DiscourseRelationType type);	
   
}
