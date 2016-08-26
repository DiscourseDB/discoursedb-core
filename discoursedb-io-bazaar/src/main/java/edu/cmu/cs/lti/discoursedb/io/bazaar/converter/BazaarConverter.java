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
package edu.cmu.cs.lti.discoursedb.io.bazaar.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Message;
import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Room;
import lombok.extern.log4j.Log4j;

/**
 * Converter for bazaar chatlogs.
 * 
 * Usage: BazaarConverterApplication <Dataset Name> <Discourse name> <chat message log> <chat room log> <agent name>\n A common value for the agent name is VirtualCarolyn. 
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Log4j
@Component
public class BazaarConverter implements CommandLineRunner {

	private String dataSetName;
	private String discourseName;
	@Autowired private BazaarConverterService converterService;

	@Override
	public void run(String... args) throws Exception {
		Assert.isTrue(args.length==5,"Usage: BazaarConverterApplication <Dataset Name> <Discourse name> <chat message log> <chat room log> <agent name>\n A common value for the agent name is VirtualCarolyn.");

		this.dataSetName = args[0];
		this.discourseName = args[1];
		String messageFileDir = args[2];
		String roomFileDir = args[3];
		String agentname = args[4];
		
		File messageFile = new File(messageFileDir);
		File roomFile = new File(roomFileDir);
		if (!messageFile.exists() || !messageFile.isFile() || !messageFile.canRead()) {
			log.error("Input file does not exist or is not readable.");
			return;
		}

		if (!roomFile.exists() || !roomFile.isFile() || !roomFile.canRead()) {
			log.error("Input file does not exist or is not readable.");
			return;
		}

		log.info("Starting bazaar conversion ("+messageFileDir+" ; "+roomFileDir+" ; "+agentname+")");
		convert(messageFileDir, roomFileDir, agentname);
		log.info("Finished bazaar conversion");
	}

	private void convert(String messageFileDir, String roomFileDir, String agentname) throws ParseException, IOException {

		Map<String, String> roomIdNameMap = new HashMap<>();
		List<String> messages = new ArrayList<>();
		
		//Read input file and preprocess
		String lineFragment = null;
		for(String line:FileUtils.readLines(new File(messageFileDir))){
			//line fragments occur in case we have line feeds in a column
			if(lineFragment!=null){
				line=lineFragment+line;
				lineFragment=null;
			}
			if (line.endsWith("\\")||line.endsWith("\\\r\f")){
				line = line.replaceAll("\\\r\f", "");
				lineFragment = line;
			}else{
				if (line.contains("\\\"We're Ready\\\"")) {
					line = line.replaceAll("\"We're Ready\\\\\"", "We're Ready\\\\");
				}
				if (line.contains("\\\"ready\\\"")) {
					line = line.replaceAll("\\\\\"ready\\\\\"", "\\\\ready\\\\");
				}
				if (line.contains("\\\""+agentname+"\\\"")){
					line = line.replaceAll("\\\\\""+agentname+"\\\\\"", "\\\\"+agentname+"\\\\");
				}
				messages.add(line);						
			}
		}

		// Phase 1: read through input room file once and map all entities
		try (InputStream in = new FileInputStream(roomFileDir)) {
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaFor(Room.class).withColumnSeparator(',');
			MappingIterator<Room> rIter = mapper.readerFor(Room.class).with(schema).readValues(in);
			while (rIter.hasNextValue()) {
				Room r = rIter.next();
				if (!roomIdNameMap.containsKey(r.getId()))
					roomIdNameMap.put(r.getId(), r.getName());
				converterService.mapRoom(r, dataSetName, discourseName);
			}
		} catch (IOException e) {
			log.error("Error reading room file",e);
		}

		// Phase 2: read through input message file and map relationships between room and message
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaFor(Message.class).withColumnSeparator(',');
			for(String message:messages){
				Message m = mapper.readerFor(Message.class).with(schema).readValue(message);
				if (m.getType().equals("text") || m.getType().equals("image") || m.getType().equals("private")){
					converterService.mapMessage(m, dataSetName, discourseName, roomIdNameMap);				
				}else{
					converterService.mapInteraction(m, dataSetName, discourseName, roomIdNameMap);					
				}
			}
	}

}