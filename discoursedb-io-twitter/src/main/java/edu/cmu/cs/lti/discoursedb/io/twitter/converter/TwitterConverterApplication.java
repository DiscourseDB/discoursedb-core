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
package edu.cmu.cs.lti.discoursedb.io.twitter.converter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.util.Assert;


@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.twitter"})
public class TwitterConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     MongoDbHost		the hostname of the mongo db server
	 *     MongoDatabaseName  the name of the mongo database
	 *     MongoCollectionName  the name of the collection that contains the tweets
	 */

	public static void main(String[] args) {
		Assert.isTrue(args.length == 5 || (args.length >= 6 && args.length <=8), "Usage: TwitterConverterApplication <DiscourseName> <DataSetName> <MongoDbHost> <MongoTwitterDatabaseName> <MongoTwitterCollectionName> [<StationsFilePath> <PemsMetaMongoDataDatabaseName> <PemsMetaDataMongoCollectionName> optional]");
		SpringApplication.run(TwitterConverterApplication.class, args);
	}

}
