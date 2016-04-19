package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;


@Log4j
@Data
@NoArgsConstructor
public class BratAnnotation {

	long id;
	
	BratAnnotationType type;
	
	/**
	 * Whitespaces are replaced by underscores since BRAT doesn't allow whitespaces in category identifiers.
	 * This might be defined in a DiscourseDB type system
	 */
	String annotationCategory;
	
	String annotationValue;
	
	int beginIndex;
	
	int endIndex;
	
	public void setAnnotationCategory(String category){
		if(category!=null){
			annotationCategory = category.replaceAll(" ", "_");
		}
	}
	
	
	@Override
	public String toString() { 
		if(type==BratAnnotationType.T){
			if(annotationValue!=null){
				return "T"+id+"\t"+annotationCategory+" "+beginIndex+" "+endIndex+"\t"+annotationValue;				
			}else{
				return "T"+id+"\t"+annotationCategory+" "+beginIndex+" "+endIndex+"\tNO VALUE";
			}			
		}else{
			log.error("Unsupported Annotation Type.");
			return super.toString();
		}
	}
}
