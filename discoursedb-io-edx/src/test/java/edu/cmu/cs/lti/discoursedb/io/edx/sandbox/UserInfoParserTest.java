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
	 * @param inFile
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
