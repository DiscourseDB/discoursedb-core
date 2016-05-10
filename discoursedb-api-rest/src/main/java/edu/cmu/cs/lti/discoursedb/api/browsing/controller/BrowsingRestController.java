package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratService;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingBratExportResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingContributionResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingDiscoursePartResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingStatsResource;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.DiscoursePartInteractionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;

@Controller
@RequestMapping(value = "/browsing", produces = "application/hal+json")
public class BrowsingRestController {

	private static final Logger logger = LogManager.getLogger(BrowsingRestController.class);

	@Autowired
	private BratService bratService;
	
	@Autowired 
	private Environment environment;
	
	@Autowired
	private DiscourseRepository discourseRepository;

	@Autowired
	private DiscoursePartRepository discoursePartRepository;

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
	@Autowired PagedResourcesAssembler<BrowsingContributionResource> praContributionAssembler;
	@Autowired PagedResourcesAssembler<BrowsingBratExportResource> praBratAssembler;
	
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

		return r;
	}
	
	
	@RequestMapping(value = "/repos", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> discourseParts(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @RequestParam("repoType") String repoType,
														   @RequestParam(value="annoType", defaultValue="*") String annoType) {
		PageRequest p = new PageRequest(page,size, new Sort("startTime"));
		Page<BrowsingDiscoursePartResource> repoResources = 
				discoursePartRepository.findAllNonDegenerateByType(repoType, p)
				.map(BrowsingDiscoursePartResource::new)
				.map(bdpr -> {bdpr.filterAnnotations(annoType); 
				              bdpr.fillInUserInteractions(discoursePartInteractionRepository);
				              return bdpr; });
				
		repoResources.forEach(bcr -> {if (bcr.getContainingDiscourseParts().size() > 1) { bcr.getContainingDiscourseParts().forEach(
			     dp -> bcr.add(
			    		 makeLink1Arg("/browsing/subDiscoursePartsByName", "Contained in: " + dp, "discoursePartName", dp)));}});
		
		PagedResources<Resource<BrowsingDiscoursePartResource>> response = praDiscoursePartAssembler.toResource(repoResources);

		response.add(makeLink("/browsing/subDiscoursePartsByName{?discoursePartName}", "search"));
		return response;
	}
	
	/*@Autowired private BratThreadExport bratThreadExport;
	
	@RequestMapping(value = "/export/brat", method = RequestMethod.GET)
	@ResponseBody
	void getFile(
				@RequestParam(value= "discourse", defaultValue = "GITHUB") String discourse, 
				@RequestParam(value= "splitOnWhich", defaultValue="THREAD") String splitOnWhich,
				HttpServletResponse response) {
	    try {
	      // get your file as InputStream
	      String zipfile = bratThreadExport.zipBratExport(discourse, splitOnWhich);
	      InputStream is = new FileInputStream(zipfile);
	      // copy it to response's OutputStream
	      IOUtils.copy(is, response.getOutputStream());
	      response.flushBuffer();
	    } catch (Exception ex) {
	      logger.info("Error writing file to output stream. ");
	      throw new RuntimeException("IOError writing file to output stream");
	    }

	}
	*/
	
	@RequestMapping(value = "/subDiscoursePartsByName", method=RequestMethod.GET)
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
	}
	
	public String discoursePart2BratName(DiscoursePart dp) {
		return /*"dp_" + dp.getId().toString() + "_" + */dp.getName().replaceAll("[^a-zA-Z0-9]", "_");
	}
	/*public Optional<DiscoursePart> bratName2DiscoursePart(String filename) {
		Long id = Long.getLong(filename.substring(3));
		return discoursePartRepository.findOne(id);
	}*/
	
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
		return exportBrat();
	}

	@RequestMapping(value = "/action/importBrat", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> importBratAction(@RequestParam(value= "bratDirectory") String bratDirectory) throws IOException {
		String bratDataDirectory = environment.getRequiredProperty("brat.data_directory");		
		bratService.importDataset(bratDataDirectory + "/" + bratDirectory);
		
		return exportBrat();
	}

	
	
	@RequestMapping(value = "/exportBrat", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> exportBrat() {
		String bratDataDirectory = environment.getRequiredProperty("brat.data_directory");
		List<BrowsingBratExportResource> exported = BrowsingBratExportResource.findPreviouslyExported(bratDataDirectory);
		Page<BrowsingBratExportResource> p = new PageImpl<BrowsingBratExportResource>(exported, new PageRequest(0,100), exported.size());
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
			repoResources.forEach(bcr -> {if (bcr.getContainingDiscourseParts().size() > 1) { bcr.getContainingDiscourseParts().forEach(
				     dp -> bcr.add(
				    		 makeLink1Arg("/browsing/subDiscoursePartsByName", "Contained in: " + dp, "discoursePartName", dp)));}});
			PagedResources<Resource<BrowsingDiscoursePartResource>> response = praDiscoursePartAssembler.toResource(repoResources);

			long threadcount = 0L;
			for (BrowsingDiscoursePartResource bdpr: repoResources) {
				threadcount += bdpr.getContributionCount();
			}
			
			if (threadcount > 0) {
				response.add(makeLink1Arg("/browsing/action/exportBrat", "Export these all to BRAT", "parentDpId", dpId.toString()));
			}
			
			return response;
		} else {
			logger.info("subdiscourseParts(" + dpId + ") : isPresent==false");
			return null;
		}
	}
	
	
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
			lbcr.forEach(bcr -> {if (bcr.getDiscourseParts().size() > 1) { bcr.getDiscourseParts().forEach(
					     dp -> bcr.add(
					    		 makeLink1Arg("/browsing/subDiscoursePartsByName", "Contained in: " + dp, "discoursePartName", dp)  )  ); }} );
			PagedResources<Resource<BrowsingContributionResource>> response = praContributionAssembler.toResource(lbcr);
			
			//response.add(makeLink("/browsing/subDiscourseParts{?page,size,repoType,annoType}", "search"));
			return response;
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
