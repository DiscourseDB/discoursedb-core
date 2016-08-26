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
package edu.cmu.cs.lti.discoursedb.io.neuwirth.converter;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;


@Component
public class NeuwirthConverter implements CommandLineRunner{
		
	@Autowired private NeuwirthConverterService converterService;
	
	@Override
	public void run(String... args) throws IOException, ParseException {		
		//validate input
		Assert.isTrue(args.length==2,"Usage: NeuwirthConverterApplication <DatasetName> </path/to/datafolder>");
		String dataSetName = args[0];
		String folderPath = args[1];		
		File folder = new File(folderPath);
		Assert.isTrue(folder.isDirectory(),"Input file does not exist or is not readable.");
		
		//start processing
		Arrays.stream(folder.listFiles()).forEach(file-> converterService.mapFile(dataSetName, file));
	}
		
}