/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Authors: Oliver Ferschke and Chris Bogart
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
package edu.cmu.cs.lti.discoursedb.api.recommendation.controller;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationContributionResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationDataSourceInstanceResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationDiscoursePartResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationDiscourseResource;
import edu.cmu.cs.lti.discoursedb.api.recommendation.resource.RecommendationUserResource;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseRelation;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseToDiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;

@Controller
//@RequestMapping(value = "/recommendation", produces = "application/hal+json")
public class RecommendationRestController {

	@Autowired
	private DiscourseRepository discourseRepository;

	@Autowired
	private DiscoursePartRepository discoursePartRepository;

	@Autowired
	private DiscourseToDiscoursePartRepository discourseToDiscoursePartRepository;

	@Autowired
	DiscoursePartContributionRepository discoursePartContributionRepository;
	
	@Autowired
	private ContributionRepository contributionRepository;

	@Autowired
	private UserRepository userRepository;

	//@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	Resources<RecommendationDiscourseResource> discourses() {
		List<RecommendationDiscourseResource> discourseResources = StreamSupport
				.stream(discourseRepository.findAll().spliterator(), false)
				.map(RecommendationDiscourseResource::new).collect(Collectors.toList());
		return new Resources<RecommendationDiscourseResource>(discourseResources);
	}

	//@RequestMapping(value = "/allcontributions", method = RequestMethod.GET)
	@ResponseBody
	Resources<RecommendationContributionResource> contributions() {
		List<RecommendationContributionResource> contribResources = StreamSupport
				.stream(contributionRepository.findAll().spliterator(), false)
				.map(RecommendationContributionResource::new).collect(Collectors.toList());
		return new Resources<RecommendationContributionResource>(contribResources);
	}

	//@RequestMapping(value = "/allusers", method = RequestMethod.GET)
	@ResponseBody
	Resources<RecommendationUserResource> users() {
		List<RecommendationUserResource> userResources = StreamSupport
				.stream(userRepository.findAll().spliterator(), false).map(RecommendationUserResource::new)
				.collect(Collectors.toList());
		return new Resources<RecommendationUserResource>(userResources);
	}

	//@RequestMapping(value = "/contributionParent/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RecommendationContributionResource contribParent(@PathVariable Long id) {
		return new RecommendationContributionResource(getParentContribution(id));				
	}
	
	//@RequestMapping(value = "/threadStarter/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RecommendationContributionResource threadStarter(@PathVariable Long id) {
		return new RecommendationContributionResource(getThreadStarter(id));				
	}
	
	public Contribution getParentContribution(Long contribId){
		//TODO check if optional is present
		Contribution contrib= contributionRepository.findOne(contribId).get();
		for(DiscourseRelation rel:contrib.getTargetOfDiscourseRelations()){
			if(rel.getType().equals(DiscourseRelationTypes.COMMENT.name())){
				return rel.getSource();
			}
			if(rel.getType().equals(DiscourseRelationTypes.REPLY.name())){
				return rel.getSource();
			}
		}
		return contrib;
	}
	
	public Contribution getThreadStarter(Long contribId){
		//TODO check if optional is present
		Contribution contrib= contributionRepository.findOne(contribId).get();
		for(DiscourseRelation rel:contrib.getTargetOfDiscourseRelations()){
			if(rel.getType().equals(DiscourseRelationTypes.DESCENDANT.name())){
				return rel.getSource();
			}
		}
		return contrib;
	}

	//@RequestMapping(value = "/contribution/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RecommendationContributionResource contrib(@PathVariable Long id) {
		Contribution contrib = contributionRepository.findOne(id).get();
		return new RecommendationContributionResource(contrib);
	}

	//@RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
	@ResponseBody
	public RecommendationUserResource user(@PathVariable Long id) {
		User user = userRepository.findOne(id).get();
		return new RecommendationUserResource(user);
	}

	//@RequestMapping(value = "/discoursePartsOfDiscourse/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Resources<RecommendationDiscoursePartResource> discourseToDiscourseParts(@PathVariable Long id) {
		Discourse discourse = discourseRepository.findOne(id).get();
		List<RecommendationDiscoursePartResource> discoursePartResources = discourseToDiscoursePartRepository.findByDiscourse(discourse).stream().map(e -> e.getDiscoursePart()).map(RecommendationDiscoursePartResource::new).collect(Collectors.toList());
		return new Resources<RecommendationDiscoursePartResource>(discoursePartResources);
	}

	//@RequestMapping(value = "/contributionsOfDiscoursePart/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Resources<RecommendationContributionResource> contributionsForDiscoursePart(@PathVariable Long id) {
		DiscoursePart discoursePart = discoursePartRepository.findOne(id).get();		
		List<RecommendationContributionResource> discoursePartResources = discoursePartContributionRepository
				.findByDiscoursePart(discoursePart).stream()
				.map(e -> e.getContribution())
				.map(RecommendationContributionResource::new).collect(Collectors.toList());
		return new Resources<RecommendationContributionResource>(discoursePartResources);
	}

	//@RequestMapping(value = "/usersOfDiscoursePart/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Resources<RecommendationUserResource> usersForDiscoursePart(@PathVariable Long id) {
		DiscoursePart discoursePart = discoursePartRepository.findOne(id).get();		
		List<RecommendationUserResource> discoursePartResources = discoursePartContributionRepository
				.findByDiscoursePart(discoursePart).stream()
				.map(e -> e.getContribution().getCurrentRevision().getAuthor())
				.map(RecommendationUserResource::new)
				.collect(Collectors.toList());
		return new Resources<RecommendationUserResource>(discoursePartResources);
	}
	
	//@RequestMapping(value = "/sourcesForUser/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Resources<RecommendationDataSourceInstanceResource> sourcesForUser(@PathVariable Long id) {
		User user= userRepository.findOne(id).get();		
		List<RecommendationDataSourceInstanceResource> dataSourceResources = 
				user.getDataSourceAggregate().getSources().stream()
				.map(RecommendationDataSourceInstanceResource::new).collect(Collectors.toList());
		return new Resources<RecommendationDataSourceInstanceResource>(dataSourceResources);
	}
	
	//@RequestMapping(value = "/sourcesForContribution/{id}", method = RequestMethod.GET)
	@ResponseBody
	public Resources<RecommendationDataSourceInstanceResource> sourcesForContribution(@PathVariable Long id) {
		Contribution contrib= contributionRepository.findOne(id).get();		
		List<RecommendationDataSourceInstanceResource> dataSourceResources = 
				contrib.getDataSourceAggregate().getSources().stream()
				.map(RecommendationDataSourceInstanceResource::new).collect(Collectors.toList());
		return new Resources<RecommendationDataSourceInstanceResource>(dataSourceResources);
	}

}
