/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
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
package edu.cmu.cs.lti.discoursedb.io.habworlds.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.io.habworlds.model.HabWorldPost;


@Component
public class HabworldsConverter implements CommandLineRunner {
	
	private static final Logger logger = LogManager.getLogger(HabworldsConverter.class);
	
	@Autowired 
	HabworldsConverterService converterService;
	
	@Override
	public void run(String... args) throws ParseException {
		
		if (args.length != 3) {
			logger.error("Usage: HabworldsConverterApplication <DiscourseName> <DatasetName> </path/to/open-ended.csv>");
			return;
		}
		
		String discourseName = args[0];
		String datasetName = args[1];		
		String filePath = args[2];
		
		//check whether the given file exists
		File f = new File(filePath);
		if (!f.exists() || !f.isFile() || !f.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			return;
		}
		
		logger.info("Starting open-ended discussion data conversion");
		this.convert(discourseName, datasetName, filePath);
		logger.info("Open-ended discussion data conversion completed");
	}
	
	private void convert(String discourseName, String datasetName, String filePath) throws ParseException {
		
		try(InputStream in = new FileInputStream(filePath)) {
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator(',');
			MappingIterator<HabWorldPost> iter = mapper.readerFor(HabWorldPost.class).with(schema).readValues(in);
			while(iter.hasNextValue()) {
				HabWorldPost post = iter.next();
				converterService.mapPost(post, discourseName, datasetName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
