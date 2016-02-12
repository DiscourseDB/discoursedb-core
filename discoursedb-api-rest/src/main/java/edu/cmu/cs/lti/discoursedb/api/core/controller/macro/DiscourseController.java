package edu.cmu.cs.lti.discoursedb.api.core.controller.macro;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@ResponseBody
@RepositoryRestController
@ExposesResourceFor(Discourse.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class DiscourseController {
	
	private final @NonNull DiscourseService discourseService;
	private final @NonNull EntityLinks entityLinks;

}
