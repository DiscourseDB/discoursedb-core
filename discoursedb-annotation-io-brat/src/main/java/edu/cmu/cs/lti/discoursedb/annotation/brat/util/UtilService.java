package edu.cmu.cs.lti.discoursedb.annotation.brat.util;

import java.io.IOException;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class UtilService {

	private final @NonNull DiscourseService discourseService;
	private final @NonNull ContributionService contribService;
	private final @NonNull ContentService contentService;
	private final @NonNull AnnotationService annoService;
	
	
	/**
	 * Imports the annotations of all brat-annotated documents located in the provided folder.  
	 * 
	 * @param inputFolder the path to the brat corpus folder to import 
	 * @throws IOException if an Exception occurs accessing the folder
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	public void removeUnannotatedContribs(String[] discourseNames) throws EntityNotFoundException{
		Assert.notEmpty(discourseNames, "At least one discourse name needs to be defined.");

		for(String discourseName: discourseNames){
			removeUnannotatedContribs(discourseName);
		}
	}

	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	public void removeUnannotatedContribs(String discourseName) throws EntityNotFoundException{
		Assert.notNull(discourseName, "The discourseName cannot be null");
		Assert.hasText(discourseName, "The discourseName cannot be empty");
		
		Discourse curDiscourse = discourseService.findOne(discourseName).orElseThrow(()->new EntityNotFoundException());
		for(Contribution curContrib: contribService.findAllByDiscourse(curDiscourse)){
			if(annoService.findAnnotations(curContrib).isEmpty()){

				
				/*
				 * Without invalidating the references to related entities, we cannot delete.
				 * This should not be the case - FIX CASCADE TYPES 
				 */
				for(DiscoursePartContribution dpc:curContrib.getContributionPartOfDiscourseParts()){
					dpc.setContribution(null);
					dpc.setDiscoursePart(null);
				}
				for(DiscourseRelation sdr:curContrib.getSourceOfDiscourseRelations()){
					sdr.setSource(null);
					sdr.setTarget(null);
				}
				for(DiscourseRelation tdr:curContrib.getTargetOfDiscourseRelations()){
					tdr.setSource(null);
					tdr.setTarget(null);
				}
				/*
				 *  ------------------
				 */

				contribService.delete(curContrib);
			}
		}
		
	}	
	
}
