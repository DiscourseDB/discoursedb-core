package edu.cmu.cs.lti.discoursedb.annotation.demo.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Interchange format for binary labels on contributions including their first content
 * 
 * @author Oliver Ferschke
 */
@Data
@EqualsAndHashCode(callSuper=true)
public class BinaryLabeledContributionInterchange extends BinaryLabelInterchange{
	
	private static final long serialVersionUID = 1L;

	private String text;
	
}
