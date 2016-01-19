package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.io.piazza.model.Content;

/**
 * @author Oliver Ferschke
 *
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
public class PiazzaConverterService {
	
	private static final Logger logger = LogManager.getLogger(PiazzaConverterService.class);
	
	/**
	 * Converts an single Piazza content Object to DiscourseDB
	 * 
	 * @param discourseName the name of the associated discourse
	 * @param content the content object to convert
	 */
	public void convertPiazzaContent(String discourseName, Content content){
		Assert.hasText(discourseName, "No discourse name defined in Piazza content converter");
		Assert.notNull(content, "The Piazza Content to convert was null");
		
		logger.trace("Converting content object "+content.getId());
		
		
	}
}
