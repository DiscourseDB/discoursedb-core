/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 * Contributor Haitian Gong
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
package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.io.piazza.model.PiazzaContent;
import lombok.extern.log4j.Log4j;

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
@Log4j
@Component
public class PiazzaConverter implements CommandLineRunner {

	@Autowired private PiazzaConverterService converterService;

	private String dataSetName;
	private String discourseName;
	private File inputFile;

	@Override
	public void run(String... args) throws Exception {
		Assert.hasText(args[0], "discourse name invalid: "+args[1]);
		Assert.hasText(args[1], "dataset name invalid: "+args[0]);
		Assert.hasText(args[2], "File name invalid: "+args[2]);

		discourseName = args[0];
		dataSetName = args[1];
		String inputFileName = args[2];
		
		inputFile = new File(inputFileName);
		if (!inputFile.exists() || !inputFile.isFile()) {
			log.error("Cannot access data dump file: " + inputFileName);
			return;
		}

		log.info("Start importing dump file: "+inputFileName);
		
		//Parse dump and serially parse on Piazza content object to converter service 
		try (InputStream in = new FileInputStream(inputFile)) {
			@SuppressWarnings("unchecked")
			List<PiazzaContent> contents = (List<PiazzaContent>) new ObjectMapper()
					.enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
					.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
					.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
					.readValues(new JsonFactory().createParser(in), new TypeReference<List<PiazzaContent>>() {
					}).next();
			contents.stream().forEach(c -> converterService.convertPiazzaContent(discourseName, dataSetName, c));
		}

		log.info("Successfully imported the Piazza dump file.");

	}

}
