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
package edu.cmu.cs.lti.discoursedb.io.mturk.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.google.common.collect.Table;

import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import lombok.extern.log4j.Log4j;

/**
 * Converter for bazaar chatlogs.
 * 
 * Usage: BazaarConverterApplication <Dataset Name> <Discourse name> <chat message log> <chat room log> <agent name>\n A common value for the agent name is VirtualCarolyn. 
 * 
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Log4j
@Component
public class MturkConverter implements CommandLineRunner {

	private String directory;
	private String dataset;
	String xuDiscourseName = "Xu Study";
	String wenDiscourseName = "Wen Study";
	
	@Autowired private MturkConverterService mcs;

	@Override
	public void run(String... args) throws Exception {
		Assert.isTrue(args.length==2,"Usage: MturkConverterApplication <directory> <dataset>.");

		// This is specific enough to a single workshop
		// that I'll just hardcode stuff -- it won't be reused likely
		
		this.directory = args[0];
		this.dataset = args[1];
		log.info("Starting mturk conversion");
		convert(this.directory, this.dataset);		
		log.info("Finished mturk conversion");
	}
	
	private CsvSchema.Builder mkCsvSchema(String fieldlist) {
		CsvSchema.Builder builder = new CsvSchema.Builder();
		for (String colname : fieldlist.split(",")) {
			builder.addColumn(colname);
		}
		builder.setColumnSeparator(',');
		builder.setUseHeader(false);
		return builder;
	}
	
	private Iterable<Map<String,String>> csvIteratorNoHeaders(String filename, String fieldlist) throws JsonProcessingException, IOException {
		InputStream in = new FileInputStream(filename);
        MappingIterator<Map<String, String>> iterator = new CsvMapper()
                .readerFor(Map.class)
                .with(mkCsvSchema(fieldlist).build())
                .readValues(in);
        return () -> iterator;
	}
	
	private Iterable<Map<String,String>> csvIteratorExistingHeaders(String filename) throws JsonProcessingException, IOException {
		//InputStream in = new FileInputStream(filename, "UTF-8");
		InputStreamReader in = new InputStreamReader(new FileInputStream(filename), "ISO-8859-1");
        MappingIterator<Map<String, String>> iterator = new CsvMapper()
                .readerFor(Map.class)
                .with(CsvSchema.emptySchema().withColumnSeparator(',').withHeader())
                .readValues(in);
        return () -> iterator;
	}

	public static
	<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
	  List<T> list = new ArrayList<T>(c);
	  java.util.Collections.sort(list);
	  return list;
	}
	
	private void convert(String directory, String datasetName) throws ParseException, IOException {
		
		// xu_end_id is string: group + _ team + _ + id
		// username is group:username
		Map<String,String> xu_id2username = new HashMap<String,String>();
		Map<String,String> wen_username2groupteam = new HashMap<String,String>();
		Map<String,Long> ddb_user_ids = new HashMap<String,Long>();
		Map<String,String> discforum_id2username = new HashMap<String,String>();
		Map<String,String> username2group = new HashMap<String,String>();
		Map<String,String> username2team = new HashMap<String,String>();
		Map<String,String> username2experiment = new HashMap<String,String>();
		
		Pattern forum_team_user0 = Pattern.compile("(\\d\\d\\d)_(\\d+)_(\\d+)");
		Matcher m11 = forum_team_user0.matcher("234pre234_2_3.csv");
		m11.find();
		assert m11.group(1) == "234";
		System.out.println("Success!");
		/* 
			* Read xu and wen's users -> write users, 
			    * keep map xu_user_id -> username, group, team, experiment;  username->xu_user_id,group, team, experiment; also username -> discoursedb_userid
			    * write group entities for all three levels and link to users
			    * write DPs for group, team, experiment, and link them (dprs) */
		
		/* userid,groupid,group,newuserid,username,id,trans,bazaar,teamid,rolereasoning,
		 * chattrans,score,tradeoff,discussiontran,totalprelen,reasoning,chatlength,
		 * numofwords,reasoning_percentage,bazaarprompt,tranpercentage,pre_epi_rt,
		 * pre_epi_wr,pre_tf_rt,pre_tf_wr,post_epi_rt,post_epi_wr,post_tf_rt,post_tf_wr
		 * 
		 * 1,222_1,222,222_1,Bobs,222_1_1,1,1,mturk987641,4,10,23...
		 */
		for (Map<String,String> row : csvIteratorExistingHeaders(directory + "/xustudy/individualdata_0622.csv")) {
			String group = row.get("id").split("_")[0];
			String team = row.get("id").split("_")[1];
			String username = group + ":" + row.get("username");
			xu_id2username.put(row.get("id"), username);

			ddb_user_ids.put(username,  
					mcs.mapUser(username, xuDiscourseName, datasetName,
							"individualdata_0622", "id", row.get("id")));
			username2group.put(username,  group);
			username2team.put(username, team);
			username2experiment.put(username,  xuDiscourseName);
			mcs.mapTeamAndGroup(xuDiscourseName, group, team,
					datasetName, "individualdata_0622", "id", row.get("id"));
		}
		
		/*
		 * userid,assign_groupsize,groupid,totalpost,cothreadwithteammates,gotreplyfromteammate,replytoteammate,initialproposal,team_score,team_energy_requirement,team_energy_energy,team_additional,team_incorrect,score,energy_requirement,energy_energy,additional,incorrect,cntwords,cntchats,Experience,End_Result,Communication_Quality,Topic_Familiarity,Perceived_Learning,type,energy
		 * 
		 * Amy,3,ff1,6,0,0,0,226,2,2,0,0,0,0,0,0,0,0,0,0,5,5,5,2,5,community-early,1
		 *
		for (Map<String,String> row : csvIteratorExistingHeaders(directory + "/wenstudy/exp1-ANOVA-peruser.csv")) {
			String group = row.get("groupid").substring(0, row.get("groupid").length()-1);
			String username = group + ":" + row.get("userid");
			wen_username2groupteam.put(username, row.get("groupid"));
			username2group.put(username,  group);
			String team = row.get("groupid");
			username2team.put(username, team);
			username2experiment.put(username,  wenDiscourseName);

			ddb_user_ids.put(username,  
					mcs.mapUser(username, wenDiscourseName, datasetName,
							"exp1-ANOVA-peruser", "userid", row.get("userid")));
							//, team, group, "xustudy"));
			System.out.println(row.get("userid") + " --> " + row.get("groupid"));
			mcs.mapTeamAndGroup(wenDiscourseName, group, team,
					datasetName, "individualdata_0622", "groupid", row.get("groupid"));
			
		}*/
		
		
		
		/*
		Table<R, C, V> t2 = csvscan("wenfiles/exp1_sdmething.csv", new Array<String>(flds));
		for (t in t2.rows()) {
			ddb_user_ids[t.get("user_name")] = 
					mcs.addUser(t.get("userxxxxxid"), t.get("user_name"),
							group, team, experiment); // also fields here
		}
		
		//* Read users.csv -> keep discussion_id in memory; don't write
		 * "user_uid","user_name","user_pwd","forum_uid","uuid","access_enabled","password_reset"
		 * 
		 * "1","erose","innerpath","1","CF5725C5-B089-CC1F-509F4E3E9BE24881","1",""
		 * "2603","Amber","Amber64","64","Amber","1",""
		 * "173","64","64","1","64","1",""
		 */
		for (Map<String,String> row : csvIteratorExistingHeaders(directory + "/user.csv")) {
			discforum_id2username.put(row.get("user_uid"), 
							row.get("forum_uid") + ":" + row.get("user_name"));
		}

		
		/* discussionforum.csv 
		 * post_uid,forum_uid,thread_uid,replyto_uid,user_uid,subject,content,
		 * 			posted_at,uuid
		 * 
		 * 15,1,7,0,3,"If Oil is Scarce, Why's It So Damn Cheap?","My question ...",
		 * 		4/4/15 20:20,4584DA50-EDFC-B74D-EEAAA78C8CF4F2DC
		 */
		Map<Long,Long> sourceDiscId2ddbDiscId = new HashMap<Long,Long>();
		for (Map<String,String> row : csvIteratorExistingHeaders(directory + "/discussionforum.csv")) {
			//if (true) break;
			String username = discforum_id2username.getOrDefault(row.get("user_uid"), row.get("forum_uid") + ":User" + row.get("user_uid"));
			
			//TODO: get group, team, experiment for each user
			
			String thisDiscourse = username2experiment.getOrDefault(username, "discussionforum");
			
			if (!ddb_user_ids.containsKey(username)) {
				ddb_user_ids.put(username,  
					mcs.mapUser(username, thisDiscourse, datasetName,
							"discussionforum", "post_uid(User)", row.get("post_uid")));
			}
			Long post_uid = Long.valueOf(row.get("post_uid"));
			System.out.println("Mapping post " + row.get("post_uid") + " by user " + username + " aka " + sourceDiscId2ddbDiscId.getOrDefault(Long.valueOf(row.get("user_uid")), 0L));
			
			Long post_ddbid = mcs.mapDiscussionPost(
							row.get("subject"),
							row.get("content"),
							row.get("forum_uid"),
							row.get("thread_uid"), username2group.get(username), username2team.get(username),
							ddb_user_ids.getOrDefault(username,0L),
							row.get("posted_at"),
							Long.valueOf(row.get("replyto_uid")),
							thisDiscourse,
							datasetName,
							"discussionforum",
							"post_uid",
							row.get("post_uid")
							);
			sourceDiscId2ddbDiscId.put(post_uid,post_ddbid); 
		}
		
		
		/*
		 * forumid, offset, forumname
		 * 218,10510,92722
		 * 222,10810,98764
		 * 224,11010,79865
		 */
		Map<String,String> xu_forumname2forum = new HashMap<String,String>();
		for (Map<String,String> row : csvIteratorExistingHeaders(directory + "/xustudy/newmapping.csv")) {
			xu_forumname2forum.put(row.get("forumname"), row.get("forumid"));
		}
		
		File[] listOfFiles = new File(directory + "/xustudy/chatlogs_transactivity_annotated/").listFiles();

		for (File file : listOfFiles) {
		    if (file.isFile() && file.getName().endsWith(".csv")) {
				//if (true) break;
		    		String n = file.getName();
		    		String forum_id = "", team_id = "";
		    		if (n.startsWith("mturkno")) {
		    			forum_id = n.substring(7, n.length() - 5);
		    		} else if (n.startsWith("mturk")) {
		    			forum_id = n.substring(5, n.length() - 5);
		    		}
		    		forum_id = xu_forumname2forum.getOrDefault(forum_id, "0");
		    		team_id = n.substring(n.length() - 5, n.length()-4);
		    		if (!forum_id.equals("0")) {
		    			int lineno = 0;
		    			if (n.startsWith("mturkno")) {
		    				/* ,type,username,useraddress,userid,timestamp,roomname,content,neg,
		    				 * 1,presence,BazaarAgent,128.2.220.133:35582,N,6/4/16 21:24,mturkno798238,join,bazaar,
		    				 */
		    				for (Map<String,String> row : csvIteratorExistingHeaders(file.getAbsolutePath())) {
		    					if (row.get("type") == "presence") {
		    						//mcs.mapChatInteraction(row.get("timestamp") + ":00", forum_id + ":" + row.get("username"), forum_id, team_id, row.get("content"),
		    						//		xuDiscourseName, datasetName, "chats/" + file.getName(), "lineno", lineno);
		    					} else if (row.get("username") != null && row.get("username").length() > 0) {
		    						mcs.mapChat(row.get("timestamp") + ":00", forum_id + ":" + row.get("username"), forum_id, team_id, row.get("content"),
		    								xuDiscourseName, datasetName, "chats/" + file.getName(), "lineno", Long.toString(lineno));
		    					}
		    					lineno += 1;
		    				}
		    			} else {
		    				/*
		    				 * 7/11/16,20:53:59,0,Andy,1.46828E+12,Hi,neg,neg,,,
		    				 * 7/11/16,20:54:07,0,UKCats,1.46828E+12,Hi all,neg,neg,,,
		    				 */
		    				System.out.println("Trying to scan " + file.getAbsolutePath());
		    				for (Map<String,String> row : csvIteratorNoHeaders(file.getAbsolutePath(), "date,time,zero,username,number,content,fld1,fld2,fld3,fld4,fld5,ign1,ign2,ign3,ign4,ign5,ign6")) {
		    					if (row.get("username") != null && row.get("username").length() > 0) {
		    						mcs.mapChat(row.get("date") + " " + row.get("time"), forum_id + ":" + row.get("username"), forum_id, team_id, row.get("content"),
		    							xuDiscourseName, datasetName, "chats/" + file.getName(), "lineno", Long.toString(lineno));
		    					}
							lineno += 1;
		    				}
		    				
		    			}
		    				
					
		    		} else {
		    			System.out.println("Chat session " + file.getName() + " can't be identified");
		    		}
		    }
		}
		
		
		/*HOW:
		
			* Read xu/userid-namemap -> to get username to userid-within-group
			* Read xu/newmapping -> to get forumid -> groupname
			* Read xu/chatlogs ->
			    * add dp for each file, link to experiment, group, team
			    * for each posting add user, text, date; link to dp
			    * */
		System.out.println("Doing pre/post tests");
		Pattern forum_team_user = Pattern.compile("(\\d\\d\\d)_(\\d+)_(\\d+)");
		Matcher m1 = forum_team_user.matcher("234_2_3");
		m1.find();
		assert m1.group(1) == "234";
		Iterator<File> it =  FileUtils.iterateFiles(new File(directory + "/xustudy/preposttest"), null, true);
		while (it.hasNext()) {
			File test = it.next();
		    if (test.isFile() && test.getName().endsWith(".csv")) {
	    			System.out.println("Doing test " + test.getName());
		    		String n = test.getName();
		    		Matcher m = forum_team_user.matcher(n);
		    		if (m.find()) {
		    			String forum_id = m.group(1);
		    			String team_id = m.group(2);
		    			String user_id = m.group(3);
		    			String testtype = "Pretest";
		    			if (n.contains("post")) {
		    				testtype ="Posttest";
		    			}
		    			String content = FileUtils.readFileToString(test);
		    			content = content.substring(content.indexOf("\n"));  // skip first line, which is a false csv header
		    			String xu_id = forum_id + "_" + team_id + "_" + user_id;
			    		String username = xu_id2username.get(xu_id);
			    		System.out.println("Scanning " + testtype + " " + n + " by " + username + " on team " + forum_id + "_" + team_id);
			    		mcs.mapFile(forum_id, team_id, username, testtype + " by " + username, testtype.equals("Posttest")?ContributionTypes.POSTTEST:ContributionTypes.PRETEST,
			    				content, xuDiscourseName, datasetName, "preposttests", "for_user", xu_id);
		    		}
		    }
		}
		
		
		System.out.println("Doing proposals");
		Iterable<File> it2 =  () -> FileUtils.iterateFiles(new File(directory + "/xustudy/group_proposals_txt/"), null, false);
		for (File prop : it2) {
			//if (true) break;
		    if (prop.isFile() && prop.getName().endsWith(".txt")) {
		    		System.out.println("Doing proposal " + prop.getName());
				String n = prop.getName();
		    		String forum_id = "", team_id = "";
		    		forum_id = n.substring(0, n.length() - 5);
		    		team_id = n.substring(n.length()-5,n.length()-4);
		    		forum_id = xu_forumname2forum.getOrDefault(forum_id, "0");
		    		System.out.println("Scanning proposal " + n + " by " + forum_id + "_" + team_id);
		    		String content = FileUtils.readFileToString(prop);
		    		mcs.mapFile(forum_id, team_id, forum_id + "_" + team_id, "Proposal by " + forum_id + "_" + team_id, ContributionTypes.PROPOSAL,
		    				content, xuDiscourseName, datasetName, "proposals", "for_team", forum_id + "_" + team_id);   		
		    }
		}
		
		/*
			* read  xu/preposttest files ->
			    * look up author from 
			    * create dp for pre/post+teamname, dpr link to team, group, experiment from filename
			    * import as contribution/content; ignore first line
			* read  xu/proposal files
			    * Leave author blank; place directly as posting under team's dp
			* read wen/chats
			    * make dp for each file: wen team ff1 chat, link to team, group, experiment
			    * put contribution from each one.  Number sequentially from 1972-01-01 incrementing by one minute each through the whole set of files.
			* read wen/proposals
			    * leave author blank; place directly as posting under team's dp.  title="wen team ff1 proposal"

			Mechanism:
			* thingy to read in a csv file with or without a fixed set of fields
			* thingy to coalesce a whole csv column into a single string with carriage returns
			* thingy to store a map
			* thingy to write elements and store the discoursedb-indexes to a map
			
		Map<String, String> roomIdNameMap = new HashMap<>();
		List<String> messages = new ArrayList<>();
		
		//Read input file and preprocess
		String lineFragment = null;
		for(String line:FileUtils.readLines(new File(messageFileDir))){
			//line fragments occur in case we have line feeds in a column
			if(lineFragment!=null){
				line=lineFragment+line;
				lineFragment=null;
			}
			if (line.endsWith("\\")||line.endsWith("\\\r\f")){
				line = line.replaceAll("\\\r\f", "");
				lineFragment = line;
			}else{
				if (line.contains("\\\"We're Ready\\\"")) {
					line = line.replaceAll("\"We're Ready\\\\\"", "We're Ready\\\\");
				}
				if (line.contains("\\\"ready\\\"")) {
					line = line.replaceAll("\\\\\"ready\\\\\"", "\\\\ready\\\\");
				}
				if (line.contains("\\\""+agentname+"\\\"")){
					line = line.replaceAll("\\\\\""+agentname+"\\\\\"", "\\\\"+agentname+"\\\\");
				}
				messages.add(line);						
			}
		}

		// Phase 1: read through input room file once and map all entities
		try (InputStream in = new FileInputStream(roomFileDir)) {
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaFor(Room.class).withColumnSeparator(',');
			MappingIterator<Room> rIter = mapper.readerFor(Room.class).with(schema).readValues(in);
			while (rIter.hasNextValue()) {
				Room r = rIter.next();
				if (!roomIdNameMap.containsKey(r.getId()))
					roomIdNameMap.put(r.getId(), r.getName());
				converterService.mapRoom(r, dataSetName, discourseName);
			}
		} catch (IOException e) {
			log.error("Error reading room file",e);
		}

		// Phase 2: read through input message file and map relationships between room and message
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaFor(Message.class).withColumnSeparator(',');
			for(String message:messages){
				Message m = mapper.readerFor(Message.class).with(schema).readValue(message);
				if (m.getType().equals("text") || m.getType().equals("image") || m.getType().equals("private")){
					converterService.mapMessage(m, dataSetName, discourseName, roomIdNameMap);				
				}else{
					converterService.mapInteraction(m, dataSetName, discourseName, roomIdNameMap);					
				}
			}
			*/
	}

}