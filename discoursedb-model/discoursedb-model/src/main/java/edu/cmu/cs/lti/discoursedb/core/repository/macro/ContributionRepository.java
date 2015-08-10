package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.Optional;
import java.util.Set;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface ContributionRepository extends CoreBaseRepository<Contribution,Long>{
    
	public Optional<Contribution> findOneBySourceId(String id);
	public Set<Contribution> findBySourceId(String id);

}
