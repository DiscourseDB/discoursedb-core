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
package edu.cmu.cs.lti.discoursedb.io.tags.converter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.io.tags.model.TweetInfo;

/**
 * The TweetConverter loads a csv file with data produced by TAGS v6, and maps entities extracted from the source file to DiscourseDB entities.
 * The DiscourseDB configuration is defined in the dicoursedb-model project and 
 * Spring/Hibernate are taking care of connections.
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */

@Component
public class TweetConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(TweetConverter.class);

	private String dataSetName;
	private String discourseName;

	@Autowired TweetConverterService converterService;

	@Override
	public void run(String... args) throws Exception {
		if (args.length != 3) {
			logger.error("Usage: TweetConverterApplication <DataSourceType> <DataSetName> </path/to/tags.csv>");
			return;
		}
		this.dataSetName = args[0];
		String inFileName = args[1];
		this.discourseName = args[2];

		File infile = new File(inFileName);
		if (!infile.exists() || !infile.isFile() || !infile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			return;
		}

		logger.info("Starting twitter conversion");
		convert(inFileName);
	}

	private void convert(String inFile) throws IOException, ParseException {
		
		HashMap<String, ArrayList<String[]>> idContributionMap = new HashMap<String, ArrayList<String[]>>();

		/*
		 * Phase 1:
		 * read through input file once and map all entities 
		 */
		
		try(InputStream in = new FileInputStream(inFile)){
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator(',');
			MappingIterator<TweetInfo> it = mapper.readerFor(TweetInfo.class).with(schema).readValues(in);
			while (it.hasNextValue()) {
				TweetInfo tweet = it.next();
				converterService.mapTweet(tweet,dataSetName,discourseName);

				//create hash map to build relation between contribution Id and contribution content
				
				if(idContributionMap.containsKey(tweet.getText())) {
					String[] origInfo = new String[2];
					origInfo[0] = tweet.getId_str();
					origInfo[1] = tweet.getFrom_user();
					idContributionMap.get(tweet.getText()).add(origInfo);
				}
				else {
					idContributionMap.put(tweet.getText(), new ArrayList<String[]>());
					String[] origInfo = new String[2];
					origInfo[0] = tweet.getId_str();
					origInfo[1] = tweet.getFrom_user();
					idContributionMap.get(tweet.getText()).add(origInfo);
				}
			}
		}
		
		/*
		 * Phase 2:
		 * read through input file a second time and map all entity relationships
		 */
		
		try(InputStream in = new FileInputStream(inFile)){
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator(',');
			MappingIterator<TweetInfo> it = mapper.readerFor(TweetInfo.class).with(schema).readValues(in);
			while (it.hasNextValue()) {
				converterService.mapRelation(it.next(),dataSetName, idContributionMap);
			}
		}
	}	
}
