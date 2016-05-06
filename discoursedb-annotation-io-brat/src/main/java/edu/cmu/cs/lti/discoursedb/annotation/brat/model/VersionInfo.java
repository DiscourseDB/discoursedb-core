package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.AnnotationSourceType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds information about the DiscourseDB entity that is associated with a
 * particular Brat annotation along with its version at export time.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VersionInfo {

	/**
	 * Generates a VersionInfo from a String that contains data in the format produced by toString()
	 * 
	 * @param parseLine
	 */
	public VersionInfo(String parseLine){
		String[] data = parseLine.split("\t");
		Assert.isTrue(data.length==4, "Illegal format of version info: "+parseLine);
		setType(AnnotationSourceType.valueOf(data[0]));
		setBratAnnotationId(data[1]);
		setDiscourseDBEntityId(Long.parseLong(data[2]));
		setDiscourseDBEntityVersion(Long.parseLong(data[3]));		
	}
	
	AnnotationSourceType type;
	String bratAnnotationId;
	Long discourseDBEntityId;
	Long discourseDBEntityVersion;
	
	@Override
	public String toString(){
		return getType().name()+"\t"+getBratAnnotationId()+"\t"+getDiscourseDBEntityId()+"\t"+getDiscourseDBEntityVersion();
	}
}
