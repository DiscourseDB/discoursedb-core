package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratService;
import edu.cmu.cs.lti.discoursedb.annotation.lightside.io.LightSideService;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingBratExportResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingContributionResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingDiscoursePartResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingDiscourseResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingStatsResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingUserResource;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.DiscoursePartInteractionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;

@Controller
@RequestMapping(value = "/browsing", produces = "application/hal+json")
public class BrowsingRestController {

	private static final Logger logger = LogManager.getLogger(BrowsingRestController.class);

	@Autowired
	private BratService bratService;
	
	@Autowired
	private LightSideService lightsideService;
	
	@Autowired 
	private Environment environment;
	
	@Autowired
	private DiscourseRepository discourseRepository;

	@Autowired
	private DiscoursePartRepository discoursePartRepository;

	@Autowired
	private UserService userService;

	@Autowired
	private DiscoursePartRelationRepository discoursePartRelationRepository;
	
	@Autowired
	private DiscoursePartInteractionRepository discoursePartInteractionRepository;
	
	@Autowired
	DiscoursePartContributionRepository discoursePartContributionRepository;
	
	@Autowired
	private ContributionRepository contributionRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired PagedResourcesAssembler<BrowsingDiscoursePartResource> praDiscoursePartAssembler;
	@Autowired PagedResourcesAssembler<BrowsingDiscourseResource> praDiscourseAssembler;
	@Autowired PagedResourcesAssembler<BrowsingContributionResource> praContributionAssembler;
	@Autowired PagedResourcesAssembler<BrowsingBratExportResource> praBratAssembler;
	@Autowired PagedResourcesAssembler<BrowsingUserResource> praUserAssembler;

	@Autowired 
	private DiscoursePartService discoursePartService;
	
	@RequestMapping(value="/stats", method=RequestMethod.GET)
	@ResponseBody
	Resources<BrowsingStatsResource> stats() {
		BrowsingStatsResource bsr = new BrowsingStatsResource(discourseRepository, discoursePartRepository, contributionRepository, userRepository);
		List<BrowsingStatsResource> l = new ArrayList<BrowsingStatsResource>();
		l.add(bsr);
		
		Resources<BrowsingStatsResource> r =  new Resources<BrowsingStatsResource>(l);
		for (String t: bsr.getDiscourseParts().keySet()) {
			r.add(makeLink("/browsing/repos?repoType=" + t, t));			
		}
		r.add(makeLink("/browsing/bratExports", "BRAT markup"));
		r.add(makeLink("/browsing/lightsideExports", "Lightside exports"));
		return r;
	}
	

	
	@RequestMapping(value="/discourses/{discourseId}", method=RequestMethod.GET)
	@ResponseBody
	Resource<BrowsingDiscourseResource> discourses(
			   @PathVariable("discourseId") Long discourseId) {
		BrowsingDiscourseResource bsr = new BrowsingDiscourseResource(discourseRepository.findOne(discourseId).get(), discoursePartRepository);
		
		
		Resource<BrowsingDiscourseResource> r =  new Resource<BrowsingDiscourseResource>(bsr);
		
		return r;
	}
	
