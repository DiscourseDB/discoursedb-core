package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.BratAnnotationType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j;


@Log4j
@Data
@NoArgsConstructor
public class BratAnnotation {

	/**
	 * Populates a BratAnnotation from a String which is formatted like Strings produced by BratAnnotation.toString()
	 * 
	 * @param data a String in the BratAnnotation.toString() format
	 */
	public BratAnnotation(String data){
		if(data.startsWith(BratAnnotationType.T.name())){
			setType(BratAnnotationType.T);
			int firstTab = data.indexOf("\t");
			int secondTab = data.indexOf("\t",firstTab+1);			
			setId(Long.parseLong(data.substring(BratAnnotationType.T.name().length(),firstTab)));
			String[] fields = data.substring(firstTab+1, secondTab).split(" ");
			setAnnotationLabel(fields[0]);
			setBeginIndex(Integer.parseInt(fields[1]));
			setEndIndex(Integer.parseInt(fields[2]));
			setCoveredText(data.substring(secondTab+1));
		}else if(data.startsWith(BratAnnotationType.A.name())){
			setType(BratAnnotationType.A);
			int firstTab = data.indexOf("\t");
			setId(Long.parseLong(data.substring(BratAnnotationType.A.name().length(),firstTab)));
			String[] fields = data.substring(firstTab+1).split(" ");
			setAnnotationLabel(fields[0]);
			setSourceAnnotationId(fields[1]);
		}else{
			log.error("Unsupported Annotation Type.");
			throw new IllegalArgumentException();
		}		
	}
	
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

	VersionInfo versionInfo;

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
