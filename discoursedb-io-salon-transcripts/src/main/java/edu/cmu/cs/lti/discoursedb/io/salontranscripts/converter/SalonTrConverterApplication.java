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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;

import edu.cmu.cs.lti.discoursedb.io.salontranscripts.converter.SalonTrConverterApplication;

@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.salontranscripts"})
public class SalonTrConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the discourse
	 */

	public static void main(String[] args) {
		Assert.isTrue(args.length == 3, "Usage: Usage: SalonConverterApplication <DiscourseName> <TranscriptDirectory> <Year class took place>");
		SpringApplication.run(SalonTrConverterApplication.class, args);
	}
	
}