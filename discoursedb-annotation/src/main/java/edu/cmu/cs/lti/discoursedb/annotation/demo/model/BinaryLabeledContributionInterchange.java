package edu.cmu.cs.lti.discoursedb.annotation.demo.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Data;

/**
 * Interchange format for binary labels on arbitray entities
 * 
 * @author Oliver Ferschke
 */
@Data
@JsonPropertyOrder({ "table", "id", "labels", "text"})
public class BinaryLabeledContributionInterchange implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private Long id;

	private String table;	
	
	/**
	 * Change to list if multiple identical labels should be allowed 
	 */
	private Set<String> labels = new HashSet<String>();
	
	public void addLabel(String label) {
		if(label!=null){
			this.labels.add(label);			
		}
	}	
	
	private String text;
}
