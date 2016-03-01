package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscourseRelationRepository extends BaseRepository<DiscourseRelation,Long>{

	Optional<DiscourseRelation> findOneBySourceAndTargetAndType(Contribution source, Contribution Target, String type);	
   
}
