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
package edu.cmu.cs.lti.discoursedb.io.piazza.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.io.piazza.model.PiazzaContent;


public class JsonReaderTest {

	public static void main(String[] args) throws Exception{

		try(InputStream in = new FileInputStream(new File(args[0]))) {
			
			@SuppressWarnings("unchecked")
			List<PiazzaContent> contents =(List<PiazzaContent>)new ObjectMapper()
					.enable(DeserializationFeature.UNWRAP_ROOT_VALUE)
					.enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
					.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
					.readValues(new JsonFactory().createParser(in), new TypeReference<List<PiazzaContent>>(){}).next();	
			contents.stream().forEach(c->System.out.println(c.getCreated()));
		}	
		
	}
}
