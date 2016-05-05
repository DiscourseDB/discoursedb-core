package edu.cmu.cs.lti.discoursedb.annotation.brat.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Identifiable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratAnnotation;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.AnnotationSourceType;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.BratAnnotationType;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.EntityTypes;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.CleanupInfo;
import edu.cmu.cs.lti.discoursedb.core.model.BaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class BratService {

	private final @NonNull ContributionService contribService;
	private final @NonNull ContentService contentService;
	private final @NonNull AnnotationService annoService;
	private final @NonNull DiscoursePartService dpService;
	

	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public void exportDiscoursePart(DiscoursePart dp, String outputFolder, String baseFileName) throws IOException{
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
			
			contribExportText.add(BratTypes.CONTRIB_SEPARATOR);
			contribExportText.add(text);
					
			for (AnnotationInstance anno : annoService.findAnnotations(curRevision)) {
				annos.addAll(convertAnnotationToBrat(anno, spanOffset, BratTypes.CONTRIB_SEPARATOR, text, curRevision, exportedAnnotationVersions));					
			}
			for (AnnotationInstance anno : annoService.findAnnotations(contrib)) {
				annos.addAll(convertAnnotationToBrat(anno, spanOffset, BratTypes.CONTRIB_SEPARATOR, text, contrib, exportedAnnotationVersions));					
			}

			//keep track of offsets
			entityOffsetMapping.add(EntityTypes.CONTRIBUTION.name()+"\t"+contrib.getId()+"\t"+spanOffset);
			entityOffsetMapping.add(EntityTypes.CONTENT.name()+"\t"+curRevision.getId()+"\t"+spanOffset);

			//update span offsets
			spanOffset+=text.length()+1;
			spanOffset+=BratTypes.CONTRIB_SEPARATOR.length()+1;				
		}
	
		FileUtils.writeLines(new File(outputFolder,baseFileName+".txt"),contribExportText);				
		FileUtils.writeLines(new File(outputFolder,baseFileName+".ann"),annos);
		FileUtils.writeLines(new File(outputFolder,baseFileName+".offsets"),entityOffsetMapping);
		FileUtils.writeLines(new File(outputFolder,baseFileName+".versions"),exportedAnnotationVersions);
	}
	
	/**
	 * Converts a DiscourseDB annotation into Brat annotations.
	 * A single DiscourseDB annotation might result in multiple Brat annotations 
	 * 
	 * @param dbAnno the DiscourseDB annotation to convert
	 * @param spanOffset the current offset of the Contribution or Content within the aggregate document
	 * @param separator the separator used to mark the boundaries between contributions
	 * @param text the contribution text
	 * @param entity the annotated entity (Contribution or Content)
	 * @param annotationVersionInfo a list in which version information about the exported annotations will be stored 
	 * @return a list of BratAnnotations 
	 * 
	 * //TODO: Integrate version information in the return object
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	private <T extends BaseEntity & Identifiable<Long>> List<BratAnnotation> convertAnnotationToBrat(AnnotationInstance dbAnno, int spanOffset, String separator, String text, T entity, List<String> annotationVersionInfo) {

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
			annotationVersionInfo.add(AnnotationSourceType.ANNOTATION.name()+"\t"+textBoundAnnotation.getFullAnnotationId()+"\t"+dbAnno.getId()+"\t"+dbAnno.getEntityVersion());
			
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

				annotationVersionInfo.add(AnnotationSourceType.FEATURE.name()+"\t"+textBoundAnnotation.getFullAnnotationId()+"\t"+f.getId()+"\t"+f.getEntityVersion());						
			}
    	}
		return newAnnotations;
	}
	
	/**
	 * Imports the annotations of all brat-annotated documents located in the provided folder.  
	 * 
	 * @param inputFolder the path to the brat corpus folder to import 
	 * @throws IOException if an Exception occurs acessing the folder
	 */
	public void importDataset(String inputFolder) throws IOException{
		File dir = new File(inputFolder);
		Assert.isTrue(dir.isDirectory(),"Provided parameter has to be a path to a folder.");
		
		// retrieve all files that end with ann, strip off the extension and save the file name without extension in a list
		List<String> baseFileNames = Arrays.stream(dir.listFiles((d, name) -> name.endsWith(".ann"))).map(f -> f.getName().split(".ann")[0]).collect(Collectors.toList());		
		for (String baseFileName : baseFileNames) {
			importThread(inputFolder, baseFileName);			
		}		
	}
	
	/**
	 * Imports annotations from a particular brat-annotated thread into DiscourseDB.
	 * 
	 * @param inputFolder the folder with the annotation and meta data files
	 * @param baseFileName the base file name of the current thread
	 * @throws IOException in case an error occurs reading the files
	 */
	//Note: this method is non-transactional, but it calls two methods that each run in their own transaction.
	public void importThread(String inputFolder, String baseFileName) throws IOException{
		// The importThreadFromBrat call performs the main import work
		// and the cleanup call deletes discoursedb annotations that have been
		// deleted in Brat. They need to run in separate transactions for the
		// deletion to work.
		log.info("Starting import of "+baseFileName);		
		cleanupAfterImport(importThreadFromBrat(inputFolder, baseFileName));
		log.trace("Finished import of "+baseFileName);
	}
	
	
	/**
	 * Imports a thread in Brat standoff format into discoursedb.
	 * 
	 * @param inputFolder folder with the brat annotation and meta data
	 * @param baseFileName the base filename for the current thread to be imported
	 * @return an info object containing lists of ids of annotaitons and fetured to be deleted after the import 
	 * @throws IOException if any exception occurs while reading the brat annotations or meta data
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	private CleanupInfo importThreadFromBrat(String inputFolder, String baseFileName) throws IOException{
		File annFile = new File(inputFolder, baseFileName + ".ann");
		File offsetFile = new File(inputFolder, baseFileName + ".offsets");
		File versionsFile = new File(inputFolder, baseFileName + ".versions");
		
				
		// get mapping from entity to offset
		TreeMap<Integer, Long> offsetToContributionId = getOffsetToIdMap(offsetFile, EntityTypes.CONTRIBUTION);
		TreeMap<Integer, Long> offsetToContentId = getOffsetToIdMap(offsetFile, EntityTypes.CONTENT);

		// keep track of versions of orginally exported annotations and features
		Map<String, DDBEntityInfo> annotationBratIdToDDB = getBratIdToDdbIdMap(versionsFile, AnnotationSourceType.ANNOTATION);
		Map<String, DDBEntityInfo> featureBratIdToDDB = getBratIdToDdbIdMap(versionsFile, AnnotationSourceType.FEATURE);

		
		/*
		 * Init ddb annotation stats for deletion handling
		 */		
		// Retrieve ids of existing annotations on contributions and current revision contents within the current DiscourseDB
		// to identify deletions. When importing the brat file, we remove every annotations/feature id we import
		// from this list. The remaining ids are associated with annotations/features that have been deleted in the Brat file.
		DiscoursePart dp = dpService.findOne(Long.parseLong(baseFileName.substring(baseFileName.lastIndexOf("_")+1))).get();			
		Set<Long> ddbAnnotationIds = new HashSet<>();
		Set<Long> ddbFeatureIds = new HashSet<>();
		extractAnnotationAndFeatureIds(annoService.findContributionAnnotationsByDiscoursePart(dp), ddbAnnotationIds, ddbFeatureIds);
		extractAnnotationAndFeatureIds(annoService.findCurrentRevisionAnnotationsByDiscoursePart(dp), ddbAnnotationIds, ddbFeatureIds);
		/* ----  */
		
		
		List<String> bratStandoffEncodedStrings =FileUtils.readLines(annFile);  
		//sorting in reverse order assures that Attribute annotations (A) are imported after text-bound annotations (T)
		Collections.sort(bratStandoffEncodedStrings, Collections.reverseOrder());
		for (String bratStandoffEncodedString : bratStandoffEncodedStrings) {

			// create BratAnnotation object from Brat-Stand-off-Encoded String
			// offset correction will be done later
			BratAnnotation anno = new BratAnnotation(bratStandoffEncodedString);				

			if (anno.getType() == BratAnnotationType.T) {					
				DDBEntityInfo entityInfo = annotationBratIdToDDB.get(anno.getFullAnnotationId());			
				
				//check if the annotation is located within the boundat
				if(anno.getBeginIndex()>=offsetToContentId.floorEntry(anno.getBeginIndex()).getKey()&&anno.getEndIndex()<=offsetToContentId.floorEntry(anno.getBeginIndex()).getKey()+BratTypes.CONTRIB_SEPARATOR.length()){				
					/*
					 * CONTRIBUTION LABEL
					 * Load the contribution entity associated with the current offset range
					 */

					Entry<Integer, Long> offset = offsetToContributionId.floorEntry(anno.getBeginIndex());
					Contribution contrib = contribService.findOne(offset.getValue()).get();

					// check if annotation already existed before
					if (annotationBratIdToDDB.keySet().contains(anno.getFullAnnotationId())) {
						ddbAnnotationIds.remove(entityInfo.getId()); //update deletion stats

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
					int offsetCorrectedBeginIdx = anno.getBeginIndex() - offset.getKey() - BratTypes.CONTRIB_SEPARATOR.length() - 1;
					int offsetCorrectedEndIdx = anno.getEndIndex() - offset.getKey() - BratTypes.CONTRIB_SEPARATOR.length() - 1;

					// check if annotation already existed before
					if (annotationBratIdToDDB.keySet().contains(anno.getFullAnnotationId())) {
						ddbAnnotationIds.remove(entityInfo.getId()); //update deletion stats

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
				
				// check if feature already existed before
				if (featureBratIdToDDB.keySet().contains(anno.getFullAnnotationId())) {
					ddbFeatureIds.remove(entityInfo.getId()); //update deletion stats

					// feature already existed
					Feature existingFeature = annoService.findOneFeature(entityInfo.getId()).get();

					//check if the feature version in the database still matches the feature version we initially exported 
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
						log.error("Cannot find the annotation this feature applies to.");
					}						
				}
			} else {
				//Implement import capabilities for other Brat Annotation types here
				log.error("Unsupported Annotation type " + anno.getType().name()+" Skipping.");
			}
		}
		
		//return info about entities to be deleted
		return new CleanupInfo(versionsFile, ddbFeatureIds, ddbAnnotationIds);
	}
	
	/**
	 * Deletes annotations and features identified by a list of ids.
	 * Also updates the versions file.
	 * 
	 * @param featureIds a list of discourse db feature ids
	 * @param annotationIds a list of discoursedb annotaiton ids
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	private void cleanupAfterImport(CleanupInfo cleanupInfo) throws IOException{
		
		//delete features from DiscourseDB that have been deleted in brat
		for(Long id:cleanupInfo.getFeaturesToDelete()){
			annoService.deleteFeature(id);
		}
		//delete annotations from DiscourseDB that have been deleted in brat
		for(Long id:cleanupInfo.getAnnotationsToDelete()){
			annoService.deleteAnnotation(id);
		}
		
		//cleanup versions file - remove deleted entities
		List<String> output = new ArrayList<>();
		for(String line: FileUtils.readLines(cleanupInfo.getVersionsFile())){
			String[] fields = line.split("\t");
			if(fields[0].equals(AnnotationSourceType.ANNOTATION.name())){
				if(!cleanupInfo.getAnnotationsToDelete().contains(Long.parseLong(fields[2]))){
					output.add(line);
				}				
			}else if(fields[0].equals(AnnotationSourceType.FEATURE.name())){
				if(!cleanupInfo.getFeaturesToDelete().contains(Long.parseLong(fields[2]))){
					output.add(line);
				}								
			}
		}
		//update versions file
		FileUtils.writeLines(cleanupInfo.getVersionsFile(), output);		
	}


	/**
	 * Parses the offsets file and provides a Map from offset to discoursedb id. This is used to 
	 * identify discoursedb entities by offset in order to identify the contirbution at a specific point in the aggated (thread-level) document. 
	 * 
	 * @param offsetFile file with the offset mapping
	 * @param entityType the entity types to extract information for (e.g. annotations or features)
	 * @return a TreeMap (has to be a TreeMap because of the required floorEntry method) mapping offset values to entity ids for the given entity type
	 * @throws IOException if an exception occured while accessing the offset file 
	 */
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

	/**
	 * Parses the a version file and returns a map from brat annotation id to DDBEntityInfo objects which provide 
	 * meta information about the corresponding discourse db entities 
	 * 
	 * @param versionFile the file containing the version information
	 * @param sourceType the brat-type of annotations that should be extracted from the versions file (e.g. text-bound annotations) 
	 * @return a map from brat annotation ids to DDBEntityInfo objects
	 * @throws IOException if an error occured reading the versions file
	 */
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
	
	/**
	 * Populates two provided lists with the annotation id of the provided
	 * annotations and all ids of features associated with these annotations.
	 * 
	 * TODO return an objects that holds the extracted data rather than populating the provided lists
	 * 
	 * @param annos a list of annotations to extract ids from
	 * @param annoIds the list that will be populated with annotation ids  
	 * @param featureIds the list that will be populated with feature ids
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	private void extractAnnotationAndFeatureIds(List<AnnotationInstance> annos, Set<Long> annoIds, Set<Long> featureIds){
		for(AnnotationInstance anno:annos){
			annoIds.add(anno.getId());
			if(anno.getFeatures()!=null){
				featureIds.addAll(anno.getFeatures().stream().map(f->f.getId()).collect(Collectors.toList()));
			}
		}
	}
		
	
	@Data
	@AllArgsConstructor
	protected class DDBEntityInfo{
		Long id;
		Long version;
	}
	
}
