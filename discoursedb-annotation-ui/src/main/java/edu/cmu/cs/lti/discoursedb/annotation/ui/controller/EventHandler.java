package edu.cmu.cs.lti.discoursedb.annotation.ui.controller;

import static edu.cmu.cs.lti.discoursedb.configuration.WebSocketConfiguration.MESSAGE_PREFIX;

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
