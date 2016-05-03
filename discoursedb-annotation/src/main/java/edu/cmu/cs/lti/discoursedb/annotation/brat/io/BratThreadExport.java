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
import org.springframework.hateoas.Identifiable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratAnnotation;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratAnnotationType;
import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.BaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
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
 * Compile one text file per DiscoursPart (default type: THREAD) with the concatenated texts of the "currentRevision" Content entities separated
 * by a String which holds meta information about the contribution (table, id, autor name)
 * 
 * Currently, only the latest revision of a contribution is supported.
 * It is assumed that annotations on contirbutions are not spans, but labels on the whole entities
 * while annotations on the content of that contributions are always spans and not entity labels 
 * 
 * @author Oliver Ferschke
 */
@Log4j
@Component
@SpringBootApplication
@ComponentScan(basePackages = { "edu.cmu.cs.lti.discoursedb.configuration", "edu.cmu.cs.lti.discoursedb.annotation.brat.io" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratThreadExport.class, BaseConfiguration.class }) })
public class BratThreadExport implements CommandLineRunner {

	@Autowired private DiscourseService discourseService;
	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private ContributionService contribService;
	@Autowired private AnnotationService annoService;
	
	public static final String CONTRIB_SEPARATOR = "[**** NEW CONTRIBUTION ****]";
	public enum EntityTypes{CONTRIBUTION, CONTENT};
	public enum AnnotationSourceType{ANNOTATION, FEATURE};
	
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
			
			List<String> contribExportText = new ArrayList<>();
			List<BratAnnotation> annos = new ArrayList<>();
			
			
			/*
			 * The offset mapping keeps track of the start positions of each contribution/content in the aggregated txt file
			 * It's used to identify the correct discoursedb entities both for exported annotations and also for annotations created after the export.
			 * Strings in this list map entity identifiers to offset (FORMAT: entityClass TAB entityId TAB offset)
			 */
			List<String> entityOffsetMapping = new ArrayList<>();  			

			/*
			 * Keeps track of annotations that have been exported including their version so we know later which ones have been added or changed
			 * Format: <Type(Annotation|Feature), BratAnnoId, DDB_Id, DDB_Version>
			 */
			List<String> exportedAnnotationVersions = new ArrayList<>();   			
			
			int spanOffset = 0;
			for (Contribution contrib : contribService.findAllByDiscoursePart(dp)) {			
				
				Content curRevision = contrib.getCurrentRevision();
				String text = curRevision.getText();
				
				contribExportText.add(CONTRIB_SEPARATOR);
				contribExportText.add(text);
						
				for (AnnotationInstance anno : annoService.findAnnotations(curRevision)) {
					annos.addAll(convertAnnotation(anno, spanOffset, CONTRIB_SEPARATOR, text, curRevision, exportedAnnotationVersions));					
				}
				for (AnnotationInstance anno : annoService.findAnnotations(contrib)) {
					annos.addAll(convertAnnotation(anno, spanOffset, CONTRIB_SEPARATOR, text, contrib, exportedAnnotationVersions));					
				}

				//keep track of offsets
				entityOffsetMapping.add(EntityTypes.CONTRIBUTION.name()+"\t"+contrib.getId()+"\t"+spanOffset);
				entityOffsetMapping.add(EntityTypes.CONTENT.name()+"\t"+curRevision.getId()+"\t"+spanOffset);

				//update span offsets
				spanOffset+=text.length()+1;
				spanOffset+=CONTRIB_SEPARATOR.length()+1;				
			}
		
