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
	String annotationLabel;
	
	String coveredText;

	//if an annotation or a pair of annotation is referenced, this holds the id (of the first annotation)
	String sourceAnnotationId;

	//if a pair of annotation is referenced, this holds the id of the second annotation
	String targetAnnotationId;
	
	int beginIndex;
	
	int endIndex;
	
	public void setAnnotationLabel(String category){
		if(category!=null){
			annotationLabel = category.replaceAll(" ", "_");
		}
	}
	
	public String getFullAnnotationId(){
		return type.name()+id;				
	}
	
	@Override
	public String toString() { 
		if(type==BratAnnotationType.T){
			return getFullAnnotationId()+"\t"+annotationLabel+" "+beginIndex+" "+endIndex+"\t"+coveredText;				
		}else if(type==BratAnnotationType.A){
			return getFullAnnotationId()+"\t"+annotationLabel+" "+sourceAnnotationId;
		}else{
			log.error("Unsupported Annotation Type.");
			return super.toString();
		}
	}
}
