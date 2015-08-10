package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface ContributionRepository extends CoreBaseRepository<Contribution,Long>{
    
	public Optional<Contribution> findBySourceId(String id);

}
