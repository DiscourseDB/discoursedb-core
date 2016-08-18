package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscoursePartRepository extends BaseRepository<DiscoursePart,Long>{
    
	Optional<DiscoursePart> findOneByName(String name);
	
	List<DiscoursePart> findAllByName(String name);
	List<DiscoursePart> findAllByType(String type);

    @Query("select dp from DiscoursePart dp left join dp.dataSourceAggregate dsa left join dsa.sources dsi where dsi.entitySourceId=:id")
    Optional<DiscoursePart> findOneByDataSourceId(@Param("id") String id);

}
