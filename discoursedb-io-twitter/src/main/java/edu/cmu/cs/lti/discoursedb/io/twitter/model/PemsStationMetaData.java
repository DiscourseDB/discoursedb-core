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
