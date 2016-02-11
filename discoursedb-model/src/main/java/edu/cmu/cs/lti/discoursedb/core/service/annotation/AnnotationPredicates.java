package edu.cmu.cs.lti.discoursedb.core.service.annotation;

import com.mysema.query.types.expr.BooleanExpression;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.QAnnotationInstance;

public class AnnotationPredicates {
	
	public static BooleanExpression typedAnnotationExists(String type) {
		if (type== null||type.isEmpty()) {
			return QAnnotationInstance.annotationInstance.isNotNull();
		} else {
			return QAnnotationInstance.annotationInstance.type.type.eq(type);
		}
	}
	
	
}
