package edu.cmu.cs.lti.discoursedb.io.bazaar;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Message;

public class Test {

	public static void main(String[] args) {
		
//		String messageFileDir = "/Users/haitian/Documents/CMU/Courses/DirectedStudy/data/message.csv";
//		try(InputStream in = new FileInputStream(messageFileDir)) {
//			int i=0;
//			CsvMapper mapper = new CsvMapper();
//			CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator(',');
//			MappingIterator<Message> mIter = mapper.readerFor(Message.class).with(schema).readValues(in);
//			while (mIter.hasNextValue()) {
//				System.out.println(i++);
//				Message m = mIter.next();
//				System.out.println(m.getContent());
//				/*
//				if(m.getType().equals("text")||m.getType().equals("image")||m.getType().equals("private"))
//					converterService.mapMessage(m, dataSetName, discourseName, roomIdNameMap);
//				else
//					converterService.mapInteraction(m, dataSetName, discourseName, roomIdNameMap);
//				*/
//			}
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		/*
		String test = "\"we are ready\"";
		System.out.println(test.replace("we", ""));
		*/

	}

}
