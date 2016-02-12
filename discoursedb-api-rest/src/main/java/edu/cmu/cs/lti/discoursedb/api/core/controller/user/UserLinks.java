package edu.cmu.cs.lti.discoursedb.api.core.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityLinks;
import org.springframework.stereotype.Component;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserLinks {
	static final String USER = "/user";

	private final @NonNull EntityLinks entityLinks;
	
	//TODO add methods that generate user-related links

}
