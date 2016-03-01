package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.ContributionContext;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface ContributionContextRepository extends BaseRepository<ContributionContext,Long>{
	
	Optional<ContributionContext> findOneByContributionAndContextContent(Contribution contribution, Content contextContent);
	Optional<ContributionContext> findOneByContributionAndContextContribution(Contribution contribution, Contribution contextContribution);
	List<ContributionContext> findByContextContribution(Contribution contextContribution);
	List<ContributionContext> findByContextContent(Content contextContent);
    
}
