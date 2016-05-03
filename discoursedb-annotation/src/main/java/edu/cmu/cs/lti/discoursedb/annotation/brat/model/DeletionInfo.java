package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor	
public class DeletionInfo{
	Set<Long> featuresToDelete = new HashSet<>();
	Set<Long> annotationsToDelete = new HashSet<>();
	
	public void addFeatures(Set<Long> features){
		featuresToDelete.addAll(features);
	}
	public void addAnnotations(Set<Long> annotations){
		annotationsToDelete.addAll(annotations);
	}
}