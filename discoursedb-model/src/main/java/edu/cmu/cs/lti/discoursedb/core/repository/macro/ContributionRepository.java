package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionType;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface ContributionRepository extends CoreBaseRepository<Contribution,Long>{
	List<Contribution> findAllByType(ContributionType type);
	
}