			String dpprefix = dp.getClass().getAnnotation(Table.class).name();
			FileUtils.writeLines(new File(outputFolder,dpprefix + "_"+dp.getId()+".txt"),contribExportText);				
			FileUtils.writeLines(new File(outputFolder,dpprefix + "_"+dp.getId()+".ann"),annos);
			FileUtils.writeLines(new File(outputFolder,dpprefix + "_"+dp.getId()+".offsets"),entityOffsetMapping);
			FileUtils.writeLines(new File(outputFolder,dpprefix + "_"+dp.getId()+".versions"),exportedAnnotationVersions);
		}	
	}

	
	private <T extends BaseEntity & Identifiable<Long>> List<BratAnnotation> convertAnnotation(AnnotationInstance dbAnno, int spanOffset, String separator, String text, T entity, List<String> exportedAnnotations) {

		//one DiscourseDB annotation could result in multiple BRAT annotations 
		List<BratAnnotation> newAnnotations = new ArrayList<>();

		if (dbAnno.getType() != null&&dbAnno.getType().equals(BratAnnotationType.R.name())) {
			//specifically handle DiscourseDB annotation that have the Brat Type R
			//to produce the corresponding BRAT annotation
			//TODO implement
			log.warn("BRAT Type R annotations are not supported yet.");
		}
		else if (dbAnno.getType() != null&&dbAnno.getType().equals(BratAnnotationType.E.name())) {
			//specifically handle DiscourseDB annotation that have the Brat Type E			
			//Syto produce the corresponding BRAT annotation
			//TODO implement
			log.warn("BRAT Type E annotations are not supported yet.");
		}
		else if (dbAnno.getType() != null&&dbAnno.getType().equals(BratAnnotationType.M.name())) {
			//specifically handle DiscourseDB annotation that have the Brat Type M
			//to produce the corresponding BRAT annotation
			//TODO implement
			log.warn("BRAT Type M annotations are not supported yet.");
		}
		else if (dbAnno.getType() != null&&dbAnno.getType().equals(BratAnnotationType.N.name())) {
			//specifically handle DiscourseDB annotation that have the Brat Type N			
			//to produce the corresponding BRAT annotation
			//TODO implement
			log.warn("BRAT Type N annotations are not supported yet.");
		}
		else {
			//PRODUCE Text-Bound Annotation for ALL other annotations		
			BratAnnotation textBoundAnnotation = new BratAnnotation();  //TODO change meta data to something usable
			textBoundAnnotation.setType(BratAnnotationType.T);			
			textBoundAnnotation.setId(dbAnno.getId());
			textBoundAnnotation.setAnnotationLabel(dbAnno.getType());

			//CALC OFFSET			
			if (entity instanceof Contribution) {
				//annotations on contributions are always annotated on the contribution separator as an entity label 
				textBoundAnnotation.setBeginIndex(spanOffset);
				textBoundAnnotation.setEndIndex(spanOffset+separator.length());
				textBoundAnnotation.setCoveredText(separator);															
			}else if (entity instanceof Content) {
				//content labels are always annotated as text spans on the currentRevision content entity
				if(dbAnno.getEndOffset()==0){
					log.warn("Labels on Content entites should define a span and should not be entity labels."); 
				}
				textBoundAnnotation.setBeginIndex(spanOffset+dbAnno.getBeginOffset()+separator.length()+1);
				textBoundAnnotation.setEndIndex(spanOffset+dbAnno.getEndOffset()+separator.length()+1);
				textBoundAnnotation.setCoveredText(text.substring(dbAnno.getBeginOffset(),dbAnno.getEndOffset()));											
			}		
			newAnnotations.add(textBoundAnnotation);
			exportedAnnotations.add(AnnotationSourceType.ANNOTATION.name()+"\t"+textBoundAnnotation.getFullAnnotationId()+"\t"+dbAnno.getId()+"\t"+dbAnno.getEntityVersion());
			
			//FEATURE VALUES ARE USED TO CREATE BRAT ANNOTATION ATTRIBUTES
			//Feature types are ignores.
			//TODO: only nominal/binary attributes are supported. need to be registered in the conf file. Maybe store them as Notes instead of Attributes?
			for(Feature f:dbAnno.getFeatures()){			
				BratAnnotation newAttribute = new BratAnnotation();
				newAttribute.setType(BratAnnotationType.A);			
				newAttribute.setId(f.getId());
				newAttribute.setAnnotationLabel(f.getValue());
				newAttribute.setSourceAnnotationId(textBoundAnnotation.getFullAnnotationId());
				newAnnotations.add(newAttribute);

				exportedAnnotations.add(AnnotationSourceType.FEATURE.name()+"\t"+textBoundAnnotation.getFullAnnotationId()+"\t"+f.getId()+"\t"+f.getEntityVersion());						
			}
    	}
		return newAnnotations;
	}


	@SuppressWarnings("unused")
	private List<String> extractMetaData(List<BratAnnotation> annos){
		List<String> meta = new ArrayList<>();
		for(BratAnnotation anno:annos){
			meta.add(anno.getMetaData());
		}
		return meta;
	}

}
