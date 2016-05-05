package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.AnnotationSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Implement this to replace the string based representation of each line in the versions files
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionInfo {

	AnnotationSourceType type;
	String bratAnnotationId;
	Long discourseDBEntityId;
	Long discourseDBEntityVersion;
	
	@Override
	public String toString(){
		return getType().name()+"\t"+getBratAnnotationId()+"\t"+getDiscourseDBEntityId()+"\t"+getDiscourseDBEntityVersion();
	}
}
