package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
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
			File annFile = new File(inputFolder, fileName+".ann");
			File offsetFile = new File(inputFolder, fileName+".offsets");

			TreeMap<Integer, Long> offsetToContributionId = getOffsetToIdMap(offsetFile, EntityTypes.CONTRIBUTION);			
			TreeMap<Integer, Long> offsetToContentId = getOffsetToIdMap(offsetFile, EntityTypes.CONTENT);
			
			for(String line:FileUtils.readLines(annFile)){
				//create offset-corrected annotations from line			
				BratAnnotation anno = new BratAnnotation(line);
				
				if(anno.getCoveredText().startsWith(BratThreadExport.SEPARATOR_PREFIX)&&anno.getCoveredText().trim().endsWith(BratThreadExport.SEPARATOR_SUFFIX)){
					//we have a contribution label, so load the contribution in the current offset range 
					Entry<Integer,Long> offset = offsetToContributionId.floorEntry(anno.getBeginIndex());
					Contribution contrib = contribService.findOne(offset.getValue()).get();
					
					//TODO check if annotation already exists

					if(anno.getType()==BratAnnotationType.T){
						AnnotationInstance newAnno = annoService.createTypedAnnotation(anno.getAnnotationLabel());						
						annoService.addAnnotation(contrib, newAnno);						
					}else if(anno.getType()==BratAnnotationType.A){
						//TODO Attributes have to be translated into features
						// we need the reference annotation for that
					}else{
						log.error("Unsupported Annotation type "+anno.getType().name());
					}
					
				}else{
					//we have a span annotation, so load the content in the current offset range
					Entry<Integer,Long> offset = offsetToContentId.floorEntry(anno.getBeginIndex());
					Content content = contentService.findOne(offset.getValue()).get();
					
					//TODO check if annotation already exists
					
					if(anno.getType()==BratAnnotationType.T){
						AnnotationInstance newAnno = annoService.createTypedAnnotation(anno.getAnnotationLabel());
						//apply offset corrected index values
						newAnno.setBeginOffset(anno.getBeginIndex()-offset.getKey()-BratThreadExport.CONTRIB_SEPARATOR.length()-1);
						newAnno.setEndOffset(anno.getEndIndex()-offset.getKey()-BratThreadExport.CONTRIB_SEPARATOR.length()-1);
						annoService.addAnnotation(content, newAnno);						
					}else if(anno.getType()==BratAnnotationType.A){
						//TODO Attributes have to be translated into features
						// we need the reference annotation for that
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
		
}
