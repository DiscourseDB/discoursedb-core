package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.List;

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
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<Content> findAll(List<Long> ids){
		return contentRepo.findByIdIn(ids);
	}
	
	public Content save(Content content){
		return contentRepo.save(content);
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Content findOne(Long id){
		return contentRepo.findOne(id);
	}
	
	public void setNextRevision(Long id, Long nextRevId){
		contentRepo.setNextRevisionId(id, nextRevId);
	}
	
	public void setPreviousRevision(Long id, Long previousRevId){
		contentRepo.setPreviousRevisionId(id, previousRevId);
	}


}