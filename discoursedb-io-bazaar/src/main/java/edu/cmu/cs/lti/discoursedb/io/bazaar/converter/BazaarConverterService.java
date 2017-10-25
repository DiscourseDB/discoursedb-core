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
package edu.cmu.cs.lti.discoursedb.io.bazaar.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartInteractionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.bazaar.model.BazaarSourceMapping;
import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Message;
import edu.cmu.cs.lti.discoursedb.io.bazaar.model.Room;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

/**
 * @author Haitian Gong
 * @author Oliver Ferschke
 *
 */
@Log4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
public class BazaarConverterService {
	
	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	/**
	 * Maps a message to DiscourseDB 
	 * 
	 * @param m the message object
	 * @param dataSetName the dataset the message is imported from
	 * @param discourseName the discourse the message belongs to
	 * @param roommap a map from room ids to room names
	 */
	public void mapMessage(Message m, String dataSetName, String discourseName, Map<String, String> roommap) {
		if (contributionService.findOneByDataSource(m.getId(), BazaarSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).isPresent()) {
			log.warn("Message " + m.getId() + " already in database. Skipping...");
			return;
		}
		
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName, dataSetName);
		User curUser = userService.createOrGetUser(curDiscourse, m.getUsername());
		dataSourceService.addSource(curUser, new DataSourceInstance(m.getUsername(), BazaarSourceMapping.FROM_USER_ID_STR_TO_USER,DataSourceTypes.BAZAAR, dataSetName));
		
		contributionService.findOneByDataSource(m.getId(),BazaarSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName).orElseGet(()->{

					ContributionTypes mappedType = null;
					switch (m.getType()) {
						case "text":
							mappedType = ContributionTypes.POST; break;
						case "image":
							mappedType = ContributionTypes.BAZAAR_IMAGE; break;
						case "private":
							mappedType = ContributionTypes.PRIVATE_MESSAGE; break;
						default:
							log.warn("Cannot map message type "+m.getType()+". Skipping...");
							return null;
					}					
						
					log.trace("Create Content entity");
					Content curContent = contentService.createContent();
					curContent.setText(m.getContent());
					curContent.setAuthor(curUser);
					dataSourceService.addSource(curContent, new DataSourceInstance(m.getId(), BazaarSourceMapping.ID_STR_TO_CONTENT, DataSourceTypes.BAZAAR, dataSetName));
					
					log.trace("Create Contribution entity");
					Contribution curContribution = contributionService.createTypedContribution(mappedType);
					curContribution.setCurrentRevision(curContent);
					curContribution.setFirstRevision(curContent);
					dataSourceService.addSource(curContribution, new DataSourceInstance(m.getId(), BazaarSourceMapping.ID_STR_TO_CONTRIBUTION, DataSourceTypes.BAZAAR, dataSetName));

					
					//parse and set creation time for content and contribution
					try{
						Date date = sdf.parse(m.getCreated_time());									
						curContent.setStartTime(date);
						curContent.setEndTime(date);
						curContribution.setStartTime(date);
						curContribution.setEndTime(date);									
					}catch(ParseException e){
						log.error("Could not parse creation time "+m.getCreated_time(), e);
					}

					//map relation between contribution and discoursepart
					DiscoursePart curDiscoursePart = discoursepartService.createOrGetTypedDiscoursePart(
									curDiscourse, lookupRoomName(m.getRoomid(), roommap), DiscoursePartTypes.CHATROOM);
					discoursepartService.addContributionToDiscoursePart(curContribution, curDiscoursePart);
									
					return curContribution; //only needed to fulfill return reqs of orElseGet							
				}
		);		
	}
	
	/**
	 * Maps room object to DiscourseDB DiscourseParts
	 * 
	 * @param r the room object
	 * @param dataSetName the dataset the room is imported from
	 * @param discourseName the name of the discourse the chatroom belongs to
	 */
	public void mapRoom(Room r, String dataSetName, String discourseName) {

		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName, dataSetName);		
		
		//add discoursepartType entity to database
		DiscoursePart curDiscoursePart = discoursepartService.createOrGetTypedDiscoursePart(
						curDiscourse, r.getName(), DiscoursePartTypes.CHATROOM);
		
		if(r.getCreated_time()!=null) {
			try{
				curDiscoursePart.setStartTime(sdf.parse(r.getCreated_time()));
			}catch(ParseException e){
				log.error("Could not parse creation time "+r.getCreated_time(), e);
			}
		}
		if(r.getModified_time()!=null) {
			try{
				curDiscoursePart.setEndTime(sdf.parse(r.getModified_time()));				
			}catch(ParseException e){
				log.error("Could not parse modification time "+r.getModified_time(), e);				
			}
		}
		curDiscoursePart.setName(r.getName());			
		
	}
	
	/**
	 * Represents DiscoursePartInteractions based on activity indicators in the chat, such as "JOING", "LEAVE" or "READY"
	 * 
	 * @param m the message that contains an indicator for a DiscoursePartInteraction
	 * @param discourseName the name of the discourse the interaction belongs to
	 * @param dataSetName the name of the dataset the message is imported from
	 * @param roommap a map from room ids to room names.
	 */
	public void mapInteraction(Message m, String discourseName, String dataSetName, Map<String, String> roommap) {
		Discourse curDiscourse = discourseService.createOrGetDiscourse(discourseName, dataSetName);
		User curUser = userService.createOrGetUser(curDiscourse, m.getUsername());
		dataSourceService.addSource(curUser,
				new DataSourceInstance(m.getUsername(), BazaarSourceMapping.FROM_USER_ID_STR_TO_USER,DataSourceTypes.BAZAAR, dataSetName));
		
		DiscoursePart curDiscoursePart = discoursepartService.createOrGetTypedDiscoursePart(curDiscourse, lookupRoomName(m.getRoomid(), roommap), DiscoursePartTypes.CHATROOM);
		if(m.getContent().equals("join"))
			userService.createDiscoursePartInteraction(curUser, curDiscoursePart, DiscoursePartInteractionTypes.JOIN);
		if(m.getContent().equals("ready"))
			userService.createDiscoursePartInteraction(curUser, curDiscoursePart, DiscoursePartInteractionTypes.READY);
		if(m.getContent().equals("leave"))
			userService.createDiscoursePartInteraction(curUser, curDiscoursePart, DiscoursePartInteractionTypes.LEAVE);
		if(m.getContent().equals("global unready"))
			userService.createDiscoursePartInteraction(curUser, curDiscoursePart, DiscoursePartInteractionTypes.UNREADY);
		
	}
	
	/**
	 * Returns the room name from the map if it exists and is not empty.
	 * If it no valid room name exists, it uses the room id to create one.
	 * 
	 * @param roomId the if of the chat room
	 * @param roomNameMap a map from ids to names
	 * @return the room name
	 */
	private String lookupRoomName(String roomId, Map<String, String> roomNameMap){
		String mappedName = roomNameMap.get(roomId); 
		if(mappedName!=null&&!mappedName.isEmpty()){
			return mappedName;
		}else{
			log.warn("Could not find valid room name for room "+roomId+". Using room id instead.");
			return "Unnamed room "+ roomId;
		}
	}
	
}