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
		if(data.startsWith(BratAnnotationType.TEXT.toString())){
			setType(BratAnnotationType.TEXT);
			int firstTab = data.indexOf("\t");
			int secondTab = data.indexOf("\t",firstTab+1);			
			setId(data.substring(0,firstTab));
			String[] fields = data.substring(firstTab+1, secondTab).split(" ");
			setAnnotationLabel(fields[0]);
			setBeginIndex(Integer.parseInt(fields[1]));
			setEndIndex(Integer.parseInt(fields[2]));
			setCoveredText(data.substring(secondTab+1));
		}else if(data.startsWith(BratAnnotationType.ATTRIBUTE.toString())){
			setType(BratAnnotationType.ATTRIBUTE);
			int firstTab = data.indexOf("\t");
			setId(data.substring(0,firstTab));
			String[] fields = data.substring(firstTab+1).split(" ");
			setAnnotationLabel(fields[0]);
			setSourceAnnotationId(fields[1]);
		}else if(data.startsWith(BratAnnotationType.NOTE.toString())){
			setType(BratAnnotationType.NOTE);
			int firstTab = data.indexOf("\t");
			int secondTab = data.indexOf("\t",firstTab+1);			
			setId(data.substring(0,firstTab));
			String[] fields = data.substring(firstTab+1, secondTab).split(" ");
			//fields[0] holds the label "AnnotatorNotes" which we don't need
			setSourceAnnotationId(fields[1]);
			setAnnotationLabel(data.substring(secondTab+1));
		}else{
			log.error("Unsupported Annotation Type.");
			throw new IllegalArgumentException();
		}		
	}
	
	String id;
		
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
	
	@Override
	public String toString() { 
		if(type==BratAnnotationType.TEXT){
			return id+"\t"+annotationLabel+" "+beginIndex+" "+endIndex+"\t"+coveredText;				
		}else if(type==BratAnnotationType.ATTRIBUTE){
			return id+"\t"+annotationLabel+" "+sourceAnnotationId;
		}else if(type==BratAnnotationType.NOTE){
			return id+"\t"+"AnnotatorNotes"+" "+sourceAnnotationId+"\t"+annotationLabel;
		}else{
			log.error("Unsupported Annotation Type.");
			return super.toString();
		}
	}
}
