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
package edu.cmu.cs.lti.discoursedb.io.prosolo.blog.converter;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.io.prosolo.blog.model.ProsoloBlogPost;

/**
 * 
 * @author Oliver Ferschke
 *
 */
@Component
public class BlogConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(BlogConverter.class);	
	private static final String MAPPING_SEPARATOR = ",";	
	private boolean dumpWrappedInJsonArray = false;
	private Map<String,String> blogToedxMap = new HashMap<>();
	
	@Autowired private DataSourceService dataSourceService;
	@Autowired private BlogConverterService converterService;

	@Override
	public void run(String... args) throws Exception {
		if(args.length<3){
			logger.error("USAGE: BlogConverterApplication <DiscourseName> <DataSetName> <blogDump> <userMapping (optional)> <dumpIsWrappedInJsonArray (optional, default=false)>");
			throw new RuntimeException("Incorrect number of launch parameters.");

		}
		final String discourseName=args[0];		

		final String dataSetName=args[1];		
		if(dataSourceService.findDataset(dataSetName) != null){
			logger.warn("Dataset "+dataSetName+" has already been imported into DiscourseDB. Terminating...");			
			return;
		}
		
		final String forumDumpFileName = args[2];
		File blogDumpFile = new File(forumDumpFileName);
		if (!blogDumpFile.exists() || !blogDumpFile.isFile() || !blogDumpFile.canRead()) {
			logger.error("Forum dump file does not exist or is not readable.");
			throw new RuntimeException("Can't read file "+forumDumpFileName);
		}


		//parse the optional fourth and fifth parameter
		String userMappingFileName=null;
		String jsonarray=null;
		if(args.length==4){
			if(args[3].equalsIgnoreCase("true")||args[3].equalsIgnoreCase("false")){
				jsonarray=args[3];
			}else{
				userMappingFileName=args[3];
			}
		}else{
			if(args[3].equalsIgnoreCase("true")||args[3].equalsIgnoreCase("false")){
				jsonarray=args[3];
				userMappingFileName=args[4];
			}else{
				jsonarray=args[4];
				userMappingFileName=args[3];
			}			
		}
		
		//read the blog author to edX user mapping, if available
		if(userMappingFileName!=null){
			logger.trace("Reading user mapping from "+userMappingFileName);
			File userMappingFile = new File(userMappingFileName);
			if (!userMappingFile.exists() || !userMappingFile.isFile() || !userMappingFile.canRead()) {
				logger.error("User mappiong file does not exist or is not readable.");
				throw new RuntimeException("Can't read file "+userMappingFileName);
			}
			List<String> lines = FileUtils.readLines(userMappingFile);
			lines.remove(0); //remove header
			for(String line:lines){
				String[] blogToedx = line.split(MAPPING_SEPARATOR);
				//if the current line contained a valid mapping, add it to the map
				if(blogToedx.length==2&&blogToedx[0]!=null&&!blogToedx[0].isEmpty()&&blogToedx[1]!=null&&!blogToedx[1].isEmpty()){
					blogToedxMap.put(blogToedx[0], blogToedx[1]);					
				}
			}
		}
				
		if(jsonarray!=null&&jsonarray.equalsIgnoreCase(("true"))){
			logger.trace("Set reader to expect a json array rather than regular json input.");
			this.dumpWrappedInJsonArray=true;			
		}
		
		/*
		 * Map data to DiscourseDB
		 */
		
		logger.info("Mapping blog posts and comments to DiscourseDB");
		try(InputStream in = new FileInputStream(blogDumpFile)) {			
			if(dumpWrappedInJsonArray){
				//if the json dump is wrapped in a top-level array
				@SuppressWarnings("unchecked")
				List<ProsoloBlogPost> posts =(List<ProsoloBlogPost>)new ObjectMapper().readValues(new JsonFactory().createParser(in), new TypeReference<List<ProsoloBlogPost>>(){}).next();	
				posts.stream().forEach(p->converterService.mapPost(p, discourseName, dataSetName, blogToedxMap));						
			}else{
				//if the json dump is NOT wrapped in a top-level array
				Iterator<ProsoloBlogPost> pit =new ObjectMapper().readValues(new JsonFactory().createParser(in), ProsoloBlogPost.class);	
				Iterable<ProsoloBlogPost> iterable = () -> pit;
				StreamSupport.stream(iterable.spliterator(), false).forEach(p->converterService.mapPost(p, discourseName, dataSetName, blogToedxMap));	
			}
		}	
		
		logger.info("All done.");
	}



}