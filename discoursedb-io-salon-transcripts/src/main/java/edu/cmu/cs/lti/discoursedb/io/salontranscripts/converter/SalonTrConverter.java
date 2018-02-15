/*******************************************************************************
 * Copyright (C)  2015 - 2017  Carnegie Mellon University
 * Author: Oliver Ferschke, Chris Bogart
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
package edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.BulkImporterService.MissingParameterException;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.SalonTrConverter;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.SalonTrConverterService;
import edu.cmu.cs.lti.discoursedb.io.salontranscripts.models.SalonTranscript;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import lombok.extern.log4j.Log4j;

@Log4j
@Component

public class SalonTrConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(SalonTrConverter.class);
	@Autowired SalonTrConverterService converterService;
	@Autowired Environment env;
	
	public String discourseName;
	public String directory;
	public int year;
	
	@Override
	public void run(String... args) throws ParseException, SQLException {
		Assert.isTrue(args.length == 3,
				"Usage: SalonTrConverterApplication <DiscourseName> <TranscriptDirectory> <Year class took place>");

		discourseName = args[0];
		directory = args[1];
		year = Integer.parseInt(args[2]);
		
		// start data import
		
		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");

		File folder = new File(directory);
		logger.info("Reading folder " + folder.getAbsolutePath());
		File[] listOfFiles = folder.listFiles();

		for (File f : listOfFiles) {
			if (f.getName().endsWith(".txt")) {
				logger.info("Reading file " + f.getAbsolutePath());
			    SalonTranscript transcript;
				try {
					transcript = new SalonTranscript(f);
					transcript.adjustYear(year);
				    converterService.importTranscript(transcript, discourseName);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (java.text.ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MissingParameterException e) {
					// TODO Auto-generated catch block
					logger.info("Failed to import transcript: " + e.getMessage());
					e.printStackTrace();
				}
			}
			
		}
		
		logger.info("Done reading files");
	
			
	}

}
