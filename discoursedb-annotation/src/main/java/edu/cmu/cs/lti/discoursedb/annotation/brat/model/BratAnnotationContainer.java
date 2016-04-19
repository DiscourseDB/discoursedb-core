package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BratAnnotationContainer {

	Map<BratAnnotationType, List<BratAnnotation>> annoMap = new HashMap<>();

	public void add(BratAnnotation anno) {
		List<BratAnnotation> typeList;
		BratAnnotationType type = anno.getType();
		if (annoMap.containsKey(type)) {
			typeList = annoMap.get(type);
		} else {
			typeList = new ArrayList<>();
		}

		typeList.add(anno);
		annoMap.put(type, typeList);
	}

	
	
	
}
