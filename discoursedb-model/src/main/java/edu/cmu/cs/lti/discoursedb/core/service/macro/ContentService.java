package edu.cmu.cs.lti.discoursedb.core.service.macro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class ContentService {

	@Autowired
	private ContentRepository contentRepo;
	
	public Content createContent(){
		return contentRepo.save(new Content());
	}
	

}