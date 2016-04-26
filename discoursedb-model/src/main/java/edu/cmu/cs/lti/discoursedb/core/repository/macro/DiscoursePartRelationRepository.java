package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.repository.BaseRepository;

public interface DiscoursePartRelationRepository extends BaseRepository<DiscoursePartRelation, Long> {
	Optional<DiscoursePartRelation> findOneBySourceAndTargetAndType(DiscoursePart source, DiscoursePart Target, String type);
	List<DiscoursePartRelation> findAllBySourceAndType(DiscoursePart source, String type);
	List<DiscoursePartRelation> findAllBySource(DiscoursePart source, Pageable page);
	Page<DiscoursePartRelation> findAllByTarget(DiscoursePart discoursePart, Pageable page);

}
