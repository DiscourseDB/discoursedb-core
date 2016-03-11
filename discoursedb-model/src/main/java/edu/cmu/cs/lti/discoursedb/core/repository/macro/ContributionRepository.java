package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface ContributionRepository extends BaseRepository<Contribution,Long>{
	List<Contribution> findAllByType(String type);
	
}
