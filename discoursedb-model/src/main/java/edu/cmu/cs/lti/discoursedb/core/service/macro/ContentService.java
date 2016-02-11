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
		Assert.notNull(ids);
		Assert.notEmpty(ids);
		return contentRepo.findByIdIn(ids);
	}
	
	public Content save(Content content){
		return contentRepo.save(content);
	}
	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public Content findOne(Long id){
		Assert.notNull(id);
		Assert.isTrue(id>0);
		return contentRepo.findOne(id);
	}
	
	public void setNextRevision(Long id, Long nextRevId){
		Assert.notNull(id);
		Assert.isTrue(id>0);
		Assert.notNull(nextRevId);
		Assert.isTrue(nextRevId>0);
		Assert.isTrue(id!=nextRevId);		
		contentRepo.setNextRevisionId(id, nextRevId);
	}
	
	public void setPreviousRevision(Long id, Long previousRevId){
		Assert.notNull(id);
		Assert.isTrue(id>0);
		Assert.notNull(previousRevId);
		Assert.isTrue(previousRevId>0);
		Assert.isTrue(id!=previousRevId);		
		contentRepo.setPreviousRevisionId(id, previousRevId);
	}


}