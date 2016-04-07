package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.web.bind.annotation.RequestParam;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscoursePartRepository extends BaseRepository<DiscoursePart,Long>{
    
	Optional<DiscoursePart> findOneByName(@Param("name") String name);
	
	List<DiscoursePart> findAllByName(@Param("name") String name);
	List<DiscoursePart> findAllByType(@Param("discoursePartType") String type);
	
	@Query("select dp from DiscoursePart dp where "
			+ "((select count(ai) from dp.annotations aa join aa.annotations ai where ai.type like 'Degenerate') = 0)"
			+ " and type=:discoursePartType")
	Page<DiscoursePart> findAllNonDegenerateByType(@Param("discoursePartType") String type, Pageable pageable);
	
	@Query(value = "select * from discourse_part dp where fk_annotation not in " +
				      "(select fk_annotation from annotation_instance where type=:annotationType)",
		nativeQuery=true)
	List<DiscoursePart> findAllNotAnnotatedWithType(@Param("annotationType") String type);
	
	@Query("select dp from DiscoursePart dp where "
			+ "((select count(ai) from dp.annotations aa join aa.annotations ai where ai.type like :annotationType) = 0)")
	Page<DiscoursePart> findAllNotAnnotatedWithTypePaged(@Param("annotationType") String type, Pageable pageable);
	
	@Query("select new edu.cmu.cs.lti.discoursedb.core.repository.macro.AnnotatedDiscoursePart(dp,ai,feat)  from DiscoursePart dp join dp.annotations as aa "
			+ "join aa.annotations as ai "
			+ "join ai.features as feat "
			+ "where dp.type like :include_discoursePartType "
			+ "and ai.type like :include_annotationType "
			+ "and ((select count(ai2) from aa.annotations ai2 where ai2.type like 'Degenerate') = 0)")
	Page<AnnotatedDiscoursePart> findDiscoursePartsEnhanced(@Param("include_annotationType") String include_annotationType, 
			@Param("include_discoursePartType") String include_discoursePartType,
			Pageable pageable);

}
