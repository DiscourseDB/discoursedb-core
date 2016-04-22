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

/**
 * 
 * @author Oliver Ferschke
 */
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
	
	private final String SEPARATOR_PREFIX = "[*** ";
	private final String SEPARATOR_SUFFIX = " ***]";	
	
	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length >=2 && args.length<=3, "USAGE: BratDiscourseExport <DiscourseName> <outputFolder> <Split on which DiscoursePart type (default: THREAD)>");
		SpringApplication.run(BratThreadExport.class, args);
	}

	@Override
	@Transactional //TODO -> move actual transactions into a service class to avoid overly large transactions 
	public void run(String... args) throws Exception {
		String discourseName = args[0];
		String outputFolder = args[1];
		
		String dpType = "THREAD";
		if(args.length==3){
			dpType=args[2].toUpperCase();
		}

		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(() -> new EntityNotFoundException("Discourse with name " + discourseName + " does not exist."));
		
		for(DiscoursePart dp: discoursePartService.findAllByDiscourseAndType(discourse, DiscoursePartTypes.valueOf(dpType))){
			
			// retrieve all contributions for the given discourse
			List<String> contributions = new ArrayList<>();
			List<BratAnnotation> annos = new ArrayList<>();
			int spanOffset = 0;
			for (Contribution contrib : contribService.findAllByDiscoursePart(dp)) {			
				
				String text = contrib.getCurrentRevision().getText();
				String authorName = contrib.getCurrentRevision().getAuthor()!=null?contrib.getCurrentRevision().getAuthor().getUsername():"Unknown Author";
				String tableName = contrib.getClass().getAnnotation(Table.class).name();
				String contribSeparatorContent = tableName+"_"+contrib.getId()+"_"+authorName;

				String contribSeparator = SEPARATOR_PREFIX+contribSeparatorContent+SEPARATOR_SUFFIX;
				
				contributions.add(contribSeparator);
				contributions.add(text);
										
				for (AnnotationInstance anno : annoService.findAnnotations(contrib)) {
					annos.addAll(convertAnnotation(anno, spanOffset, contribSeparator, text));					
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

	private List<BratAnnotation> convertAnnotation(AnnotationInstance dbAnno, int spanOffset, String separator, String text) {
		//one DiscourseDB annotation could result in multiple BRAT annotations 
		List<BratAnnotation> newAnnotations = new ArrayList<>();

		if (dbAnno.getType() != null&&dbAnno.getType().equals(BratAnnotationType.R.name())) {
			//specifically handle DiscourseDB annotation that have the Brat Type R
			//to produce the corresponding BRAT annotation
			//TODO implement
		}
		else if (dbAnno.getType() != null&&dbAnno.getType().equals(BratAnnotationType.E.name())) {
			//specifically handle DiscourseDB annotation that have the Brat Type E			
			//Syto produce the corresponding BRAT annotation
			//TODO implement
		}
		else if (dbAnno.getType() != null&&dbAnno.getType().equals(BratAnnotationType.M.name())) {
			//specifically handle DiscourseDB annotation that have the Brat Type M
			//to produce the corresponding BRAT annotation
			//TODO implement
		}
		else if (dbAnno.getType() != null&&dbAnno.getType().equals(BratAnnotationType.N.name())) {
			//specifically handle DiscourseDB annotation that have the Brat Type N			
			//to produce the corresponding BRAT annotation
			//TODO implement
		}
		else {
			//PRODUCE Text-Bound Annotation for all other annotations		
			BratAnnotation textBoundAnnotation = new BratAnnotation();
			textBoundAnnotation.setType(BratAnnotationType.T);			
			textBoundAnnotation.setId(dbAnno.getId());
			textBoundAnnotation.setAnnotationLabel(dbAnno.getType());

			//CALC OFFSET			
			boolean contributionLabel = false; //indicates whether the contribution is labeled as a whole rather than a span of text
			if (dbAnno.getEndOffset() == 0) {
				textBoundAnnotation.setBeginIndex(spanOffset);
				textBoundAnnotation.setEndIndex(spanOffset+separator.length());
				contributionLabel = true;
				
			} else {
				textBoundAnnotation.setBeginIndex(spanOffset+dbAnno.getBeginOffset()+separator.length()-1);
				textBoundAnnotation.setEndIndex(spanOffset+dbAnno.getEndOffset()+separator.length()-1);
			}
			
			if(contributionLabel){
				textBoundAnnotation.setCoveredText(separator);											
			}else{
				textBoundAnnotation.setCoveredText(text.substring(dbAnno.getBeginOffset(),dbAnno.getEndOffset()));											
			}

			newAnnotations.add(textBoundAnnotation);
			
			//FEATURE VALUES ARE USED TO CREATE BRAT ANNOTATION ATTRIBUTES
			//Feature types are ignores.
			//
			for(Feature f:dbAnno.getFeatures()){			
				BratAnnotation newAttribute = new BratAnnotation();
				newAttribute.setType(BratAnnotationType.A);			
				newAttribute.setId(f.getId());
				newAttribute.setAnnotationLabel(f.getValue());
				newAttribute.setSourceAnnotationId(textBoundAnnotation.getFullAnnotationId());
				newAnnotations.add(newAttribute);
			}
    	}

		

		return newAnnotations;
	}

}
