package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import org.springframework.util.Assert;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Holds information about where in the aggregated brat file a particular Contribution or Content entity begins.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
public class OffsetInfo {
	
	/**
	 * Generates an OffsetInfo from a String that contains data in the format produced by toString()
	 * 
	 * @param parseLine
	 */
	public OffsetInfo(String parseLine){
		String[] data = parseLine.split("\t");
		Assert.isTrue(data.length==3, "Illegal format of offset info: "+parseLine);
		setSpanOffset(Integer.parseInt(data[0]));
		setDiscourseDbContributionId(Long.parseLong(data[1]));
		setDiscourseDbContentId(Long.parseLong(data[2]));
	}
	
	int spanOffset;
	Long discourseDbContributionId;
	Long discourseDbContentId;
	
	public String toString(){
		return getSpanOffset()+"\t"+getDiscourseDbContributionId()+"\t"+getDiscourseDbContentId();
	}
}
