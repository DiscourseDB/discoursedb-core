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
package edu.cmu.cs.lti.discoursedb.annotation.ui.controller;

import static edu.cmu.cs.lti.discoursedb.configuration.WebSocketConfiguration.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.core.annotation.HandleAfterCreate;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.hateoas.EntityLinks;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;

@Component
@RepositoryEventHandler(Contribution.class)
public class EventHandler {

	private final SimpMessagingTemplate websocket;

	private final EntityLinks entityLinks;

	@Autowired
	public EventHandler(SimpMessagingTemplate websocket, EntityLinks entityLinks) {
		this.websocket = websocket;
		this.entityLinks = entityLinks;
	}

	@HandleAfterCreate
	public void newContribution(Contribution contribution) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/newContribution", getPath(contribution));
	}

	@HandleAfterDelete
	public void deleteContribution(Contribution contribution) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/deleteContribution", getPath(contribution));
	}

	@HandleAfterSave
	public void updateContribution(Contribution contribution) {
		this.websocket.convertAndSend(
				MESSAGE_PREFIX + "/updateContribution", getPath(contribution));
	}

	/**
	 * Take a {@link Contribution} and get the URI using Spring Data REST's {@link EntityLinks}.
	 *
	 * @param contribution
	 */
	private String getPath(Contribution contribution) {
		return this.entityLinks.linkForSingleResource(contribution.getClass(),
				contribution.getId()).toUri().getPath();
	}

}
