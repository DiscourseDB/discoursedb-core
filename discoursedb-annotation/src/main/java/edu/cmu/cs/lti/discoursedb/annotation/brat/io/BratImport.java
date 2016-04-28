package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
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
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.log4j.Log4j;

/**
 * 
 * @author Oliver Ferschke
 */
@Log4j
@Component
@SpringBootApplication
@ComponentScan(basePackages = {
		"edu.cmu.cs.lti.discoursedb.configuration" }, useDefaultFilters = false, includeFilters = {
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
		Assert.isTrue(args.length == 1, "USAGE: BratThreadImport <inputFolder>");
		SpringApplication.run(BratImport.class, args);
	}

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		String inputFolder = args[0];

		File dir = new File(inputFolder);
		// retrieve all files that end with ann, strip off the extension and
		// save the file name without extension in a list
		List<String> fileNames = Arrays.stream(dir.listFiles((d, name) -> name.endsWith(".ann")))
				.map(f -> f.getName().split(".ann")[0]).collect(Collectors.toList());
		
		for (String fileName : fileNames) {

			// TODO SUPPORT FOR ANNOTATION/FEATURE DELETION
			// retrieve a list of existing annotation and feature ids related to
			// this discoursepart in DDB and compare them with the brat
			// annotations to detect deleted annotations
			// NOTE: mapping file is updated with newly created annotations 

			File annFile = new File(inputFolder, fileName + ".ann");
			File offsetFile = new File(inputFolder, fileName + ".offsets");
			File versionsFile = new File(inputFolder, fileName + ".versions");

			// get mapping from entity to offset
			TreeMap<Integer, Long> offsetToContributionId = getOffsetToIdMap(offsetFile, EntityTypes.CONTRIBUTION);
			TreeMap<Integer, Long> offsetToContentId = getOffsetToIdMap(offsetFile, EntityTypes.CONTENT);

			// keep track of versions of orginally exported annotations and features
			Map<String, DDBEntityInfo> annotationBratIdToDDB = getBratIdToDdbIdMap(versionsFile, AnnotationSourceType.ANNOTATION);
			Map<String, DDBEntityInfo> featureBratIdToDDB = getBratIdToDdbIdMap(versionsFile, AnnotationSourceType.FEATURE);

			List<String> bratStandoffEncodedLines =FileUtils.readLines(annFile);  
			//sorting in reverse order assures that Attribute annotations (A) are imported after text-bound annotations (T)
			Collections.sort(bratStandoffEncodedLines, Collections.reverseOrder());
			for (String bratStandoffEncodedLine : bratStandoffEncodedLines) {

				// create BratAnnotation object from Brat-Standoff-Encoded String
				// offset correction will be done later
				BratAnnotation anno = new BratAnnotation(bratStandoffEncodedLine);

				if (anno.getType() == BratAnnotationType.T) {					
					DDBEntityInfo entityInfo = annotationBratIdToDDB.get(anno.getFullAnnotationId());
					
					// if the annotation covers a span of at least half of the length of the separator
					// AND is fully contained in the separator, we assume we are creating an entity annotation
					if (anno.getCoveredText().length() > (BratThreadExport.CONTRIB_SEPARATOR.length() / 2)
							&& BratThreadExport.CONTRIB_SEPARATOR.contains(anno.getCoveredText())) {
						/*
						 * CONTRIBUTION LABEL
						 * Load the contribution entity associated with the current offset range
						 */

						Entry<Integer, Long> offset = offsetToContributionId.floorEntry(anno.getBeginIndex());
						Contribution contrib = contribService.findOne(offset.getValue()).get();

						// check if annotation already existed before
						if (annotationBratIdToDDB.keySet().contains(anno.getFullAnnotationId())) {
							AnnotationInstance existingAnno = annoService.findOneAnnotationInstance(entityInfo.getId()).get();

							//check if the anno version in the database still matches the anno version we initially exported 
							if(existingAnno.getEntityVersion()==entityInfo.getVersion()){
								//check for and apply changes
								if (existingAnno.getBeginOffset() != 0) {
									existingAnno.setBeginOffset(0);
								}
								if (existingAnno.getEndOffset() != 0) {
									existingAnno.setBeginOffset(0);
								}
								if (existingAnno.getType().equalsIgnoreCase(anno.getAnnotationLabel())) {
									existingAnno.setType(anno.getAnnotationLabel());
								}								
							}else{
								log.error("Entity changed in DiscourseDB since the data was last import but also changed in the exported file. Cannot import annotation.");
							}
							
						} else {
							// anno is new and didn't exist in ddb before
							AnnotationInstance newAnno = annoService.createTypedAnnotation(anno.getAnnotationLabel());
							annoService.addAnnotation(contrib, newAnno);
							//add to mapping file in case we also create a feature for this new annotation
							annotationBratIdToDDB.put(anno.getFullAnnotationId(), new DDBEntityInfo(newAnno.getId(), newAnno.getEntityVersion())); 
						}
					} else {
						/*
						 * SPAN ANNOTATION
						 * Load the content entity associated with the current offset range
						 */

						Entry<Integer, Long> offset = offsetToContentId.floorEntry(anno.getBeginIndex());
						Content content = contentService.findOne(offset.getValue()).get();

						// calculate offset corrected index values for span annotation
						int offsetCorrectedBeginIdx = anno.getBeginIndex() - offset.getKey() - BratThreadExport.CONTRIB_SEPARATOR.length() - 1;
						int offsetCorrectedEndIdx = anno.getEndIndex() - offset.getKey() - BratThreadExport.CONTRIB_SEPARATOR.length() - 1;

						// check if annotation already existed before
						if (annotationBratIdToDDB.keySet().contains(anno.getFullAnnotationId())) {
							// Anno already existed. Check for changes.
							AnnotationInstance existingAnno = annoService.findOneAnnotationInstance(entityInfo.getId()).get();

							//check if the anno version in the database still matches the anno version we initially exported 
							if(existingAnno.getEntityVersion()==entityInfo.getVersion()){
								//check for and apply changes
								if (existingAnno.getBeginOffset() != offsetCorrectedBeginIdx) {
									existingAnno.setBeginOffset(offsetCorrectedBeginIdx);
								}
								if (existingAnno.getEndOffset() != offsetCorrectedEndIdx) {
									existingAnno.setBeginOffset(offsetCorrectedEndIdx);
								}
								if (existingAnno.getType().equalsIgnoreCase(anno.getAnnotationLabel())) {
									existingAnno.setType(anno.getAnnotationLabel());
								}								
							}else{
								log.error("Entity changed in DiscourseDB since the data was last import but also changed in the exported file. Cannot import annotation.");
							}

						} else {
							// Anno is new and didn't exist in ddb before. Create it.
							AnnotationInstance newAnno = annoService.createTypedAnnotation(anno.getAnnotationLabel());
							newAnno.setBeginOffset(offsetCorrectedBeginIdx);
							newAnno.setEndOffset(offsetCorrectedEndIdx);
							annoService.addAnnotation(content, newAnno);
						}
					}
					
				} else if (anno.getType() == BratAnnotationType.A) {
					
					DDBEntityInfo entityInfo = featureBratIdToDDB.get(anno.getFullAnnotationId());
					
					// check if annotation already existed before
					if (featureBratIdToDDB.keySet().contains(anno.getFullAnnotationId())) {
						// anno already existed
						Feature existingFeature = annoService.findOneFeature(entityInfo.getId()).get();

						//check if the anno version in the database still matches the anno version we initially exported 
						if(existingFeature.getEntityVersion()==entityInfo.getVersion()){
							//check for and apply changes
							if(existingFeature.getValue().equalsIgnoreCase(anno.getAnnotationLabel())){
								existingFeature.setValue(anno.getAnnotationLabel());
							}
						}else{
							log.error("Entity changed in DiscourseDB since the data was last import but also changed in the exported file. Cannot import feature.");							
						}
					} else {
						// feature didn't exist in database yet. Create it.
						DDBEntityInfo referenceAnnotationInfo = annotationBratIdToDDB.get(anno.getSourceAnnotationId());
						if(referenceAnnotationInfo!=null){
							AnnotationInstance referenceAnno = annoService.findOneAnnotationInstance(referenceAnnotationInfo.getId()).get();
							Feature newFeature = annoService.createTypedFeature(anno.getType().name(), anno.getAnnotationLabel());
							annoService.addFeature(referenceAnno, newFeature);
						}else{
							log.error("Cannot find the annotation this feature applied to.");
						}						
					}
				} else {
					//Implement import capabilities for other Brat Annotation types here
					log.error("Unsupported Annotation type " + anno.getType().name()+" Skipping.");
				}

			}
		}

	}

	private TreeMap<Integer, Long> getOffsetToIdMap(File offsetFile, EntityTypes entityType) throws IOException {
		TreeMap<Integer, Long> offsetToId = new TreeMap<>();
		for (String line : FileUtils.readLines(offsetFile)) {
			String[] fields = line.split("\t");
			if (fields[0].equalsIgnoreCase(entityType.name())) {
				offsetToId.put(Integer.parseInt(fields[2]), Long.parseLong(fields[1]));
			}
		}
		return offsetToId;
	}

	private Map<String, DDBEntityInfo> getBratIdToDdbIdMap(File versionFile, AnnotationSourceType sourceType) throws IOException {
		Map<String, DDBEntityInfo> bratIdToDdbVersion = new HashMap<>();
		for (String line : FileUtils.readLines(versionFile)) {
			String[] fields = line.split("\t");
			if (fields[0].equalsIgnoreCase(sourceType.name())) {
				bratIdToDdbVersion.put(fields[1], new DDBEntityInfo(Long.parseLong(fields[2]),Long.parseLong(fields[3])));
			}
		}
		return bratIdToDdbVersion;
	}

	
	@Data
	@AllArgsConstructor
	protected class DDBEntityInfo{
		Long id;
		Long version;
	}
}
