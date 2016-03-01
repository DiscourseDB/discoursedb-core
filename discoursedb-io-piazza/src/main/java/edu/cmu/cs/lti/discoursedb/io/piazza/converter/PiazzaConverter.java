package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.io.piazza.model.PiazzaContent;

/**
 * This converter loads data from a piazza dump file (in JSON format) and maps all entities to DiscourseDB.
 * The DiscourseDB configuration is defined in the dicoursedb-model project and
 * Spring/Hibernate are taking care of connections.
 * 
 * Each piazza dump file is related to a course in Piazza
 * 
 * @author Oliver Ferschke
 * @author Haitian Gong
 *
 */
@Component
public class PiazzaConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(PiazzaConverter.class);

	@Autowired
	PiazzaConverterService converterService;

	private String dataSetName;
	private String discourseName;
	private File inputFile;

	@Override
	public void run(String... args) throws Exception {
		Assert.hasText(args[0], "dataset name invalid: "+args[0]);
		Assert.hasText(args[1], "discourse name invalid: "+args[1]);
		Assert.hasText(args[2], "File name invalid: "+args[2]);

		dataSetName = args[0];
		discourseName = args[1];
		String inputFileName = args[2];
		
		inputFile = new File(inputFileName);
		if (!inputFile.exists() || !inputFile.isFile()) {
			logger.error("Cannot access data dump file: " + inputFileName);
			return;
		}

		logger.info("Start processing dump file: "+inputFileName);
		
		//Parse dump and serially parse on Piazza content object to converter service 
		try (InputStream in = new FileInputStream(inputFile)) {
			@SuppressWarnings("unchecked")
			List<PiazzaContent> contents = (List<PiazzaContent>) new ObjectMapper()
					.enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
					.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
					.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
					.readValues(new JsonFactory().createParser(in), new TypeReference<List<PiazzaContent>>() {
					}).next();

			contents.stream().forEach(c -> converterService.convertPiazzaContent(dataSetName, discourseName, c));
		}

		logger.info("Finished processing Piazza dump.");

	}

}
