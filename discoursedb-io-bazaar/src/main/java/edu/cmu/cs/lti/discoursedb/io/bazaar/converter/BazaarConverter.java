package edu.cmu.cs.lti.discoursedb.io.bazaar.converter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Message;
import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Room;

/**
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Component
public class BazaarConverter implements CommandLineRunner {
	
	private static final Logger logger = LogManager.getLogger(BazaarConverter.class);
	private String dataSetName;
	private String discourseName;
	@Autowired 
	BazaarConverterService converterService;
	
	@Override
	public void run(String... args) throws Exception {
		if (args.length < 4) {
			logger.error("Usage: BazaarConverterApplication <DataSourceType> <DataSetName> </path/to/*-prod.mongo>");
			System.exit(1);
		}
		this.dataSetName = args[0];
		this.discourseName = args[1];
		String messageFileDir = args[2];
		String roomFileDir = args[3];

		File messageFile = new File(messageFileDir);
		File roomFile = new File(roomFileDir);
		if (!messageFile.exists() || !messageFile.isFile() || !messageFile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);
		}
		
		if (!roomFile.exists() || !roomFile.isFile() || !roomFile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);
		}

		logger.info("Starting bazaar conversion");
		convert(messageFileDir, roomFileDir);
	}

	private void convert(String messageFileDir, String roomFileDir) throws ParseException, IOException {
		
		HashMap<String, String> roomIdNameMap = new HashMap<String,String>();
		
		String tempFileDir = "src/resource/new_message.csv";
		
		//read through input message file once and remove write a new file.
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(messageFileDir)), "utf-8"));
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tempFileDir,true), "utf-8"));
		String lineString;
		while((lineString=br.readLine())!=null) {
			if(lineString.contains("\\\"We're Ready\\\"")) {
				lineString = lineString.replaceAll("\"We're Ready\\\\\"", "We're Ready\\\\");
				//System.out.println(lineString);
			}
			if(lineString.contains("\\\"ready\\\"")) {
				lineString = lineString.replaceAll("\\\\\"ready\\\\\"", "\\\\ready\\\\");
				//System.out.println(lineString);
			}
			if(lineString.contains("\\\"VirtualCarolyn\\\""))
				lineString = lineString.replaceAll("\\\\\"VirtualCarolyn\\\\\"", "\\\\VirtualCarolyn\\\\");
			bw.write(lineString);
			bw.write("\n");
		}
		br.close();
		bw.flush();
		bw.close();
		
		//Phase 1: read through input room file once and map all entities
		try(InputStream in = new FileInputStream(roomFileDir)) {		
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator(',');
			MappingIterator<Room> rIter = mapper.readerFor(Room.class).with(schema).readValues(in);
			while (rIter.hasNextValue()) {
				Room r = rIter.next();
				if(!roomIdNameMap.containsKey(r.getId()))
					roomIdNameMap.put(r.getId(), r.getName());
				converterService.mapRoom(r, dataSetName, discourseName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Phase 2: read through input message file and map relationships between room and message
		try(InputStream in = new FileInputStream("src/resource/new_message.csv")) {
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator(',');
			MappingIterator<Message> mIter = mapper.readerFor(Message.class).with(schema).readValues(in);
			int q = 0;
			while (mIter.hasNextValue()) {
				/*
				lineString = br.readLine();
				if(lineString.contains("\\\"We're Ready\\\""))
					lineString.replace("\\\"We're Ready\\\"", "\\We're Ready\\");
				if(lineString.contains("\\\"ready\\\""))
					lineString.replace("\\\"ready\\\"", "\\ready\\");
					*/
				System.out.println(q++);
				Message m = mIter.next();
				System.out.println(m.getContent());
				if(m.getType().equals("text")||m.getType().equals("image")||m.getType().equals("private"))
					converterService.mapMessage(m, dataSetName, discourseName, roomIdNameMap);
				else
					converterService.mapInteraction(m, dataSetName, discourseName, roomIdNameMap);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
}
