package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Context;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionContext;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface ContributionContextRepository extends CoreBaseRepository<ContributionContext,Long>{
	Optional<ContributionContext> findOneByContributionAndContext(Contribution contribution, Context context);
	List<ContributionContext> findByContext(Context context);

    
}
