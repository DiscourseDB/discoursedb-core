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
    	return userService.findUserBySourceIdAndUsername(sourceId, username). 	//perform the query
    			map(u->ResponseEntity.ok(assembler.toResource(u))). 			//transform result into Resource if result exists
    			orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND)); 			//handle cases with no result
    }

	/* (non-Javadoc)
	 * @see org.springframework.hateoas.ResourceProcessor#process(org.springframework.hateoas.ResourceSupport)
	 * 
	 * Registers new search endpoints fo the User class
	 */
	@Override
	public RepositorySearchesResource process(RepositorySearchesResource resource) {
        resource.add(new Link(entityLinks.linkFor(resource.getDomainType(),"sourceid","username","projection") + "/search/findUserBySourceIdAndUsername{?sourceid,username,projection}", "findUserBySourceIdAndUsername"));
        return resource;	
    }
	
}

