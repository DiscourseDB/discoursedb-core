package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * @author Oliver Ferschke
 *
 */
@Component
public class PiazzaConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(PiazzaConverter.class);

	@Autowired PiazzaConverterService converterService;

	@Override
	public void run(String... args) throws Exception {
	
	}

}
