package edu.cmu.cs.lti.discoursedb.api.core.controller.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.PersistentEntityResourceAssembler;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositorySearchesResource;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.ResourceProcessor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RepositoryRestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class UserController implements ResourceProcessor<RepositorySearchesResource> {
	
	private final @NonNull UserService userService;
	private final @NonNull EntityLinks entityLinks;
	
    @RequestMapping(method = RequestMethod.GET, value="/users/search/findUserBySourceIdAndUsername")
	public @ResponseBody ResponseEntity<?> findUserBySourceIdAndUsername(
			@RequestParam("sourceid") String sourceId,
			@RequestParam("username") String username,
			PersistentEntityResourceAssembler assembler) 
    {
    	return userService.findUserBySourceIdAndUsername(sourceId, username).
    			map(u->ResponseEntity.ok(assembler.toResource(u))).
    			orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

	/* (non-Javadoc)
	 * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
	 * 
	 * Registers new search endpoints fo the User class
	 */
	@Override
	public RepositorySearchesResource process(RepositorySearchesResource resource) {
        resource.add(new Link(entityLinks.linkFor(User.class, "sourceId", "username", "projection") + "/search/findUserBySourceIdAndUsername{?sourceid,username,projection}", "findUserBySourceIdAndUsername"));
        return resource;	
    }
	
}

