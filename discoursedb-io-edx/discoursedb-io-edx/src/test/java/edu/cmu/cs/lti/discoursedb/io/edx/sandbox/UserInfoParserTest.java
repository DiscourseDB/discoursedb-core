package edu.cmu.cs.lti.discoursedb.io.edx.sandbox;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.io.edx.forum.model.UserInfo;

public class UserInfoParserTest {

	public static void main(String[] args) throws Exception {
		new UserInfoParserTest().convert("src/test/resources/userinfo.tsv");
	}

	/**
	 * Stream-reads the json input and binds each post in the dataset to an
	 * object that is passed on to the mapper.
	 * 
	 * @param filename
	 *            of json file that contains forum data
	 * @throws IOException
	 *             in case the inFile could not be read
	 * @throws JsonParseException
	 *             in case the json was malformed and couln't be parsed
	 */
	public void convert(String inFile) throws JsonParseException, JsonProcessingException, IOException {
		final InputStream in = new FileInputStream(inFile);
		try {
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaFor(UserInfo.class).withColumnSeparator('\t').withHeader();
			MappingIterator<UserInfo> it = mapper.readerFor(UserInfo.class).with(schema).readValues(in);
			while (it.hasNextValue()) {
				UserInfo curPost = it.next();
				map(curPost);
			}
		} finally {
			in.close();
		}
	}

	/**
	 * Maps a post to DiscourseDB entities.
	 * 
	 * @param u
	 *            the post object to map to DiscourseDB
	 */
	public void map(UserInfo u) {
		System.out.println(u.getEmail());
		if (!u.getFirst_name().isEmpty()) {
			if (!u.getLast_name().isEmpty()) {
				System.out.println(u.getFirst_name() + " " + u.getLast_name());
			} else {
				System.out.println(u.getFirst_name());
			}
		} else {
			if (!u.getLast_name().isEmpty()) {
				System.out.println(u.getLast_name());
			}
		}
		System.out.println(u.getCountry());
	}
}
