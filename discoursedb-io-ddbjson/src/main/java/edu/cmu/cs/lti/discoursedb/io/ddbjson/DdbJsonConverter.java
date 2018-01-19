package edu.cmu.cs.lti.discoursedb.io.ddbjson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.ddbjson.DdbJsonConverterService.DataSourceInfo;
import edu.cmu.cs.lti.discoursedb.io.ddbjson.DdbJsonConverterService.ImportContext;

//import lombok.extern.log4j.Log4j;

//@Log4j
@Component
public class DdbJsonConverter  implements CommandLineRunner {

	private String dataset;
	private String infile;
	private String dstype;
	
	private static final Logger log = LogManager.getLogger(DdbJsonConverter.class);
	@Autowired private DdbJsonConverterService ccs;

	@Override
	public void run(String... args) throws Exception {
		Assert.isTrue(args.length==3,"Usage: DdbJsonConverterApplication <ddbjson file> <dataset> <datassourcetype>");

		this.infile = args[0];
		this.dataset = args[1];
		this.dstype = args[2];
		log.info("Starting conversion");
		convert(this.infile, this.dataset, this.dstype);		
		log.info("Finished conversion");
	}
	
	@Autowired DdbJsonConverterService svc;
    
	
	
	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	private void log(String s) { System.out.println(s); }
	
	private void convert(String ddbJsonFile, String dataset, String dataSourceType) throws ParseException, IOException {
		
		// Read json entries sequentially. 
		//    NB if first character is {, then this will be a file full of many json items, not a valid json file
		//       but if first char is [, then it will be a single json list of items
		// For each record
		//     Switch based on table name (e.g. "table": "content")
		// 
		//    For each record with a 3-item list value:
		//       create a DSinfo record 
		//       call a function named for the table name, pulling in optionally each item from the json record
		//
		// What that function will do:
		//    Optionally, keep a map for sanity reasons, and just check that each entry is being created exactly once
		//    For the dsinfo records, it represents an index in some table. Determine the appropriate table 
		//       (if it's the ID of this record,
		//       then this table; otherwise it depends on what this is a foreign key for)
		//    For the dsinfo recs that are NOT the index of this table:
		//       long fk_value = retrieve-or-create-dummy-from-dsinfo(dsinfo, foriegn-table-name)
		//				this fills in dummy values for all fields if it doesn't exist
		//              it should create a ds record
		//    Handle the index field of the record last:
		//      For the dsinfo rec that IS the index of the table
		//         long id_value = create_by_dsinfo(dsinfo, tablename, various values)
		//               this should OPTIONALLY check that it doesn't already exist, and if it does, warn.
		//               it should link a ds record
		//      If there is no identity item for this record
		//      	   long id_value = create_by_dsinfo(NULL, tablename, various values)
		//               Just create a record and do not leave a datasource for it.  Warn that there's no DS
		// 
		
		 ImportContext context = new ImportContext(DataSourceTypes.valueOf(dataSourceType), dataset, dataset);
		 ObjectMapper mapper  = new ObjectMapper();
		 JsonFactory jsonFactory = new JsonFactory();
		 try(BufferedReader br = new BufferedReader(new FileReader(ddbJsonFile))) {
		     Iterator<JsonNode> value = mapper.readValues( jsonFactory.createParser(br), JsonNode.class);
		     value.forEachRemaining((node)->{

		    	 String tabletype = node.get("table").toString();
         		switch(tabletype.toLowerCase()) {
         			case "content": svc.addContent(node, context); break;
         			case "discourse": 
         				context.m_discourse = node.get("name").asText();
         				svc.addDiscourse(node, context);  
         				break;
         			case "discourse_part": svc.addDiscoursePart(node, context); break;
         			case "contribution": svc.addContribution(node, context); break;
         			case "contribution_partof_discourse_part": svc.addContributionPartofDiscoursePart(node, context); break;
         			case "discourse_has_discourse_part": svc.addDiscourseHasDiscoursePart(node, context); break;
         			case "discourse_part_relation": svc.addDiscoursePartRelation(node, context); break;
         			default:
         				log("TODO: Add handling for " + tabletype);
         		}
		     
		     });
		 }

	}
}