package edu.cmu.cs.lti.discoursedb.core.service.macro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContentRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.ContributionInteractionRepository;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class ContentService {

	@SuppressWarnings("unused")
	@Autowired
	private ContentRepository contentRepo;
	
	@SuppressWarnings("unused")
	@Autowired
	private ContributionInteractionRepository contributionInteractionRepo;

	
	

}