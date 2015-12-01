package edu.cmu.cs.lti.discoursedb.core.repository.macro;

import java.util.List;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.repository.CoreBaseRepository;

public interface ContentRepository extends CoreBaseRepository<Content,Long>{
	public List<Content> findByIdIn(List<Long> contentIdList);
    
}
