package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contains all the information necessary during the cleanup phase, i.e. the entities that need to be deleted
 * and the entries in the *.versions files that need to be removed.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor	
public class CleanupInfo{
	
	File versionsFile;
	Set<Long> featuresToDelete = new HashSet<>();	
	Set<Long> annotationsToDelete = new HashSet<>();
	
	public void addFeatures(Set<Long> features){
		featuresToDelete.addAll(features);
	}
	public void addAnnotations(Set<Long> annotations){
		annotationsToDelete.addAll(annotations);
	}
}