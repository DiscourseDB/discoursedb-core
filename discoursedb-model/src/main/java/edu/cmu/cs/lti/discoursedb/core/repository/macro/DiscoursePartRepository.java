package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscoursePartRepository extends BaseRepository<DiscoursePart,Long>{
    
	Optional<DiscoursePart> findOneByName(String name);
	
	List<DiscoursePart> findAllByName(String name);
	List<DiscoursePart> findAllByType(String type);

	@Query("select dp from DiscoursePart dp left join fetch dp.annotations aa "
			+ "left join fetch aa.annotations ai left join fetch ai.features feat "
			+ " where dp.type=:discoursePartType")
	List<DiscoursePart> findExtendedByType(@Param("discoursePartType") String discoursePartType);
	
	
	@Query("select dp from DiscoursePart dp where "
			+ "((select count(ai) from dp.annotations aa join aa.annotations ai where ai.type like 'Degenerate') = 0)"
			+ " and type=:discoursePartType")
	Page<DiscoursePart> findAllNonDegenerateByType(@Param("discoursePartType") String discoursePartType, Pageable pageable);
	
	@Query("select dp from DiscoursePart dp left join dp.dataSourceAggregate dsa left join dsa.sources dsi where dsi.entitySourceId=:id")
	Optional<DiscoursePart> findOneByDataSourceId(@Param("id") String id);
		
	
	@Query(value = "select * from discourse_part dp where fk_annotation not in " +
				      "(select fk_annotation from annotation_instance where type=:annotationType)",
		nativeQuery=true)
	List<DiscoursePart> findAllNotAnnotatedWithType(@Param("annotationType") String type);
	
	@Query("select dp from DiscoursePart dp where "
			+ "((select count(ai) from dp.annotations aa join aa.annotations ai where ai.type like :annotationType) = 0)")
	Page<DiscoursePart> findAllNotAnnotatedWithTypePaged(@Param("annotationType") String type, Pageable pageable);

	@Query("select type, count(*) as count from DiscoursePart dp group by type")
	List<Object[]> countsByType();
}
