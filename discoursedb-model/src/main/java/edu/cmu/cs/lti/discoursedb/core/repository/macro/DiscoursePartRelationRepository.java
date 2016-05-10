package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscoursePartRelationRepository extends BaseRepository<DiscoursePartRelation, Long> {
	Optional<DiscoursePartRelation> findOneBySourceAndTargetAndType(DiscoursePart source, DiscoursePart Target, String type);
	List<DiscoursePartRelation> findAllBySourceAndType(DiscoursePart source, String type);
	Page<DiscoursePartRelation> findAllBySource(DiscoursePart source, Pageable page);
	
	@Query("select targ from DiscoursePartRelation dpr left join dpr.target targ left join dpr.source src where src=:source order by targ.startTime")
	Page<DiscoursePart> findAllTargetsBySource(@Param("source") DiscoursePart source, Pageable page);
	
	List<DiscoursePartRelation> findAllBySource(DiscoursePart source);
	Page<DiscoursePartRelation> findAllByTarget(DiscoursePart discoursePart, Pageable page);	


}
