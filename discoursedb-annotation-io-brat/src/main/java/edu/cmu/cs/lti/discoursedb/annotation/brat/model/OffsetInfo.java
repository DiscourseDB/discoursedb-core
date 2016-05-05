package edu.cmu.cs.lti.discoursedb.annotation.brat.model;

import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.annotation.brat.model.BratTypes.EntityTypes;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
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
		setType(EntityTypes.valueOf(data[0]));
		setDiscourseDbEntityId(Long.parseLong(data[1]));
		setSpanOffset(Integer.parseInt(data[2]));
	}
	
	EntityTypes type;
	Long discourseDbEntityId;
	int spanOffset;
	
	public String toString(){
		return getType().name()+"\t"+getDiscourseDbEntityId()+"\t"+getSpanOffset();
	}
}
