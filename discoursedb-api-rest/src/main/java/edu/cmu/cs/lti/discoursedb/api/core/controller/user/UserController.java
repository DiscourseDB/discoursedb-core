package edu.cmu.cs.lti.discoursedb.api.core.controller.user;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestController;
import org.springframework.data.rest.webmvc.RepositorySearchesResource;
import org.springframework.hateoas.EntityLinks;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.hateoas.ResourceProcessor;
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
@ExposesResourceFor(User.class)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class UserController implements ResourceProcessor<RepositorySearchesResource>, ResourceAssembler<User, Resource<User>> {
	
	private final @NonNull UserService userService;
	private final @NonNull EntityLinks entityLinks;
	
    @RequestMapping(method = RequestMethod.GET, value="/users/search/findUserBySourceIdAndUsername")
    public @ResponseBody ResponseEntity<?> findUserBySourceIdAndUsername(@RequestParam("sourceid")String sourceId, @RequestParam("username")String username) {
    	Optional<User> user = userService.findUserBySourceIdAndUsername(sourceId, username);
    	if(!user.isPresent()){
    		return ResponseEntity.notFound().build();     		    		
    	}else{
    		return ResponseEntity.ok(new Resource<User>(user.get()));     		
    	}
    }

	@Override
	public Resource<User> toResource(User entity) {
	     return new Resource<User>(entity);
	}

	@Override
	public RepositorySearchesResource process(RepositorySearchesResource resource) {
        resource.add(new Link(entityLinks.linkFor(User.class, "sourceId", "username") + "/search/findUserBySourceIdAndUsername{?sourceid,username}", "findUserBySourceIdAndUsername"));
        return resource;	
       }
	
}

