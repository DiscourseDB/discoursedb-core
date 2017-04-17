/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
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
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;
import javax.persistence.Table;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Identifiable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.google.common.collect.Lists;

import antlr.Utils;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratAnnotation;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratSeparator;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.AnnotationSourceType;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.BratAnnotationType;
import edu.cmu.cs.lti.discoursedb.annotation.brat.util.UtilService;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.CleanupInfo;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.OffsetInfo;
import edu.cmu.cs.lti.discoursedb.annotation.brat.model.VersionInfo;
import edu.cmu.cs.lti.discoursedb.core.model.BaseEntity;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class BratService {

	private final @NonNull DiscourseService discourseService;
	@Autowired private final @NonNull ContributionService contribService;
	private final @NonNull ContentService contentService;
	private final @NonNull AnnotationService annoService;
	private final @NonNull DiscoursePartService dpService;
	@Autowired private final @NonNull UtilService utilService;
	
	/**
	 * Imports the annotations of all brat-annotated documents located in the provided folder.  
	 * 
	 * @param inputFolder the path to the brat corpus folder to import 
	 * @throws IOException if an Exception occurs accessing the folder
	 */
	public void importDataset(String inputFolder) throws IOException{
		Assert.hasText(inputFolder, "inputFolder parameter cannot be empty");
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
	public void importThread(String inputFolder, String baseFileName) throws IOException{
		Assert.hasText(inputFolder, "inputFolder parameter cannot be empty");
		Assert.hasText(baseFileName, "baseFileName parameter cannot be empty");
		File dir = new File(inputFolder);
		Assert.isTrue(dir.isDirectory(),"Provided parameter has to be a path to a folder.");

		// The importThreadFromBrat call performs the main import work
		// and the cleanup call deletes discoursedb annotations that have been
		// deleted in Brat. They need to run in separate transactions for the
		// deletion to work.
		log.info("Starting import of "+baseFileName);		
		cleanupAfterImport(importThreadFromBrat(inputFolder, baseFileName));
		log.trace("Finished import of "+baseFileName);
	}
	
	
	/**
	 * Imports a thread in Brat stand-off format into discoursedb.
	 * 
	 * @param inputFolder folder with the brat annotation and meta data
	 * @param baseFileName the base filename for the current thread to be imported
	 * @return an info object containing lists of ids of annotations and featured to be deleted after the import 
	 * @throws IOException if any exception occurs while reading the brat annotations or meta data
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
	private CleanupInfo importThreadFromBrat(String inputFolder, String baseFileName) throws IOException{
		Assert.hasText(inputFolder, "inputFolder parameter cannot be empty");
		Assert.hasText(baseFileName, "baseFileName parameter cannot be empty");

		File annFile = new File(inputFolder, baseFileName + ".ann");
		File offsetFile = new File(inputFolder, baseFileName + ".offsets");
		File versionsFile = new File(inputFolder, baseFileName + ".versions");
				
		// get mapping from entity to offset
		TreeMap<Integer, OffsetInfo> offsetToOffsetInfo = getOffsetToOffsetInfoMap(offsetFile);

		// keep track of versions of orginally exported annotations and features
		Map<String, VersionInfo> annotationBratIdToVersionInfo = getBratIdToDdbIdMap(versionsFile, AnnotationSourceType.DDB_ANNOTATION);
		Map<String, VersionInfo> featureBratIdToVersionInfo = getBratIdToDdbIdMap(versionsFile, AnnotationSourceType.DDB_FEATURE);

		
		DiscoursePart dp = dpService.findOne(Long.parseLong(baseFileName.substring(baseFileName.lastIndexOf("_")+1))).get();			
		
		//Init ddb annotation stats for deletion handling
		Set<Long> ddbAnnotationIds = new HashSet<>();
		Set<Long> ddbFeatureIds = new HashSet<>();
		//extract annotations on Contributions
		for(AnnotationInstance anno:annoService.findContributionAnnotationsByDiscoursePart(dp)){
			ddbAnnotationIds.add(anno.getId());
			if(anno.getFeatures()!=null){
				ddbFeatureIds.addAll(anno.getFeatures().stream().map(f->f.getId()).collect(Collectors.toList()));
			}
		}
		//extract annotations on Content entities
		for(AnnotationInstance anno:annoService.findCurrentRevisionAnnotationsByDiscoursePart(dp)){
			ddbAnnotationIds.add(anno.getId());
			if(anno.getFeatures()!=null){
				ddbFeatureIds.addAll(anno.getFeatures().stream().map(f->f.getId()).collect(Collectors.toList()));
			}
		}
		log.info(ddbAnnotationIds.size()+" annotations within current thread available in DiscoursDB.");
		log.info(ddbFeatureIds.size()+" features within current thread available in DiscoursDB.");
		
		
		List<String> bratStandoffEncodedStrings =FileUtils.readLines(annFile);  
		//sorting in reverse order assures that Attribute annotations (A) are imported after text-bound annotations (T)
		Collections.sort(bratStandoffEncodedStrings, Collections.reverseOrder());
		for (String bratStandoffEncodedString : bratStandoffEncodedStrings) {

			// create BratAnnotation object from Brat-Stand-off-Encoded String
			// offset correction will be done later
			BratAnnotation bratAnno = new BratAnnotation(bratStandoffEncodedString);				

			if (bratAnno.getType() == BratAnnotationType.BRAT_TEXT) {					
				
				Entry<Integer, OffsetInfo> offset = offsetToOffsetInfo.floorEntry(bratAnno.getBeginIndex());
				Contribution contrib = contribService.findOne(offset.getValue().getDiscourseDbContributionId()).get();
				Content content = contentService.findOne(offset.getValue().getDiscourseDbContentId()).get();
				long separatorStartIndex = offset.getKey();
				long separatorEndIndex = separatorStartIndex+ BratSeparator.length;
				long textEndIndex = separatorEndIndex + content.getText().length();
				
				// CONTRIBUTION LABEL: Annotation is completely within a separator
				if (bratAnno.getBeginIndex() >= separatorStartIndex && bratAnno.getBeginIndex() <= separatorEndIndex
						&& bratAnno.getEndIndex() >= separatorStartIndex
						&& bratAnno.getEndIndex() <= separatorEndIndex) {

					// check if annotation already existed before
					if (annotationBratIdToVersionInfo.keySet().contains(bratAnno.getId())) {
						VersionInfo entityInfo = annotationBratIdToVersionInfo.get(bratAnno.getId());							

						ddbAnnotationIds.remove(entityInfo.getDiscourseDBEntityId()); //update deletion stats

						AnnotationInstance existingAnno = annoService.findOneAnnotationInstance(entityInfo.getDiscourseDBEntityId()).get();

						//check if the anno version in the database still matches the anno version we initially exported 
						if(existingAnno.getEntityVersion()==entityInfo.getDiscourseDBEntityVersion()){
							existingAnno.setBeginOffset(0);
							existingAnno.setEndOffset(0);
							existingAnno.setType(bratAnno.getAnnotationLabel());
						}else{
							log.error("Entity changed in DiscourseDB since the data was last import but also changed in the exported file. Cannot import annotation.");
						}
						
					} else {
						// anno is new and didn't exist in ddb before
						AnnotationInstance newAnno = annoService.createTypedAnnotation(bratAnno.getAnnotationLabel());
						annoService.addAnnotation(contrib, newAnno);
						contribService.save(contrib); //this should happen in addAnnotation. Filed Issue #15
						//update version file
						annotationBratIdToVersionInfo.put(bratAnno.getId(), new VersionInfo(AnnotationSourceType.DDB_ANNOTATION,bratAnno.getId(),newAnno.getId(), newAnno.getEntityVersion())); 
					}
				} 
				// SPAN ANNOTATION WITHIN CONTRIBUTION TEXT (does neither span over separator nor over multiple contributions)
				else if (bratAnno.getBeginIndex() > separatorEndIndex && bratAnno.getBeginIndex() <= textEndIndex&&bratAnno.getEndIndex() > separatorEndIndex && bratAnno.getEndIndex() <= textEndIndex) {

					// calculate offset corrected index values for span annotation
					int offsetCorrectedBeginIdx = bratAnno.getBeginIndex() - offset.getKey() - BratSeparator.length - 1;
					int offsetCorrectedEndIdx = bratAnno.getEndIndex() - offset.getKey() - BratSeparator.length - 1;

					// check if annotation already existed before
					if (annotationBratIdToVersionInfo.keySet().contains(bratAnno.getId())) {
						VersionInfo entityInfo = annotationBratIdToVersionInfo.get(bratAnno.getId());							
						ddbAnnotationIds.remove(entityInfo.getDiscourseDBEntityId()); //update deletion stats

						// Anno already existed. Check for changes.
						AnnotationInstance existingAnno = annoService.findOneAnnotationInstance(entityInfo.getDiscourseDBEntityId()).get();

						//check if the anno version in the database still matches the anno version we initially exported
						//if so, we can update
						if(existingAnno.getEntityVersion()==entityInfo.getDiscourseDBEntityVersion()){
							existingAnno.setBeginOffset(offsetCorrectedBeginIdx);
							existingAnno.setEndOffset(offsetCorrectedEndIdx);
							existingAnno.setType(bratAnno.getAnnotationLabel());
						}else{
							log.error("Entity changed in DiscourseDB since the data was last import but also changed in the exported file. Cannot import annotation.");
						}
					} else {
						// Anno is new and didn't exist in ddb before. Create it.
						AnnotationInstance newAnno = annoService.createTypedAnnotation(bratAnno.getAnnotationLabel());
						newAnno.setBeginOffset(offsetCorrectedBeginIdx);
						newAnno.setEndOffset(offsetCorrectedEndIdx);
						annoService.addAnnotation(content, newAnno);
						contentService.save(content); //this should happen in addAnnotation. Filed Issue #15
						//update version file
						annotationBratIdToVersionInfo.put(bratAnno.getId(), new VersionInfo(AnnotationSourceType.DDB_ANNOTATION,bratAnno.getId(),newAnno.getId(), newAnno.getEntityVersion())); 
					}
				}else{
					log.error("Annotation extends over contribution separator(s) AND text. You can only annotate within a separator or within a contribution. Skipping this annotation...");
				}
			} else if (bratAnno.getType() == BratAnnotationType.BRAT_NOTE) {
				
				VersionInfo entityInfo = featureBratIdToVersionInfo.get(bratAnno.getId());
				
				// check if feature already existed before
				if (featureBratIdToVersionInfo.keySet().contains(bratAnno.getId())) {
					ddbFeatureIds.remove(entityInfo.getDiscourseDBEntityId()); //update deletion stats

					// feature already existed
					Feature existingFeature = annoService.findOneFeature(entityInfo.getDiscourseDBEntityId()).get();

					//check if the feature version in the database still matches the feature version we initially exported 
					if(existingFeature.getEntityVersion()==entityInfo.getDiscourseDBEntityVersion()){
						//check for and apply changes
						if(existingFeature.getValue().equalsIgnoreCase(bratAnno.getAnnotationLabel())){
							existingFeature.setValue(bratAnno.getAnnotationLabel());
						}
					}else{
						log.error("Entity changed in DiscourseDB since the data was last import but also changed in the exported file. Cannot import feature.");							
					}
				} else {
					// feature didn't exist in database yet. Create it.
					VersionInfo referenceAnnotationInfo = annotationBratIdToVersionInfo.get(bratAnno.getSourceAnnotationId());
					if(referenceAnnotationInfo!=null){
						AnnotationInstance referenceAnno = annoService.findOneAnnotationInstance(referenceAnnotationInfo.getDiscourseDBEntityId()).get();
						Feature newFeature = annoService.createTypedFeature(bratAnno.getNoteText(), bratAnno.getType().name());
						//update version file
						featureBratIdToVersionInfo.put(bratAnno.getId(), new VersionInfo(AnnotationSourceType.DDB_FEATURE,bratAnno.getId(),newFeature.getId(), newFeature.getEntityVersion())); 
						annoService.addFeature(referenceAnno, newFeature);						
						annoService.saveFeature(newFeature); //this should happen in addFeature. Filed Issue #15
					}else{
						log.error("Cannot find the annotation this feature applies to.");
					}						
				}
			} else {
				//Implement import capabilities for other Brat Annotation types here
				log.error("Unsupported Annotation type " + bratAnno.getType()+" Skipping.");
			}
		}

		//Regenerate the version infos updated data from the newly created annotations 
		List<VersionInfo> updatedVersionInfo = new ArrayList<>();
		updatedVersionInfo.addAll(annotationBratIdToVersionInfo.values());
		updatedVersionInfo.addAll(featureBratIdToVersionInfo.values());
		FileUtils.writeLines(versionsFile,updatedVersionInfo);

		//return info about entities to be deleted
		return new CleanupInfo(versionsFile, ddbFeatureIds, ddbAnnotationIds);
	}
	
	/**
	 * Deletes annotations and features identified by a list of ids.
	 * Also updates the versions file.
	 * 
	 * @param featureIds a list of discourse db feature ids
	 * @param annotationIds a list of discoursedb annotation ids
	 */
	@Transactional(propagation= Propagation.REQUIRES_NEW, readOnly=false)
	private void cleanupAfterImport(CleanupInfo cleanupInfo) throws IOException{
		Assert.notNull(cleanupInfo, "cleanup info cannot be null");
		
		//delete features from DiscourseDB that have been deleted in brat
		for(Long id:cleanupInfo.getFeaturesToDelete()){
			log.info("Delete feature "+id);
			annoService.deleteFeature(id);
		}
		//delete annotations from DiscourseDB that have been deleted in brat
		for(Long id:cleanupInfo.getAnnotationsToDelete()){
			log.info("Delete annotation "+id);
			annoService.deleteAnnotation(id);
		}
		
		//cleanup versions file - remove deleted entities
		List<VersionInfo> filteredVersionFile = new ArrayList<>();
		for(String line: FileUtils.readLines(cleanupInfo.getVersionsFile())){
			VersionInfo info = new VersionInfo(line);
			if(info.getType()==AnnotationSourceType.DDB_ANNOTATION){
				if(!cleanupInfo.getAnnotationsToDelete().contains(info.getDiscourseDBEntityId())){
					filteredVersionFile.add(info);
				}				
			}else if(info.getType()==AnnotationSourceType.DDB_FEATURE){
				if(!cleanupInfo.getFeaturesToDelete().contains(info.getDiscourseDBEntityId())){
					filteredVersionFile.add(info);
				}								
			}
		}
		FileUtils.writeLines(cleanupInfo.getVersionsFile(), filteredVersionFile);		
	}

	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public void exportDiscoursePart(DiscoursePart dp, String outputFolder) throws IOException{
		 exportDiscoursePart(dp, outputFolder, false);
	}
	
	public String discoursePart2BratName(DiscoursePart dp) {
		return dp.getName().replaceAll("[^a-zA-Z0-9]", "_") + "_" + dp.getId().toString();
	}

	
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public void exportDiscoursePart(DiscoursePart dp, String outputFolder, Boolean threaded) throws IOException{
		Assert.notNull(dp, "The DiscoursePart cannot be null");
		Assert.hasText(outputFolder, "The outputFolder has to be specified");
		
		//define a common base filename for all files associated with this DiscoursePart
		String baseFileName = discoursePart2BratName(dp);
		//dp.getClass().getAnnotation(Table.class).name() + "_"+dp.getId();  
		// delete me
		
		//The offset mapping keeps track of the start positions of each contribution/content in the aggregated txt file
		List<OffsetInfo> entityOffsetMapping = new ArrayList<>();  					
		List<String> discoursePartText = new ArrayList<>();
		List<BratAnnotation> bratAnnotations = new ArrayList<>();			
		BratIdGenerator bratIdGenerator = new BratIdGenerator();
		
		int spanOffset = 0;
		
		// Sort contributions by their start time, without crashing on null
		List<Contribution> contribsTimeOrdered = Lists.newArrayList(contribService.findAllByDiscoursePart(dp));
		//This should be (maybe, optionally) a depth-first sort, with start time as a tiebreaker.
		contribsTimeOrdered.sort((c1,c2) -> {
			if (c1 == null) { return -1; }
			else if (c2 == null) { return 1; }
			else if (c1.getStartTime() == c2.getStartTime()) {return c1.getId().compareTo(c2.getId()); }
			else { return c1.getStartTime().compareTo(c2.getStartTime()); }
		});
		List<Contribution> contribs = null;
		if (threaded) {
			contribs = utilService.threadsort(contribsTimeOrdered, c -> c.getId(), 
				c -> { Contribution p = contribService.getOneRelatedContribution(c);
						if (p == null) { return 0L; } else { return p.getId(); }
						});
		} else {
			contribs = contribsTimeOrdered;
		}

		// Export current revision of sorted contributions
		for (Contribution contrib : contribs) {			
			
			Content curRevision = contrib.getCurrentRevision();
			String text = curRevision.getText();
			
			String sep = new BratSeparator(0, contrib.getCurrentRevision().getAuthor().getUsername(), contrib.getStartTime()).get();
			discoursePartText.add(sep);
			discoursePartText.add(text);
								
			//annotations on content
			for (AnnotationInstance anno : annoService.findAnnotations(curRevision)) {
				bratAnnotations.addAll(convertAnnotationToBrat(anno, spanOffset, sep, text, curRevision, bratIdGenerator));					
			}
			//annotations on contributions
			for (AnnotationInstance anno : annoService.findAnnotations(contrib)) {
				bratAnnotations.addAll(convertAnnotationToBrat(anno, spanOffset, sep, text, contrib, bratIdGenerator));					
			}

			//keep track of offsets
			entityOffsetMapping.add(new OffsetInfo(spanOffset, contrib.getId(),curRevision.getId()));

			//update span offsets
			spanOffset+=text.length()+1;
			spanOffset+=BratSeparator.length+1;				
		}
	
		FileUtils.writeLines(new File(outputFolder,baseFileName+".txt"),discoursePartText);				
		FileUtils.writeLines(new File(outputFolder,baseFileName+".ann"),bratAnnotations);
		FileUtils.writeLines(new File(outputFolder,baseFileName+".offsets"),entityOffsetMapping);
		FileUtils.writeLines(new File(outputFolder,baseFileName+".versions"),bratAnnotations.stream().map(anno->anno.getVersionInfo()).filter(Objects::nonNull).collect(Collectors.toList()));
	}
	
	/**
	 * Converts a DiscourseDB annotation into Brat annotations.
	 * A single DiscourseDB annotation might result in multiple Brat annotations 
	 * 
	 * @param dbAnno the DiscourseDB annotation to convert
	 * @param spanOffset the current offset of the Contribution or Content within the aggregate document
	 * @param text the contribution text
	 * @param entity the annotated entity (Contribution or Content)
	 * @param annotationVersionInfo a list in which version information about the exported annotations will be stored 
	 * @return a list of BratAnnotations 
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	private <T extends BaseEntity & Identifiable<Long>> List<BratAnnotation> convertAnnotationToBrat(AnnotationInstance dbAnno, int spanOffset, String sep, String text, T entity, BratIdGenerator bratIdGenerator) {
		Assert.notNull(dbAnno, "The annotation instance to be converted cannot be null.");
		Assert.notNull(text, "The text may be empty, but not null.");
		Assert.notNull(entity, "The entity associated with the annotation cannot be null.");
		Assert.notNull(bratIdGenerator, "The Brat IDGenerator cannot be null.");
		
		//one DiscourseDB annotation could result in multiple BRAT annotations 
		List<BratAnnotation> newAnnotations = new ArrayList<>();
		
		//PRODUCE Text-Bound Annotation for ALL other annotations		
		BratAnnotation textBoundAnnotation = new BratAnnotation();
		textBoundAnnotation.setType(BratAnnotationType.BRAT_TEXT);			
		textBoundAnnotation.setId(bratIdGenerator.getNextAvailableBratId(BratAnnotationType.BRAT_TEXT, dbAnno.getId()));
		textBoundAnnotation.setAnnotationLabel(dbAnno.getType());

		//CALC OFFSET			
		if (entity instanceof Contribution) {
			//annotations on contributions are always annotated on the contribution separator as an entity label 
			textBoundAnnotation.setBeginIndex(spanOffset);
			textBoundAnnotation.setEndIndex(spanOffset+ sep.length());
			textBoundAnnotation.setCoveredText(sep);															
		}else if (entity instanceof Content) {
			//content labels are always annotated as text spans on the currentRevision content entity
			if(dbAnno.getEndOffset()==0){
				log.warn("Labels on Content entites should define a span and should not be entity labels."); 
			}
			textBoundAnnotation.setBeginIndex(spanOffset+dbAnno.getBeginOffset()+sep.length()+1);
			textBoundAnnotation.setEndIndex(spanOffset+dbAnno.getEndOffset()+sep.length()+1);
			textBoundAnnotation.setCoveredText(text.substring(dbAnno.getBeginOffset(),dbAnno.getEndOffset()));											
		}		
		textBoundAnnotation.setVersionInfo(new VersionInfo(AnnotationSourceType.DDB_ANNOTATION, textBoundAnnotation.getId(), dbAnno.getId(), dbAnno.getEntityVersion()));
		newAnnotations.add(textBoundAnnotation);
		
		//FEATURE VALUES ARE USED TO CREATE BRAT ANNOTATION ATTRIBUTES. Feature types are ignored.
		for(Feature f:dbAnno.getFeatures()){			
			BratAnnotation newAttribute = new BratAnnotation();
			newAttribute.setType(BratAnnotationType.BRAT_NOTE);			
			newAttribute.setId(bratIdGenerator.getNextAvailableBratId(BratAnnotationType.BRAT_NOTE, f.getId()));
			newAttribute.setAnnotationLabel("AnnotatorNotes");
			newAttribute.setNoteText(f.getValue());
			newAttribute.setSourceAnnotationId(textBoundAnnotation.getId());
			newAttribute.setVersionInfo(new VersionInfo(AnnotationSourceType.DDB_FEATURE, newAttribute.getId(), f.getId(), f.getEntityVersion()));
			newAnnotations.add(newAttribute);				
		}
    	
		return newAnnotations;
	}	
	

	/**
	 * Parses the offsets file and provides a Map from offset to discoursedb id. This is used to 
	 * identify discoursedb entities by offset in order to identify the contribution at a specific point in the aggated (thread-level) document. 
	 * 
	 * @param offsetFile file with the offset mapping
	 * @return a TreeMap (has to be a TreeMap because of the required floorEntry method) mapping offset values to entity ids for the given entity type
	 * @throws IOException if an exception occurred while accessing the offset file 
	 */
	private TreeMap<Integer, OffsetInfo> getOffsetToOffsetInfoMap(File offsetFile) throws IOException {
		Assert.notNull(offsetFile, "OffsetFile has to be specified.");
		
		TreeMap<Integer, OffsetInfo> offsetToOffsetInfo = new TreeMap<>();
		for (String line : FileUtils.readLines(offsetFile)) {
			OffsetInfo info = new OffsetInfo(line);
			offsetToOffsetInfo.put(info.getSpanOffset(),info);
		}
		return offsetToOffsetInfo;
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
	private Map<String, VersionInfo> getBratIdToDdbIdMap(File versionFile, AnnotationSourceType sourceType) throws IOException {
		Assert.notNull(versionFile, "OffsetFile has to be specified.");
		Assert.notNull(sourceType, "A source type needs to be specified.");

		Map<String, VersionInfo> bratIdToDdbVersion = new HashMap<>();
		for (String line : FileUtils.readLines(versionFile)) {
			VersionInfo info = new VersionInfo(line);
			if (info.getType()==sourceType) {
				bratIdToDdbVersion.put(info.getBratAnnotationId(), info);
			}
		}
		return bratIdToDdbVersion;
	}	

	
	/**
	 * Generates a Brat annotation.conf file with all DiscourseDB annotation types that occur in the set of annotations on contributions or current revisions within the provided discourse registered as Brat annotations.
	 * Relations, events and attribute sections are left empty and not further configuration is generated.
	 *     
	 * @param discourseName the name of the discourse for which to export annotation types
	 * @param outputFolder the folder to which the config file should be written
	 * @throws IOException if an exception occurs writing the config file
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public void generateBratConfig(String discourseName, String outputFolder) throws IOException{
		Assert.hasText(discourseName, "The discourse name cannot be empty.");
		Assert.hasText(outputFolder, "The output folder path  cannot be empty.");
		generateBratConfig(discourseService.findOne(discourseName).orElseThrow(() -> new EntityNotFoundException("Discourse with name " + discourseName + " does not exist.")), outputFolder);				
	}

	/**
	 * Generates a Brat annotation.conf file with all DiscourseDB annotation types that occur in the set of annotations on contributions or current revisions within the provided discourse registered as Brat annotations.
	 * Relations, events and attribute sections are left empty and not further configuration is generated.
	 *     
	 * @param discourseName the name of the discourse for which to export annotation types
	 * @param outputFolder the folder to which the config file should be written
	 * @throws IOException if an exception occurs writing the config file
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public void generateBratConfig(String outputFolder) throws IOException{
		Assert.hasText(outputFolder, "The output folder path  cannot be empty.");
		Set<String> annoTypes = new HashSet<>();
		for(Discourse curDiscourse:discourseService.findAll()){
			for(DiscoursePart dp: dpService.findAllByDiscourse(curDiscourse)){
				annoTypes.addAll(annoService.findContributionAnnotationsByDiscoursePart(dp).stream().map(anno->BratAnnotation.cleanString(anno.getType())).collect(Collectors.toSet()));
				annoTypes.addAll(annoService.findCurrentRevisionAnnotationsByDiscoursePart(dp).stream().map(anno->BratAnnotation.cleanString(anno.getType())).collect(Collectors.toSet()));
			}	
		}		
		generateBratConfig(outputFolder, annoTypes);
	}
	
	/**
	 * Generates a Brat annotation.conf file with all DiscourseDB annotation types that occur in the set of annotations on contributions or current revisions within the provided discourse registered as Brat annotations.
	 * Relations, events and attribute sections are left empty and not further configuration is generated.
	 *     
	 * @param discourse the discourse for which to export annotation types
	 * @param outputFolder the folder to which the config file should be written
	 * @throws IOException if an exception occurs writing the config file
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public void generateBratConfig(Discourse discourse, String outputFolder) throws IOException{
		Assert.notNull(discourse, "The discourse cannot be null.");
		Assert.hasText(outputFolder, "The output folder path  cannot be empty.");
		Set<String> annoTypes = new HashSet<>();

		for(DiscoursePart dp: dpService.findAllByDiscourse(discourse)){
			annoTypes.addAll(annoService.findContributionAnnotationsByDiscoursePart(dp).stream().map(anno->BratAnnotation.cleanString(anno.getType())).collect(Collectors.toSet()));
			annoTypes.addAll(annoService.findCurrentRevisionAnnotationsByDiscoursePart(dp).stream().map(anno->BratAnnotation.cleanString(anno.getType())).collect(Collectors.toSet()));
		}	
				
		generateBratConfig(outputFolder, annoTypes);
	}

	/**
	 * Generates an empty brat anotation.conf file and registers the provided set of annotation types.
	 *     
	 * @param outputFolder the folder to which the config file should be written
	 * @param annotationTypes a set of annotation types to register in the brat configuration file
	 * @throws IOException if an exception occurs writing the config file
	 */
	@Transactional(propagation= Propagation.REQUIRED, readOnly=true)
	public void generateBratConfig(String outputFolder, Set<String> annotationTypes) throws IOException{
		Assert.hasText(outputFolder, "The output folder path  cannot be empty.");
		Assert.notNull(annotationTypes, "The set holding annotation types was null. Please pass a (potentially empty) set.");
		List<String> annotationConf = new ArrayList<>();
		
		annotationConf.add("[relations]");
		annotationConf.add("[events]");
		annotationConf.add("[attributes]");
		annotationConf.add("[entities]");
		annotationConf.addAll(annotationTypes);
		
		FileUtils.writeLines(new File(outputFolder,"annotation.conf"), annotationConf);
	}
	
	/**
	 * The Brat UI auto-generates annotations starting with ID 1. 
	 * If an annotation with id 1 is deleted, the next annotation created will again get id 1.
	 * If we delete an annotation that exists in DiscourseDB that has brat id 1 and then create a new annotation that does not yet exist in discoursedb it
	 * will get the brat id 1 and the import process would not create the annotation since it thinks the annotation with brat id 1 is already there.
	 * That's why we offset all annotations that came from discoursedb with a large number upon export to create a certain id range that will always be
	 * associated with annotations already available in discoursedb.
	 * 
	 * This generator produces brat ids and ensured they don't collide with previously generated ids.
	 */
	protected class BratIdGenerator{
		private static final int BRAT_ID_OFFSET = 100000;

		private List<String> ids = new ArrayList<>(); 
		
		public String getNextAvailableBratId(BratAnnotationType type, long baseId){
			long offsetId = BRAT_ID_OFFSET+baseId;
			String curBratId = type.toString()+offsetId;
			while(ids.contains(curBratId)){
				offsetId++;
				curBratId = type.toString()+offsetId;
			}
			ids.add(curBratId);
			return curBratId;
		}		
	}
	
	
}
