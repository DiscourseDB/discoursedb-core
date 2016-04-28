package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.stream.Collectors;

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

import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratThreadExport.AnnotationSourceType;
import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratThreadExport.EntityTypes;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratAnnotation;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratAnnotationType;
import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import lombok.extern.log4j.Log4j;

/**
 * 
 * @author Oliver Ferschke
 */
@Log4j
@Component
@SpringBootApplication
@ComponentScan(basePackages = { "edu.cmu.cs.lti.discoursedb.configuration" }, useDefaultFilters = false, includeFilters = {
				@ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, value = { BratImport.class,
						BaseConfiguration.class }) })
public class BratImport implements CommandLineRunner {

	@Autowired private ContributionService contribService;
	@Autowired private ContentService contentService;
	@Autowired private AnnotationService annoService;
		

	/**
	 * Launches the SpringBoot application
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		Assert.isTrue(args.length ==1, "USAGE: BratThreadImport <inputFolder>");
		SpringApplication.run(BratImport.class, args);
	}

	@Override
	@Transactional 
	public void run(String... args) throws Exception {
		String inputFolder = args[0];
		
		File dir = new File(inputFolder);
		//retrieve all files that end with ann, strip off the extension and save the file name without extension in a list 
		List<String> fileNames = Arrays.stream(dir.listFiles((d, name) -> name.endsWith(".ann"))).map(f -> f.getName().split(".ann")[0]).collect(Collectors.toList());
		


		for(String fileName:fileNames){

			//TODO retrieve a list of existing annotation and feature ids related to this discoursepart in DDB and compare them with the brat annotations to detect deleted annotations

			File annFile = new File(inputFolder, fileName+".ann");
			File offsetFile = new File(inputFolder, fileName+".offsets");
			File versionsFile = new File(inputFolder, fileName+".versions");

			//get mapping from entity to offset
			TreeMap<Integer, Long> offsetToContributionId = getOffsetToIdMap(offsetFile, EntityTypes.CONTRIBUTION);			
			TreeMap<Integer, Long> offsetToContentId = getOffsetToIdMap(offsetFile, EntityTypes.CONTENT);
			
			//keep track of versions of orginally exported annotations and features
			//TODO map to DDB_ID+DDB_version instead of just DDB_ID
			Map<Long,Long> annotationBratIdToDDB = getBratIdToDdbIdMap(versionsFile, AnnotationSourceType.ANNOTATION);
			Map<Long,Long> featureBratIdToDDB = getBratIdToDdbIdMap(versionsFile, AnnotationSourceType.FEATURE);
						
			
			for(String line:FileUtils.readLines(annFile)){
				//create offset-corrected annotations from line			
				BratAnnotation anno = new BratAnnotation(line);
				
				//if the annotation covers a span of at least half of the length of the separator 
				//AND is fully contained in the separator, we assume we are creating an entity annotation
				if (anno.getCoveredText().length() > (BratThreadExport.CONTRIB_SEPARATOR.length() / 2)
						&& BratThreadExport.CONTRIB_SEPARATOR.contains(anno.getCoveredText())) {

					//we have a contribution label, so load the contribution in the current offset range 
					Entry<Integer,Long> offset = offsetToContributionId.floorEntry(anno.getBeginIndex());
					Contribution contrib = contribService.findOne(offset.getValue()).get();
					
					if(anno.getType()==BratAnnotationType.T){
						//check if annotation already existed before
						if(annotationBratIdToDDB.keySet().contains(anno.getFullAnnotationId())){
							//anno already existed before
							//for now, do nothing. do we need to check for changes in case of contribution labels?
							//if we need to check for changes, then first check if the DDB version changed since we last exported.
							log.debug("Annotation "+anno.getFullAnnotationId()+" already exists. Skipping.");
						}else{
							//anno is new and didn't exist in ddb before
							AnnotationInstance newAnno = annoService.createTypedAnnotation(anno.getAnnotationLabel());						
							annoService.addAnnotation(contrib, newAnno);													
						}
					}else if(anno.getType()==BratAnnotationType.A){
						//check if annotation already existed before
						if(featureBratIdToDDB.keySet().contains(anno.getFullAnnotationId())){
							//TODO check if DDB entities changed in the meantime. If they did, we have to throw an exception. If not, we can continue checking for offline changes

							//TODO feature already existed. check for changes
						}else{
							//feature didn't exist in database yet
							//TODO Attributes have to be translated into features/ We need the reference annotation for that							
						}
					}else{
						//all other annotation types
						log.error("Unsupported Annotation type: "+anno.getType().name());
					}
					
				}else{
					//we have a span annotation, so load the content in the current offset range
					Entry<Integer,Long> offset = offsetToContentId.floorEntry(anno.getBeginIndex());
					Content content = contentService.findOne(offset.getValue()).get();

					//calculate offset corrected index values for span annotation
					int offsetCorrectedBeginIdx = anno.getBeginIndex()-offset.getKey()-BratThreadExport.CONTRIB_SEPARATOR.length()-1;
					int offsetCorrectedEndIdx = anno.getEndIndex()-offset.getKey()-BratThreadExport.CONTRIB_SEPARATOR.length()-1;
					
					if(anno.getType()==BratAnnotationType.T){

						//check if annotation already existed before
						if(annotationBratIdToDDB.keySet().contains(anno.getFullAnnotationId())){
							//anno already existed
							AnnotationInstance existingAnno = annoService.findOneAnnotationInstance(annotationBratIdToDDB.get(anno.getFullAnnotationId())).get();

							//TODO check if DDB entities changed in the meantime. If they did, we have to throw an exception. If not, we can continue checking for offline changes
							
							if(existingAnno.getBeginOffset()!=offsetCorrectedBeginIdx){
								existingAnno.setBeginOffset(offsetCorrectedBeginIdx);
							}
							if(existingAnno.getEndOffset()!=offsetCorrectedEndIdx){
								existingAnno.setBeginOffset(offsetCorrectedEndIdx);								
							}
							if(existingAnno.getType().equalsIgnoreCase(anno.getAnnotationLabel())){
								existingAnno.setType(anno.getAnnotationLabel());
							}
						}else{
							//anno is new and didn't exist in ddb before
							AnnotationInstance newAnno = annoService.createTypedAnnotation(anno.getAnnotationLabel());
							newAnno.setBeginOffset(offsetCorrectedBeginIdx);
							newAnno.setEndOffset(offsetCorrectedEndIdx);
							annoService.addAnnotation(content, newAnno);													
						}
					}else if(anno.getType()==BratAnnotationType.A){
						//check if annotation already existed before
						if(featureBratIdToDDB.keySet().contains(anno.getFullAnnotationId())){
							//TODO check if DDB entities changed in the meantime. If they did, we have to throw an exception. If not, we can continue checking for offline changes

							//TODO feature already existed. check for changes
						}else{
							//feature didn't exist in database yet
							//TODO Attributes have to be translated into features/ We need the reference annotation for that							
						}
					}else{
						log.error("Unsupported Annotation type "+anno.getType().name());
					}				
				}
			}
		}
		
		
	}

	private TreeMap<Integer, Long> getOffsetToIdMap(File offsetFile, EntityTypes entityType) throws IOException
	{
		TreeMap<Integer, Long>  offsetToId = new TreeMap<>();
		for(String line:FileUtils.readLines(offsetFile)){
			String[] fields = line.split("\t");
			if(fields[0].equalsIgnoreCase(entityType.name())){
				offsetToId.put(Integer.parseInt(fields[2]),Long.parseLong(fields[1]));
			}
		}
		return offsetToId;		
	}
	
	
	private Map<Long, Long> getBratIdToDdbIdMap(File versionFile, AnnotationSourceType sourceType) throws IOException
	{
		Map<Long, Long>  bratIdToDdbVersion = new HashMap<>();
		for(String line:FileUtils.readLines(versionFile)){
			String[] fields = line.split("\t");
			if(fields[0].equalsIgnoreCase(sourceType.name())){
				//TODO fields[3] is the entity version - not needed at the moment
				bratIdToDdbVersion.put(Long.parseLong(fields[1]),Long.parseLong(fields[2]));
			}
		}
		return bratIdToDdbVersion;		
	}
		
}
