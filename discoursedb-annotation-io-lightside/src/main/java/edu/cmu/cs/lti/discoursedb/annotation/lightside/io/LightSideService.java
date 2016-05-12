package edu.cmu.cs.lti.discoursedb.annotation.lightside.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.annotation.lightside.model.LightSideData;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
public class LightSideService {

	private final @NonNull DiscourseService discourseService;
	private final @NonNull ContributionService contribService;
	private final @NonNull ContentService contentService;
	private final @NonNull AnnotationService annoService;
	private final @NonNull DiscoursePartService dpService;
	
	
	@Transactional(readOnly=true)
	public void exportAnnotations(String discourseName, DiscoursePartTypes dptype, File outputFolder){
		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(()->new EntityNotFoundException("Discourse with name "+discourseName+" does not exist."));
		exportAnnotations(discourse, dptype, outputFolder);	
	}
	
	@Transactional(readOnly=true)
	public void exportAnnotations(Discourse discourse, DiscoursePartTypes dptype, File outputFolder){
		log.info("Processing discourse "+discourse.getName()+". Extracting DiscourseParts of type "+dptype.name());
		exportAnnotations(dpService.findAllByDiscourseAndType(discourse, dptype), outputFolder);	
	}
	
		
	@Transactional(readOnly=true)
	public void exportAnnotations(Iterable<DiscoursePart> discourseParts, File outputFolder){
		List<LightSideData> data = StreamSupport.stream(discourseParts.spliterator(), false)
				.flatMap(dp -> extractAnnotations(dp).stream()).collect(Collectors.toList());		
		
		try{
			FileUtils.writeLines(new File(outputFolder,"lightside.csv"), data);
			//TODO: we might want two write two separate files - one based on contribution labels and one based on span annotations
		}catch(IOException e){
			log.error("Error writing LightSide file to disk. Skipping...",e);
		}

	}
	
	@Transactional(readOnly=true)
	public List<LightSideData> extractAnnotations(DiscoursePart dp){
		log.info("Processing DiscoursePart "+dp.getName());
		return extractAnnotations(contribService.findAllByDiscoursePart(dp));
	}
	
	
	@Transactional(readOnly=true)
	public List<LightSideData> extractAnnotations(Iterable<Contribution> contribs){
		//if this is very slow, we could implement a native query for this
		
		List<LightSideData> outputList = new ArrayList<>();

		for(Contribution contrib: contribs){
			LightSideData newData = new LightSideData();

			for(AnnotationInstance contribAnno: annoService.findAnnotations(contrib)){
				//TODO process contrib annotation
			}


			/*
			 * Content-annotations not yet supported 	
			 */
//			Content curRevision = contrib.getCurrentRevision();
//			for(AnnotationInstance contentAnno: annoService.findAnnotations(curRevision)){
//			}
			
			//TODO populate data object
			
			outputList.add(newData);
		}		
		return outputList;
	}
	
}
