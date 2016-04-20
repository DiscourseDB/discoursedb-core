package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Table;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratAnnotation;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratAnnotationType;
import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import lombok.extern.log4j.Log4j;

/**
 * 
 * @author Oliver Ferschke
 */
@Log4j
@Component
@SpringBootApplication
@ComponentScan(basePackages = { "edu.cmu.cs.lti.discoursedb.configuration" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratThreadExport.class,
						BaseConfiguration.class }) })
public class BratThreadExport implements CommandLineRunner {

	@Autowired private DiscourseService discourseService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private ContributionService contribService;
	@Autowired private AnnotationService annoService;
	
	private final String SEPARATOR_PREFIX = "********* ";
	private final String SEPARATOR_SUFFIX = " *********";
	
	
	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length >=2 && args.length<=3, "USAGE: BratDiscourseExport <DiscourseName> <outputFolder> <mappingFile (optional)>");
		SpringApplication.run(BratThreadExport.class, args);
	}

	@Override
	@Transactional //TODO -> move actual transactions into a service class to avoid overly large transactions 
	public void run(String... args) throws Exception {
		String discourseName = args[0];
		String outputFolder = args[1];

		List<String> mappingLabels=new ArrayList<>();  
		if(args.length==3){
			mappingLabels = FileUtils.readLines(new File(args[2]));
		}

		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(() -> new EntityNotFoundException("Discourse with name " + discourseName + " does not exist."));
		
		for(DiscoursePart dp: discoursePartService.findAllByDiscourseAndType(discourse, DiscoursePartTypes.THREAD)){
			
			// retrieve all contributions for the given discourse
			List<String> contributions = new ArrayList<>();
			List<BratAnnotation> annos = new ArrayList<>();
			int spanOffset = 0;
			for (Contribution contrib : contribService.findAllByDiscoursePart(dp)) {			
				
				String text = contrib.getCurrentRevision().getText();			
				String contribSeparatorContent = contrib.getClass().getAnnotation(Table.class).name()+"_"+contrib.getId();
				String contribSeparator = SEPARATOR_PREFIX+contribSeparatorContent+SEPARATOR_SUFFIX;
				
				contributions.add(contribSeparator);
				contributions.add(text);
										
				for (AnnotationInstance anno : annoService.findAnnotations(contrib)) {
					BratAnnotation newAnno = convertAnnotation(anno, spanOffset, contribSeparator, text, mappingLabels);
					if(newAnno!=null){
						annos.add(newAnno);					
					}
				}

				//update span offset
				spanOffset+=text.length()+1;
				spanOffset+=contribSeparator.length()+1;
			}
			String dpprefix = dp.getClass().getAnnotation(Table.class).name();
			FileUtils.writeLines(new File(outputFolder,dpprefix + "_"+dp.getId()+".txt"),contributions);				
			FileUtils.writeLines(new File(outputFolder,dpprefix + "_"+dp.getId()+".ann"),annos);				
			
		}	
	}

	private BratAnnotation convertAnnotation(AnnotationInstance dbAnno, int spanOffset, String separator, String text, List<String> mappingLabels) {
		BratAnnotation anno = new BratAnnotation();
		anno.setId(dbAnno.getId());
		
		int numFeatures = dbAnno.getFeatures()==null?0:dbAnno.getFeatures().size();

		//MAP OFFSET
		
		boolean contributionLabel = false; //indicates whether the contribution is labeled as a whole rather than a span of text
		if (dbAnno.getEndOffset() == 0) {
			anno.setBeginIndex(spanOffset);
			anno.setEndIndex(spanOffset+separator.length());
			contributionLabel = true;
			
		} else {
			anno.setBeginIndex(spanOffset+dbAnno.getBeginOffset()+separator.length()-1);
			anno.setEndIndex(spanOffset+dbAnno.getEndOffset()+separator.length()-1);
		}

		
		//MAP ANNOTATION TYPE TO BRAT TYPE
		
		if (dbAnno.getType() != null) {
			// TEXT BOUND ANNOTATION
			if (dbAnno.getType().equals(BratAnnotationType.T.name())) {
				anno.setType(BratAnnotationType.T);
			}
			
			/*
			 * TODO HANDLERS FOR OTHER TYPES GO HERE
			 */

			// MAPPED ANNOTATION
			else if(mappingLabels.contains(dbAnno.getType())){
				//map types defined in mapping to Brat T type
				anno.setType(BratAnnotationType.T);			
			}
			
			// UNSUPPORTED ANNOTATION
			else{
				log.warn("Unsupported annotation type: "+dbAnno.getType()+". Skipping.");
				return null;
			}
		}

		//MAP ANNOTATION FEATURE TYPES TO BRAT ANNOTATION CATEGORY

		//we have exactly one feature
		if(numFeatures==1){
			for(Feature f:dbAnno.getFeatures()){
				anno.setAnnotationCategory(f.getType());
				if(contributionLabel){
					anno.setAnnotationValue(separator);											
				}else{
					anno.setAnnotationValue(text.substring(dbAnno.getBeginOffset(),dbAnno.getEndOffset()));											
				}
			}
		}
		//we have more than one feature (currently unsupported)
		else if(numFeatures>1){
			log.warn("Multiple features are currently not supported");
			return null;
		}
		//we have no feature and a mapped type	
		else if(mappingLabels.contains(dbAnno.getType())){
			anno.setAnnotationCategory(dbAnno.getType());					
			if(contributionLabel){
				anno.setAnnotationValue(separator);											
			}else{
				anno.setAnnotationValue(text.substring(dbAnno.getBeginOffset(),dbAnno.getEndOffset()));											
			}
		}
		
		//we have no feature and a regular type	
		else{
			log.warn("Feature missing.");
			return null;			
		}

		return anno;
	}

}
