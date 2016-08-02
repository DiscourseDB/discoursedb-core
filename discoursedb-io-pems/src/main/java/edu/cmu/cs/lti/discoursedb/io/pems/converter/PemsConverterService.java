package edu.cmu.cs.lti.discoursedb.io.pems.converter;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * Service for mapping data retrieved from the Twitter4j API to DiscourseDB
 * 
 * @author Oliver Ferschke
 *
 */
@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PemsConverterService {

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull AnnotationService annoService;
	

	public void mapDocument(String discourseName, String datasetName, Document d){
		log.warn("Not implemented yet.");	
	}
}
