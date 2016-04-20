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
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import lombok.extern.log4j.Log4j;

/**
 * 
 * @author Oliver Ferschke
 */
@Log4j
@Component
@SpringBootApplication
@ComponentScan(basePackages = { "edu.cmu.cs.lti.discoursedb.configuration",
		"edu.cmu.cs.lti.discoursedb.annotation.demo.io" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratContributionExport.class,
						BaseConfiguration.class }) })
public class BratContributionExport implements CommandLineRunner {

	@Autowired
	private DiscourseService discourseService;
	@Autowired
	private ContributionService contribService;
	@Autowired
	private AnnotationService annoService;
	
	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length >=2 && args.length<=3, "USAGE: BratContributionExport <DiscourseName> <outputFolder> <mappingFile (optional)>");
		SpringApplication.run(BratContributionExport.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		String discourseName = args[0];
		String outputFolder = args[1];

		List<String> mappingLabels=new ArrayList<>();  
		if(args.length==3){
			mappingLabels = FileUtils.readLines(new File(args[2]));
		}

		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(() -> new EntityNotFoundException("Discourse with name " + discourseName + " does not exist."));

		// retrieve all contributions for the given discourse
		for (Contribution contrib : contribService.findAllByDiscourse(discourse)) {

			String prefix = contrib.getClass().getAnnotation(Table.class).name();
			String text = contrib.getCurrentRevision().getText();
			// one text file for each contribution
			FileUtils.write(new File(outputFolder, prefix + "_" + contrib.getId() + ".txt"), text);

			List<BratAnnotation> annos = new ArrayList<>();
			for (AnnotationInstance anno : annoService.findAnnotations(contrib)) {
				BratAnnotation newAnno = convertAnnotation(anno, text, mappingLabels);
				if(newAnno!=null){
					annos.add(newAnno);					
				}
			}
			FileUtils.writeLines(new File(outputFolder,contrib.getClass().getAnnotation(Table.class).name() + "_" + contrib.getId() + ".ann"),annos);				
		}
	}

	private BratAnnotation convertAnnotation(AnnotationInstance dbAnno, String text, List<String> mappingLabels) {
		BratAnnotation anno = new BratAnnotation();
		anno.setId(dbAnno.getId());
		
		int numFeatures = dbAnno.getFeatures()==null?0:dbAnno.getFeatures().size();

		//MAP OFFSET
		
		if (dbAnno.getEndOffset() == 0) {
			anno.setBeginIndex(0);
			anno.setEndIndex(text.length()-1);
		} else {
			anno.setBeginIndex(dbAnno.getBeginOffset());
			anno.setEndIndex(dbAnno.getEndOffset());
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
				anno.setAnnotationValue(text.substring(anno.getBeginIndex(),anno.getEndIndex()));						
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
			anno.setAnnotationValue(text.substring(anno.getBeginIndex(),anno.getEndIndex()));						
		}
		
		//we have no feature and a regular type	
		else{
			log.warn("Feature missing.");
			return null;			
		}
		


		return anno;
	}

}
