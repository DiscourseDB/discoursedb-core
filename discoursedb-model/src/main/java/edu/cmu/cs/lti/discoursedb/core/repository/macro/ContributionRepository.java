package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;

import org.springframework.data.jpa.repository.Query;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface ContributionRepository extends BaseRepository<Contribution,Long>{
	List<Contribution> findAllByType(String type);

	@Query("select type, count(*) as count from Contribution c group by type")
	List<Object[]> countsByType(); 

}