	@RequestMapping(value = "/discourses/{discourseId}/discoursePartTypes/{discoursePartType}", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> discoursePartsByTypeAndDiscourse(
														   @RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("discourseId") Long discourseId,
														   @PathVariable("discoursePartType") String discoursePartType,
														   @RequestParam(value="annoType", defaultValue="*") String annoType) {
		PageRequest p = new PageRequest(page,size);
		Page<BrowsingDiscoursePartResource> repoResources = 
				discoursePartRepository.findAllByDiscourseAndType(discoursePartType, discourseId, p)
				.map(BrowsingDiscoursePartResource::new)
				.map(bdpr -> {bdpr.filterAnnotations(annoType); 
				              bdpr.fillInUserInteractions(discoursePartInteractionRepository);
				              return bdpr; });
				
		repoResources.forEach(bcr -> {
			if (bcr.getContainingDiscourseParts().size() > 1) { 
				bcr._getContainingDiscourseParts().forEach(
			     (dpId, dpname) -> {
			    	 bcr.add(makeLink("/browsing/usersInDiscourseParts/" + dpId, "users in " + dpname));
			    	 bcr.add(makeLink("/browsing/subDiscourseParts/" + dpId, dpname));
			     });
			}  
			Link check = makeLink1Arg("/browsing/action/exportLightside","chk:Export Data to Lightside", "parentDpId", Long.toString(bcr._getDpId()));
	    	Link check2 = makeLink2Arg("/browsing/action/exportLightside","chk:Export Annotations to Lightside", "withAnnotations", "true", "parentDpId", Long.toString(bcr._getDpId()));
	    	bcr.add(check);	
	    	bcr.add(check2);
		});
		
		PagedResources<Resource<BrowsingDiscoursePartResource>> response = praDiscoursePartAssembler.toResource(repoResources);

		return response;
	}
	
	
	
	@RequestMapping(value = "/discourses", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscourseResource>> discourse(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size)  {
		PageRequest p = new PageRequest(page,size);
		
			Page<BrowsingDiscourseResource> discourseResources = discourseRepository.findAll(p).map(b -> new BrowsingDiscourseResource(b, discoursePartRepository));
					
/*			discourseResources.forEach(
			    bcr -> {
			    	Long dpId = bcr.getDiscourseId();
				    Link check0 = makeLink2Arg("/browsing/action/exportBratItem","chk:Export to BRAT", "parentDpId", dpId.toString(), "childDpId", Long.toString(bcr._getDpId()));
				    bcr.add(check0);
			    	Link check = makeLink1Arg("/browsing/action/exportLightside","chk:Export Data to Lightside", "parentDpId", Long.toString(bcr._getDpId()));
			    	Link check2 = makeLink2Arg("/browsing/action/exportLightside","chk:Export Annotations to Lightside", "withAnnotations", "true", "parentDpId", Long.toString(bcr._getDpId()));
			    	bcr.add(check);	
			    	bcr.add(check2);
		    	
			    }
			);*/
			PagedResources<Resource<BrowsingDiscourseResource>> response = praDiscourseAssembler.toResource(discourseResources);

			/*
			 * Disabling exportBrat link overall -- instead add for each thread item as a checkbox
			 *
			long threadcount = 0L;
			for (BrowsingDiscoursePartResource bdpr: repoResources) {
				threadcount += bdpr.getContributionCount();
			}
			
			if (threadcount > 0) {
				response.add(makeLink1Arg("/browsing/action/exportBrat", "Export these all to BRAT", "parentDpId", dpId.toString()));
			}
			 */
	//		response.add(makeLink("/browsing/usersInDiscoursePart/" + dpId, "show users"));

			return response;
		 
	}

	
	
	@RequestMapping(value = "/repos", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> discourseParts(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @RequestParam("repoType") String repoType,
														   @RequestParam(value="annoType", defaultValue="*") String annoType) {
		PageRequest p = new PageRequest(page,size);
		Page<BrowsingDiscoursePartResource> repoResources = 
				discoursePartRepository.findAllNonDegenerateByType(repoType, p)
				.map(BrowsingDiscoursePartResource::new)
				.map(bdpr -> {bdpr.filterAnnotations(annoType); 
				              bdpr.fillInUserInteractions(discoursePartInteractionRepository);
				              return bdpr; });
				
		repoResources.forEach(bcr -> {
			if (bcr.getContainingDiscourseParts().size() > 1) { 
				bcr._getContainingDiscourseParts().forEach(
			     (dpId, dpname) -> {
			    	 bcr.add(makeLink("/browsing/usersInDiscourseParts/" + dpId, "users in " + dpname));
			    	 bcr.add(makeLink("/browsing/subDiscourseParts/" + dpId, dpname));
			     });
			}  
			Link check = makeLink1Arg("/browsing/action/exportLightside","chk:Export Data to Lightside", "parentDpId", Long.toString(bcr._getDpId()));
	    	Link check2 = makeLink2Arg("/browsing/action/exportLightside","chk:Export Annotations to Lightside", "withAnnotations", "true", "parentDpId", Long.toString(bcr._getDpId()));
	    	bcr.add(check);	
	    	bcr.add(check2);
		});
		
		PagedResources<Resource<BrowsingDiscoursePartResource>> response = praDiscoursePartAssembler.toResource(repoResources);

		return response;
	}
	

	
	/*@RequestMapping(value = "/subDiscoursePartsByName", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> subDiscoursePartsByName(@RequestParam(value= "page", defaultValue = "0") int page, 
			   @RequestParam(value= "size", defaultValue="20") int size,
			   @RequestParam("discoursePartName") String discoursePartName)  {
		List<DiscoursePart> dps = discoursePartRepository.findAllByName(discoursePartName);
		if (dps.size() > 0) {
			logger.info("Found " + discoursePartName + " as id " + dps.get(0).getId().toString());
			return subDiscourseParts(page, size, dps.get(0).getId());
		} else {
			logger.info("Did not find " + discoursePartName);
			return subDiscourseParts(page, size, 0L);
		}
	}*/
	
	public String discoursePart2BratName(DiscoursePart dp) {
		return dp.getName().replaceAll("[^a-zA-Z0-9]", "_") + "__" + dp.getId().toString();
	}
	
	public Optional<DiscoursePart> bratName2DiscoursePart(String filename) {
		try {
			String[] parts = filename.split("__");
			if (parts.length > 1) {
				long dpid = Long.parseLong(parts[parts.length-1]);
				logger.info(" final part of " + filename + " is " + dpid);
				return discoursePartRepository.findOne(dpid);
			}
		} catch (Exception e) {
			logger.info("No success identifying brat import directory " + filename + e.toString());
		}
		return Optional.empty();
	}
	
	public Optional<DiscoursePart> lightsideName2DiscoursePartForImport(String filename) {
		if (filename.endsWith(".csv") && filename.contains("_annotated_")) {
			return bratName2DiscoursePart(filename.substring(0, filename.length()-4));
		} else {
			return Optional.empty();
		}
	}
	
	public String discoursePart2LightSideName(DiscoursePart dp, boolean withAnnotations) {
		return dp.getName().replaceAll("[^a-zA-Z0-9]", "_") + (withAnnotations?"_annotated_":"") + "__" + dp.getId() + ".csv".toString();
	}
	
	
	@RequestMapping(value = "/action/exportLightside", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> exportLightsideAction(
			@RequestParam(value="withAnnotations", defaultValue = "false") boolean withAnnotations,
			@RequestParam(value= "parentDpId") long parentDpId) throws IOException {
		DiscoursePart dp = discoursePartRepository.findOne(parentDpId).get();
		String lsDataDirectory = environment.getRequiredProperty("lightside.data_directory");
		String lsOutputFilename = lsDataDirectory + "/" + discoursePart2LightSideName(dp, withAnnotations);
		logger.info(" Exporting dp " + dp.getName());
		Set<DiscoursePart> descendents = discoursePartService.findDescendentClosure(dp, Optional.empty());
		if (withAnnotations) {
			java.io.File lsOutputFile = new java.io.File(lsOutputFilename);
			lightsideService.exportAnnotations(descendents, lsOutputFile);
		} else {
			// For multiple discourseParts, need to assemble all the contributions
			lightsideService.exportDataForAnnotation(lsOutputFilename, 
					descendents.stream()
					.flatMap(targ -> targ.getDiscoursePartContributions().stream())
					.map(dpc -> dpc.getContribution())::iterator);
		}
		return lightsideExports();
	}
	
	@RequestMapping(value = "/action/importLightside", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> importLightsideAction(@RequestParam(value= "lightsideDirectory") String lightsideDirectory) throws IOException {
		String bratDataDirectory = environment.getRequiredProperty("lightside.data_directory");		
		lightsideService.importAnnotatedData(bratDataDirectory + "/" + lightsideDirectory);
		Optional<DiscoursePart> dp = lightsideName2DiscoursePartForImport(lightsideDirectory);
		if (dp.isPresent()) {
			return subDiscourseParts(0,20, dp.get().getId());
		} else {
			return null;
		}
	}	
	
	
	@RequestMapping(value = "/action/exportBrat", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> exportBratAction(@RequestParam(value= "parentDpId") long parentDpId) throws IOException {
		String bratDataDirectory = environment.getRequiredProperty("brat.data_directory");
		DiscoursePart dp = discoursePartRepository.findOne(parentDpId).get();
		String bratDirectory = bratDataDirectory + "/" + discoursePart2BratName(dp);
		logger.info(" Exporting dp " + dp.getName());
		for (DiscoursePartRelation subdp : dp.getSourceOfDiscoursePartRelations()) {
			logger.info(" Exporting issue " + subdp.getTarget().getName());
			bratService.exportDiscoursePart(subdp.getTarget(), bratDirectory);
		}
		return bratExports();
	}
	
	@RequestMapping(value = "/action/exportBratItem", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> exportBratActionItem(
			@RequestParam(value= "parentDpId") long parentDpId,
			@RequestParam(value="childDpId")  long childDpId) throws IOException {
		String bratDataDirectory = environment.getRequiredProperty("brat.data_directory");
		DiscoursePart parentDp = discoursePartRepository.findOne(parentDpId).get();
		DiscoursePart childDp = discoursePartRepository.findOne(childDpId).get();
		String bratDirectory = bratDataDirectory + "/" + discoursePart2BratName(parentDp);
		logger.info(" Exporting dp " + childDp.getName() + " in directory for " + parentDp.getName());
		bratService.exportDiscoursePart(childDp, bratDirectory);
		return bratExports();
	}

	@RequestMapping(value = "/action/importBrat", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> importBratAction(
			@RequestParam(value= "bratDirectory") String bratDirectory) throws IOException {
		String bratDataDirectory = environment.getRequiredProperty("brat.data_directory");		
		bratService.importDataset(bratDataDirectory + "/" + bratDirectory);
		Optional<DiscoursePart> dp = bratName2DiscoursePart(bratDirectory);
		if (dp.isPresent()) {
			return subDiscourseParts(0,20, dp.get().getId());
		} else {
			return null;
		}
	}

	@RequestMapping(value = "/lightsideExports", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> lightsideExports() {
		
		String lightsideDataDirectory = environment.getRequiredProperty("lightside.data_directory");
		List<BrowsingBratExportResource> exported = BrowsingBratExportResource.findPreviouslyExportedLightside(lightsideDataDirectory);
		Page<BrowsingBratExportResource> p = new PageImpl<BrowsingBratExportResource>(exported, new PageRequest(0,100), exported.size());
		for (BrowsingBratExportResource bber: p) {
			bber.add(makeLink1Arg("/browsing/action/importLightside", "Import Lightside markup", "lightsideDirectory", bber.getName()));
		}
		return praBratAssembler.toResource(p);
	}
	
	
	@RequestMapping(value = "/bratExports", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> bratExports() {
		String bratDataDirectory = environment.getRequiredProperty("brat.data_directory");
		List<BrowsingBratExportResource> exported = BrowsingBratExportResource.findPreviouslyExportedBrat(bratDataDirectory);
		Page<BrowsingBratExportResource> p = new PageImpl<BrowsingBratExportResource>(exported, new PageRequest(0,100), exported.size());
		for (BrowsingBratExportResource bber: p) {
			bber.add(makeLink1Arg("/browsing/action/importBrat", "Import BRAT markup", "bratDirectory", bber.getName()));
			bber.add(makeBratLink("/index.xhtml#/" + bber.getName(), "Edit BRAT markup"));
		}
		return praBratAssembler.toResource(p);
	}
	
	@RequestMapping(value = "/subDiscourseParts/{childOf}", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> subDiscourseParts(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("childOf") Long dpId)  {
		PageRequest p = new PageRequest(page,size, new Sort("startTime"));
		
		Optional<DiscoursePart> parent = discoursePartRepository.findOne(dpId);
		if (parent.isPresent()) {
			Page<BrowsingDiscoursePartResource> repoResources = 
					discoursePartRelationRepository.findAllTargetsBySource(parent.get(), p)
			/*.map(dpr -> dpr.getTarget())*/.map(BrowsingDiscoursePartResource::new);
			
			repoResources.forEach(bdp -> bdp.fillInUserInteractions(discoursePartInteractionRepository));
			repoResources.forEach(
			    bcr -> {
			    	if (bcr.getContainingDiscourseParts().size() > 1) { 
				        bcr._getContainingDiscourseParts().forEach(
				             (childDpId,childDpName) -> {
				            	 bcr.add(
						    		makeLink("/browsing/subDiscourseParts/" + childDpId , "Contained in: " + childDpName));
				             }
					    );
				    }
			    	if (bcr.getContributionCount() > 0) {
				    	Link check = makeLink2Arg("/browsing/action/exportBratItem","chk:Export to BRAT", "parentDpId", dpId.toString(), "childDpId", Long.toString(bcr._getDpId()));
				    	bcr.add(check);
			    	} 
		    		Link check = makeLink1Arg("/browsing/action/exportLightside","chk:Export Data to Lightside", "parentDpId", Long.toString(bcr._getDpId()));
			    	Link check2 = makeLink2Arg("/browsing/action/exportLightside","chk:Export Annotations to Lightside", "withAnnotations", "true", "parentDpId", Long.toString(bcr._getDpId()));
			    	bcr.add(check);	
			    	bcr.add(check2);
		    	
			    }
			);
			PagedResources<Resource<BrowsingDiscoursePartResource>> response = praDiscoursePartAssembler.toResource(repoResources);

			/*
			 * Disabling exportBrat link overall -- instead add for each thread item as a checkbox
			 *
			long threadcount = 0L;
			for (BrowsingDiscoursePartResource bdpr: repoResources) {
				threadcount += bdpr.getContributionCount();
			}
			
			if (threadcount > 0) {
				response.add(makeLink1Arg("/browsing/action/exportBrat", "Export these all to BRAT", "parentDpId", dpId.toString()));
			}
			 */
			response.add(makeLink("/browsing/usersInDiscoursePart/" + dpId, "show users"));

			return response;
		} else {
			logger.info("subdiscourseParts(" + dpId + ") : isPresent==false");
			return null;
		}
	}
	
	
	@RequestMapping(value = "/usersInDiscoursePart/{dpId}", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingUserResource>> users(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("dpId") Long dpId)  {
		PageRequest p = new PageRequest(page,size);
		
		
		Optional<DiscoursePart> parent = discoursePartRepository.findOne(dpId);
		
		if (parent.isPresent()) {
			List<User> lbcr1 = new ArrayList<User>();
			lbcr1.addAll(
					userService.findUsersUnderDiscoursePart(parent.get()));
			List<BrowsingUserResource> lbcr = lbcr1.subList(page*size, lbcr1.size()).stream()
					.map(BrowsingUserResource::new)
					.map(b -> { b.fillInDiscoursePartLinks(discoursePartService); return b; })
					.collect(Collectors.toList());
			
			Page<BrowsingUserResource> pbcr = new PageImpl<BrowsingUserResource>(lbcr, p, lbcr.size());
			PagedResources<Resource<BrowsingUserResource>> response = praUserAssembler.toResource(pbcr);
			
			return response;
		} else {
			return null;
		}
	}
	
	
	
	/*@RequestMapping(value = "/discourses", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingContributionResource>> discourses()  {
		
		
		List<Discourse> ds = discourseRepository.findAll();
			Page<BrowsingDiscourseResource> lbcr = 
					ds.map(BrowsingDiscourseResource::new);
			lbcr.forEach(bcr -> {if (bcr.getDiscourseParts().size() > 1) { bcr._getDiscourseParts().forEach(
					     (dpId2, dpName2) -> {
					        if (dpId2 != dpId) { bcr.add(
						    	makeLink("/browsing/dpContributions/" + dpId2 , "Also in:" + dpName2)); }}  ); }} );
			PagedResources<Resource<BrowsingDiscourseResource>> response = praContributionAssembler.toResource(lbcr);
			
			//response.add(makeLink("/browsing/subDiscourseParts{?page,size,repoType,annoType}", "search"));
			response.add(makeLink("/browsing/usersInDiscoursePart/" + dpId, "show users"));
			return response;
		} else {
			return null;
		}
	}*/
	
	
	@RequestMapping(value = "/dpContributions/{childOf}", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingContributionResource>> dpContributions(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("childOf") Long dpId)  {
		PageRequest p = new PageRequest(page,size, new Sort("startTime"));
		
		
		Optional<DiscoursePart> parent = discoursePartRepository.findOne(dpId);
		if (parent.isPresent()) {
			Page<BrowsingContributionResource> lbcr = 
					discoursePartContributionRepository.findByDiscoursePart(parent.get(), p)
					.map(dpc -> dpc.getContribution())
					.map(BrowsingContributionResource::new);
			lbcr.forEach(bcr -> {if (bcr.getDiscourseParts().size() > 1) { bcr._getDiscourseParts().forEach(
					     (dpId2, dpName2) -> {
					        if (dpId2 != dpId) { bcr.add(
						    	makeLink("/browsing/dpContributions/" + dpId2 , "Also in:" + dpName2)); }}  ); }} );
			PagedResources<Resource<BrowsingContributionResource>> response = praContributionAssembler.toResource(lbcr);
			
			//response.add(makeLink("/browsing/subDiscourseParts{?page,size,repoType,annoType}", "search"));
			response.add(makeLink("/browsing/usersInDiscoursePart/" + dpId, "show users"));
			return response;
		} else {
			return null;
		}
	}
	public Link makeBratLink(String urlend, String rel) {
		String brat_base = environment.getProperty("brat.ui_base");
		if (brat_base != null) {
			return(new Link(brat_base + urlend, rel));
		} else {
			return null;
		}
	}
	public static Link makeLink1Arg(String dest, String rel, String key, String value) {
		String path = ServletUriComponentsBuilder.fromCurrentRequestUri()
			.replacePath(dest)
			.replaceQueryParam(key, URLEncoder.encode(value))
	        .build()
	        .toUriString();
	    Link link = new Link(path,rel);
	    return link;	
    }
	public static Link makeLink2Arg(String dest, String rel, String key, String value,String key2, String value2) {
		String path = ServletUriComponentsBuilder.fromCurrentRequestUri()
			.replacePath(dest)
			.replaceQueryParam(key, URLEncoder.encode(value))
			.replaceQueryParam(key2, URLEncoder.encode(value2))
	        .build()
	        .toUriString();
	    Link link = new Link(path,rel);
	    return link;	
    }
	public static Link makeLink3Arg(String dest, String rel, String key, String value,String key2, String value2, String key3, String value3) {
		String path = ServletUriComponentsBuilder.fromCurrentRequestUri()
			.replacePath(dest)
			.replaceQueryParam(key, URLEncoder.encode(value))
			.replaceQueryParam(key2, URLEncoder.encode(value2))
			.replaceQueryParam(key3, URLEncoder.encode(value3))
	        .build()
	        .toUriString();
	    Link link = new Link(path,rel);
	    return link;	
    }
	public static Link makeLink(String dest, String rel) {
			String path = ServletUriComponentsBuilder.fromCurrentRequestUri()
				.replacePath(dest)
		        .build()
		        .toUriString();
		    Link link = new Link(path,rel);
		    return link;	
	}
	public static Link makePageLink(int page, int size, String rel) {
		String path = ServletUriComponentsBuilder.fromCurrentRequest()
				.replaceQueryParam("page", page)
		        .replaceQueryParam("size",size)
		        .build()
		        .toUriString();
		    Link link = new Link(path,rel);
		    return link;
	 }
}
