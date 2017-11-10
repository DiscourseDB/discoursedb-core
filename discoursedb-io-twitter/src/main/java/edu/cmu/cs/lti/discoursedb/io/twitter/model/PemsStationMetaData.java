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
package edu.cmu.cs.lti.discoursedb.io.twitter.model;

import org.bson.Document;

import lombok.Data;

@Data
public class PemsStationMetaData {

	private long stationId;
	private double longitude;
	private double latitdue;
	private String stationName;

   /****************************
	* More meta data is available
	*	
	*   "_id" : ObjectId("55672dc770892621ac0a5a5f"),
	*	"county" : 37,
	*	"direction" : "S",
	*	"name" : "PHOEBE",
	*	"district" : 7,
	*	"city" : "40032",
	*	"record_date" : ISODate("2015-01-14T00:00:00Z"),
	*	"lane_type" : "ML",
	*	"location" : {
	*		"type" : "Point",
	*		"coordinates" : [
	*			-118.021787,
	*			33.880183
	*		]
	*	},
	*	"num_lanes" : 3,
	*	"station_id" : 715898,
	*	"freeway" : 5,
	*	"length" : 0.43,
	*	"user_id" : [
	*		"2029",
	*		null,
	*		null,
	*		null
	*	],
	*	"abs_pm" : 117.28,
	*	"state_pm" : 0.71
	************************************/
	public PemsStationMetaData(Document metaData){
		setStationId(metaData.getLong("station_id"));
		setStationName(metaData.getString("name"));
		double[] coordinates = (double[])((Document)metaData.get("location")).get("coordinates");
		setLatitdue(coordinates[0]);
		setLongitude(coordinates[1]);
	}

	
}
