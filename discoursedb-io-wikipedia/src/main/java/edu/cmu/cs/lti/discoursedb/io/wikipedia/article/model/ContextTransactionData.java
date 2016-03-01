package edu.cmu.cs.lti.discoursedb.io.wikipedia.article.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Holds meta information about a context that is currently being created.
 * This includes the Dates of the first and the last content item and the DiscourseDB id of the created context.
 * 
 * @author Oliver Ferschke
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContextTransactionData{
	private Date firstContent;
	private Date lastContent;
	private Long contextId;

	public boolean isAvailable() {
		return firstContent!=null&&lastContent!=null;
	}
}