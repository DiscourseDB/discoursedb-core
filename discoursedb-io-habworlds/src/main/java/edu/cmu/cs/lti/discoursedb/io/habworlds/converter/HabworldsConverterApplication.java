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
package edu.cmu.cs.lti.discoursedb.io.habworlds.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;


@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.habworlds"})
public class HabworldsConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     HabworldsFilePath  the path of open-ended discussions csv file 
	 */

	public static void main(String[] args) {
		Assert.isTrue(args.length==3, "Usage: HabworldsConverterApplication <DiscourseName> <DataSetName> <HabworldsFilePath>");
		SpringApplication.run(HabworldsConverterApplication.class, args);
	}

}
