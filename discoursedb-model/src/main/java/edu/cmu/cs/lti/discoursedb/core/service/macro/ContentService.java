package edu.cmu.cs.lti.discoursedb.core.service.macro;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class ContentService {

	private final @NonNull ContentRepository contentRepo;
	
	public Content createContent(){
		return contentRepo.save(new Content());
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public List<Content> findAll(List<Long> ids){
		Assert.notNull(ids, "List of content ids cannot be null.");
		Assert.notEmpty(ids, "List of content ids cannot be empty.");
		return contentRepo.findByIdIn(ids);
	}
	
	public Content save(Content content){
		Assert.notNull(content, "Content cannot be null.");
		return contentRepo.save(content);
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Content findOne(Long id){
		Assert.notNull(id, "Content id cannot be null.");
		Assert.isTrue(id>0, "Content id has to be a positive number.");
		return contentRepo.findOne(id);
	}
	
	public void setNextRevision(Long id, Long nextRevId){
		Assert.notNull(id, "Content id cannot be null.");
		Assert.isTrue(id>0, "Content id has to be a positive number.");
		Assert.notNull(nextRevId, "Next revision id cannot be null.");
		Assert.isTrue(nextRevId>0, "Next revision id has to be a positive number.");
		Assert.isTrue(id!=nextRevId, "Next revision cannot equal the current revision.");		
		contentRepo.setNextRevisionId(id, nextRevId);
	}
	
	public void setPreviousRevision(Long id, Long previousRevId){
		Assert.notNull(id, "Content id cannot be null.");
		Assert.isTrue(id>0, "Content id has to be a positive number.");
		Assert.notNull(previousRevId, "Previous revision id cannot be null.");
		Assert.isTrue(previousRevId>0, "Previous revision id has to be a positive number.");
		Assert.isTrue(id!=previousRevId, "Previous revision cannot equal the current revision.");		
		contentRepo.setPreviousRevisionId(id, previousRevId);
	}


}