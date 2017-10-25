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
package edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.converter;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.io.prosolo.socialactivity.io.ProsoloDB;

/**
 * This converter loads data from a prosolo database and maps it to DiscourseDB.
 * The DiscourseDB configuration is defined in the dicoursedb-model project and
 * Spring/Hibernate are taking care of connections.
 * 
 * The connection to the prosolo database is more lightweight and uses a JDBC
 * connection. The configuration parameters for this connection are passed to
 * the converter as launch parameters in the following order
 * 
 * <DiscourseName> <DataSetName> <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>
 * 
 * @author Oliver Ferschke
 *
 */
@Component
public class ProsoloConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(ProsoloConverter.class);
	
	/**
	 * List of all social activity "actions" that result in the creation of a DiscourseDB Contribution
	 */
	private final List<String> CONTRIB_CREATING_ACTIONS = Arrays.asList(new String[]{"Create","Post","AddNote","Comment","PostShare","TwitterPost"});
	private final List<String> FOLLOWED_ENTITY_TYPES = Arrays.asList(new String[]{"FollowedResourceEntity","FollowedUserEntity"});
	
	private String discourseName;
	private String dataSetName;
	
	private ProsoloDB prosolo = null;

	@Autowired private ProsoloConverterService converterService;	
	@Autowired private DataSourceService dataSourceService;
	
	@Override 
	public void run(String... args) throws Exception {

		if (args.length != 6) {
			logger.error("Incorrect number of parameters. USAGE: <DiscourseName> <DataSetName> <prosolo_dbhost> <prosolo_db> <prosolo_dbuser> <prosolo_dbpwd>");
			return;
		}
		
		//Parse command line parameters		
		this.discourseName=args[0];			
		this.dataSetName=args[1];
		prosolo = new ProsoloDB(args[2],args[3],args[4],args[5]);

		logger.info("Start mapping to DiscourseDB...");
		
		if(dataSourceService.findDataset(dataSetName) != null){
			logger.warn("Dataset "+dataSetName+" has previously already been imported. Previously imported social avitivities will be skipped.");			
		}

		try {
			//start with all the creates
			mapSocialActivities("PostSocialActivity", "Post");
			mapSocialActivities("NodeSocialActivity", "Create");
			mapSocialActivities("PostSocialActivity", "TwitterPost");
			mapSocialActivities("TwitterPostSocialActivity", "TwitterPost");
			
			//then proceed with the sharing and commenting
			mapSocialActivities("GoalNoteSocialActivity", "AddNote"); 
			mapSocialActivities("NodeComment", "Comment"); 
			mapSocialActivities("PostSocialActivity","PostShare");	
			mapSocialActivities("SocialActivityComment","Comment");
			
			//finally proceed with following activities
			mapFollowedEntities("FollowedResourceEntity");
			mapFollowedEntities("FollowedUserEntity");
		} catch (SQLException ex) {
			logger.error(ex.getMessage(), ex);
		} finally {
			prosolo.closeConnection();
		}
		logger.info("...mapping complete");
	}

	/**
	 * Maps social activities of the given type and the given action to DiscourseDB.
	 * 
	 * @param dtype the type of the SocialActivity
	 * @param action the create/add action
	 * @throws SQLException
	 */
	public void mapSocialActivities(String dtype, String action) throws SQLException{
		if(!CONTRIB_CREATING_ACTIONS.contains(action)){
			logger.warn("Action "+action+" (SocialActivity type "+dtype+") does not create DiscourseDB contributions and is thus not covered by this method.");
			return;
		}
		
		List<Long> socialActivityIDs = prosolo.getIdsForDtypeAndAction(dtype, action);
		logger.info("Mapping "+socialActivityIDs.size()+" social activities of type \""+dtype+"\", action \""+action+"\"");				
				
		//retrieve list of social activity ids and then process each of them within the loop
		for (Long curSocialActivityId : socialActivityIDs) {			
			logger.trace("Processing "+dtype+" ("+action+") id:"+curSocialActivityId);			
			converterService.mapSocialActivity(dtype,action,curSocialActivityId,prosolo, discourseName,dataSetName);
		}
	}

	
	/**
	 * Creates follow relationships from Prosolo followed_entities.
	 * 
	 * @param dtype the type of the followed_entity, i.e. "FollowedResourceEntity" or "FollowedUserEntity"
	 * @throws SQLException
	 */
	public void mapFollowedEntities(String dtype) throws SQLException{
		if(!FOLLOWED_ENTITY_TYPES.contains(dtype)){
			logger.warn(dtype+" is not a valid followed entity type.");
			return;
		}
		
		List<Long> followedEntityIDs = prosolo.getIdsForFollowedEntityType(dtype);
		logger.info("Mapping "+followedEntityIDs.size()+" followed entity if type \""+dtype+"\"");				
		
		//retrieve list of social activity ids and then process each of them within the loop
		for (Long curFollowedEntityId : followedEntityIDs) {			
			logger.trace("Processing "+dtype+" id:"+curFollowedEntityId);			
			converterService.mapFollowedEntity(dtype,curFollowedEntityId,prosolo, discourseName,dataSetName);
		}
	}	

}