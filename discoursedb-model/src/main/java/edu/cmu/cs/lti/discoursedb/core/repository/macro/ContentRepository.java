package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface ContentRepository extends CoreBaseRepository<Content,Long>{
	public List<Content> findByIdIn(List<Long> contentIdList);
	
	@Modifying
	@Query(value="update content c set c.fk_next_revision = ?2 where c.id_content = ?1",nativeQuery=true)
	public void setNextRevisionId(Long id, Long nextRevId);

	@Modifying
	@Query(value="update content c set c.fk_previous_revision = ?2 where c.id_content = ?1",nativeQuery=true)
	public void setPreviousRevisionId(Long id, Long previousRevId);
    
}
