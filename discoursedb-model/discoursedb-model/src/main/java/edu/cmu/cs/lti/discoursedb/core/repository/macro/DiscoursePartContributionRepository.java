package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface DiscoursePartContributionRepository extends CoreBaseRepository<DiscoursePartContribution,Long>{
	
	Optional<DiscoursePartContribution> findOneByContributionAndDiscoursePart(Contribution contribution, DiscoursePart discoursePart);
	List<DiscoursePartContribution> findByDiscoursePart(DiscoursePart discoursePart);
    
}
