/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
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
		if(data.startsWith(BratAnnotationType.BRAT_TEXT.toString())){
			setType(BratAnnotationType.BRAT_TEXT);
			int firstTab = data.indexOf("\t");
			int secondTab = data.indexOf("\t",firstTab+1);			
			setId(data.substring(0,firstTab));
			
			//handle the middle section of the standoff-annotations string			
			String middleSection = data.substring(firstTab+1, secondTab);
			String[] fields;
			//check if we are dealing with a discontinuous annotation
			//convert discontinuous annotation into a continuous annotation
			if(middleSection.substring(middleSection.indexOf(" ")).contains(";")){
				//convert discontinuous annotation into a continuous annotation
				String[] discontinuousFields = middleSection.split(" ");
				fields = new String[3];
				fields[0] = discontinuousFields[0];
				fields[1] = discontinuousFields[1];
				fields[2] = discontinuousFields[discontinuousFields.length-1];
				log.warn("Import of discontinuous annotations not yet importing. Converting "+middleSection.substring(middleSection.indexOf(" "))+"into a continuous annotation "+fields[1]+":"+fields[2]);
 			}else{
				fields = middleSection.split(" ");				
			}
			setAnnotationLabel(fields[0]);
			setBeginIndex(Integer.parseInt(fields[1]));
			setEndIndex(Integer.parseInt(fields[2]));
			setCoveredText(data.substring(secondTab+1));
		}else if(data.startsWith(BratAnnotationType.BRAT_ATTRIBUTE.toString())){
			setType(BratAnnotationType.BRAT_ATTRIBUTE);
			int firstTab = data.indexOf("\t");
			setId(data.substring(0,firstTab));
			String[] fields = data.substring(firstTab+1).split(" ");
			setAnnotationLabel(fields[0]);
			setSourceAnnotationId(fields[1]);
		}else if(data.startsWith(BratAnnotationType.BRAT_NOTE.toString())){
			setType(BratAnnotationType.BRAT_NOTE);
			int firstTab = data.indexOf("\t");
			int secondTab = data.indexOf("\t",firstTab+1);			
			setId(data.substring(0,firstTab));
			String[] fields = data.substring(firstTab+1, secondTab).split(" ");
			setAnnotationLabel(fields[0]);			
			setSourceAnnotationId(fields[1]);
			setNoteText(data.substring(secondTab+1));			
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

	//holds the free-form note of a #-type brat note annotations
	String noteText;

	VersionInfo versionInfo;

	int beginIndex;
	
	int endIndex;
	
	public void setAnnotationLabel(String label){
		if(label!=null){
			annotationLabel = cleanString(label);
		}
	}
	
	@Override
	public String toString() { 
		if(type==BratAnnotationType.BRAT_TEXT){
			return id+"\t"+annotationLabel+" "+beginIndex+" "+endIndex+"\t"+coveredText;				
		}else if(type==BratAnnotationType.BRAT_ATTRIBUTE){
			return id+"\t"+annotationLabel+" "+sourceAnnotationId;
		}else if(type==BratAnnotationType.BRAT_NOTE){
			return id+"\t"+annotationLabel+" "+sourceAnnotationId+"\t"+noteText;
		}else{
			log.error("Unsupported Annotation Type.");
			return super.toString();
		}
	}
	
	public static String cleanString(String input){		
		String output = input.replaceAll("[^a-zA-Z0-9_-]", "_");
		if(!input.equals(output)){
			log.warn("The label "+input+" contains characters that are not supported by brat. Using the following instead: "+output);
		}
		return output;
	}

}
