package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Oliver Ferschke
 *
 */
@Service
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
public class PiazzaConverterService {
	
	private static final Logger logger = LogManager.getLogger(PiazzaConverterService.class);
	
}
