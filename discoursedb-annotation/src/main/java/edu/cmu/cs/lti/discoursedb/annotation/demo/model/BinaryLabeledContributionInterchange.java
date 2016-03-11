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
@JsonPropertyOrder({ "table", "contribId", "contribType", "threadIds","labels", "text" })
public class BinaryLabeledContributionInterchange implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long contribId;

	private String table;

	private String contribType;

	private Set<Long> threadIds = new HashSet<Long>();
	public void addThreadId(Long id) {
		if (id != null) {
			this.threadIds.add(id);
		}
	}

	/**
	 * Change to list if multiple identical labels should be allowed
	 */
	private Set<String> labels = new HashSet<String>();
	public void addLabel(String label) {
		if (label != null) {
			this.labels.add(label);
		}
	}

	private String text;
}
