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
package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import java.beans.PropertyDescriptor;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.GeneralSecurityException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;
import javax.persistence.Table;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.GsonFactoryBean;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import edu.cmu.cs.lti.discoursedb.annotation.brat.io.BratService;
import edu.cmu.cs.lti.discoursedb.annotation.lightside.io.LightSideService;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingBratExportResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingContributionResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingDatabasesResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingDiscoursePartResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingDiscourseResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingLightsideStubsResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingStatsResource;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingUserResource;
import edu.cmu.cs.lti.discoursedb.api.query.DdbQuery;
import edu.cmu.cs.lti.discoursedb.api.query.DdbQuery.DdbQueryParseException;
import edu.cmu.cs.lti.discoursedb.core.configuration.DatabaseSelector;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscourseToDiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.ContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartContributionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRelationRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.macro.DiscourseToDiscoursePartRepository;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemUserRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.DiscoursePartInteractionRepository;
import edu.cmu.cs.lti.discoursedb.core.repository.user.UserRepository;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.system.service.system.SystemUserService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUserProperty;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;



@RestController
@CrossOrigin(origins="*", maxAge=3600)
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
	private DiscourseToDiscoursePartRepository discourseToDiscoursePartRepository;

	@Autowired
	private SystemUserService systemUserService;
	
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
	
	@Autowired
	private HttpSession httpSession;
	
	@Autowired PagedResourcesAssembler<BrowsingDiscoursePartResource> praDiscoursePartAssembler;
	@Autowired PagedResourcesAssembler<BrowsingDiscourseResource> praDiscourseAssembler;
	@Autowired PagedResourcesAssembler<BrowsingContributionResource> praContributionAssembler;
	@Autowired PagedResourcesAssembler<BrowsingBratExportResource> praBratAssembler;
	@Autowired PagedResourcesAssembler<BrowsingLightsideStubsResource> praLSAssembler;
	@Autowired PagedResourcesAssembler<BrowsingUserResource> praUserAssembler;
	@Autowired SecurityUtils securityUtils;
	@Autowired 
	private DiscoursePartService discoursePartService;
	//@Autowired private SystemUserRepository sysUserRepo;
	@Autowired private SystemUserService sysUserSvc;
	@Autowired private ApplicationContext appContext;
	@Autowired private AnnotationService annoService;
	@Autowired DatabaseSelector selector;
	
	
	void registerDb(HttpServletRequest httpServletRequest, HttpSession session, String databaseName) {
		securityUtils.authenticate(httpServletRequest, session);
		securityUtils.loggedInUser().authorizedDatabaseCheck(databaseName);
		selector.changeDatabase(databaseName);
	}
	
	
	@RequestMapping(value="/logout", method=RequestMethod.GET)
	@ResponseBody
	void logout(HttpServletRequest httpServletRequest, HttpSession session) {
		securityUtils.abandonSession(session);
	}
	
	@RequestMapping(value="/whoAmI", method=RequestMethod.GET)
	@ResponseBody
	String whoAmI(HttpServletRequest httpServletRequest, HttpSession session) {
		SystemUserAuthentication user = securityUtils.loggedInUser();
		return user.getName();
	}
	
	@RequestMapping(value="/database/{databaseName}/stats", method=RequestMethod.GET)
	@ResponseBody
	Resources<BrowsingStatsResource> stats(
			@PathVariable("databaseName") String databaseName,
			HttpServletRequest httpServletRequest, HttpSession session) {
		registerDb(httpServletRequest, session, databaseName);
		
		BrowsingStatsResource bsr = new BrowsingStatsResource(discourseRepository, discoursePartRepository, contributionRepository, userRepository, securityUtils);
		
		List<BrowsingStatsResource> l = new ArrayList<BrowsingStatsResource>();
		l.add(bsr);
		Resources<BrowsingStatsResource> r =  new Resources<BrowsingStatsResource>(l);
		/*for (String t: bsr.getDiscourseParts().keySet()) {
			r.add(makeLink("/browsing/repos?repoType=" + t, t));			
		}
		r.add(makeLink("/browsing/bratExports", "BRAT markup"));
		r.add(makeLink("/browsing/lightsideExports", "Lightside exports"));
		*/
		
		return r;
	}
	
	// UNauthenticated endpoint to list items available to a user.
	// Probably this is a bad practice because it lets a hacker explore too much.
	// However it's necessary because Learnsphere Tigris widgets need to present options to the
	// user, and those options are currently filled in from an insecure javascript iframe.
	@RequestMapping(value="/user_access", method=RequestMethod.GET)
	@ResponseBody
	Map<String, List<String>> userAccess(HttpServletRequest httpServletRequest, HttpSession session
			,@RequestParam(value= "userid", defaultValue = "") String userid) {
		
		SystemUserAuthentication userInQuestion = 
				userid.contains("@")
				        ?securityUtils.getUserByEmail(userid)
						:securityUtils.getUserByUsername(userid);
		HashMap<String,List<String>> hm = new HashMap<String,List<String>>();
		
		List<String> dbs = userInQuestion.getAllowedDatabases();
		
		List<SystemUserProperty> props = systemUserService.getPropertyList(userInQuestion.getSystemUser()); 
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			for (String db: dbs) {
				hm.put(db, new ArrayList());
			}
			for (SystemUserProperty p: props) {
				if (p.getPropType().equals("query")) {
					DdbQuery q;
					try {
						q = new DdbQuery(p.getPropValue());
						if (!hm.containsKey(q.getDatabaseName())) {
							logger.error("Query " + p.getPropName() + " has bogus database " + q.getDatabaseName() + ": skipping");
						} else {
							hm.get(q.getDatabaseName()).add(mapper.writeValueAsString(p));
						}
					} catch (DdbQueryParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.info("Could not output database/query information for " + userid);
			e.printStackTrace();
			return null;
		}
				
		return hm;
	}
	
	@RequestMapping(value="/databases", method=RequestMethod.GET)
	@ResponseBody
	Resources<BrowsingDatabasesResource> databases(HttpServletRequest httpServletRequest, HttpSession session) {
		securityUtils.authenticate(httpServletRequest,  session);

		BrowsingDatabasesResource bsr = new BrowsingDatabasesResource(securityUtils.loggedInUser().getAllowedDatabases());
		
		List<BrowsingDatabasesResource> l = new ArrayList<BrowsingDatabasesResource>();
		l.add(bsr);
		Resources<BrowsingDatabasesResource> r =  new Resources<BrowsingDatabasesResource>(l);
				
		return r;
	}
	
	@RequestMapping(value="/roles", method=RequestMethod.GET)
	@ResponseBody
	List<String> roles(HttpServletRequest httpServletRequest, HttpSession session) {
		securityUtils.authenticate(httpServletRequest,  session);
		return securityUtils.loggedInUser().allowedRoles();
	}
	
	@RequestMapping(value="/refresh", method=RequestMethod.GET)
	@ResponseBody
	void refresh(HttpServletRequest httpServletRequest, HttpSession session) {
		securityUtils.authenticate(httpServletRequest,  session);
		systemUserService.refreshSystemDatabase();
				
		return;
	}
	
	
	
	@ResponseStatus(value=HttpStatus.UNAUTHORIZED, reason="User does not have access to this database") 
	static public class UnauthorizedDatabaseAccess extends RuntimeException {
		
	}

	
	@RequestMapping(value="/database/{databaseName}/discourses/{discourseId}", method=RequestMethod.GET)
	@CrossOrigin(origins="*", maxAge=3600)
	@ResponseBody
	@Secured("ADMIN")
	Resource<BrowsingDiscourseResource> discourses(
			   @PathVariable("databaseName") String databaseName,
			   @PathVariable("discourseId") Long discourseId, HttpServletRequest hsr, HttpSession session
			   ) {
		registerDb(hsr, session,databaseName);
		BrowsingDiscourseResource bsr = new BrowsingDiscourseResource(discourseRepository.findOne(discourseId).get(), discoursePartRepository);
		
		
		Resource<BrowsingDiscourseResource> r =  new Resource<BrowsingDiscourseResource>(bsr);
		logger.info("currently logged in is "+ SecurityContextHolder.getContext().getAuthentication().getPrincipal());
	    logger.info("another check " + httpSession.getAttribute("sch"));
		return r;
		
	}
	
	@RequestMapping(value = {"/database/{databaseName}/discourses/{discourseId}/discoursePartTypes/{discoursePartType}",
			"/discourses/{discourseId}/discoursePartTypes"} , method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> discoursePartsByTypeAndDiscourse(
														   @RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("databaseName") String databaseName,
														   @PathVariable("discourseId") Long discourseId,
														   @PathVariable("discoursePartType") Optional<String> discoursePartType,
														   @RequestParam(value="annoType", defaultValue="*") String annoType,
														   HttpServletRequest hsr, HttpSession session) {
		registerDb(hsr, session, databaseName);
		PageRequest p = new PageRequest(page,size);
		
		Page<BrowsingDiscoursePartResource> repoResources = null;
		
		if (!discoursePartType.isPresent()) {
			repoResources = 
					discoursePartRepository.findAllByDiscourse(discourseId, p)
					.map(dp -> new BrowsingDiscoursePartResource(dp, annoService))
					.map(bdpr -> {bdpr.filterAnnotations(annoType); 
					              bdpr.fillInUserInteractions(discoursePartInteractionRepository, annoService);
					              return bdpr; });
		} else {
			repoResources = 
				discoursePartRepository.findAllByDiscourseAndType(discoursePartType.get(), discourseId, p)
				.map(dp -> new BrowsingDiscoursePartResource(dp, annoService))
				.map(bdpr -> {bdpr.filterAnnotations(annoType); 
				              bdpr.fillInUserInteractions(discoursePartInteractionRepository, annoService);
				              return bdpr; });
		}
		
		repoResources.forEach(bcr -> {
			if (bcr.getContainingDiscourseParts().size() > 1) { 
				bcr._getContainingDiscourseParts().forEach(
			     (dpId, dpname) -> {
			    	 bcr.add(makeLink("/browsing/database/" + databaseName + "/usersInDiscourseParts/" + dpId, "users in " + dpname));
			    	 bcr.add(makeLink("/browsing/database/" + databaseName + "/subDiscourseParts/" + dpId, dpname));
			     });
			}  
			//Link check = makeLink1Arg("/browsing/action/exportLightside","chk:Export to Lightside: no annotations", "parentDpId", Long.toString(bcr._getDpId()));
	    	//Link check2 = makeLink2Arg("/browsing/action/exportLightside","chk:Export to Lightside: with annotations", "withAnnotations", "true", "parentDpId", Long.toString(bcr._getDpId()));
			Link check = makeLightsideExportNameLink("/browsing/action/database/" + databaseName + "/exportLightside",false,"chk:Export to Lightside, no annotations", bcr.getName(), Long.toString(bcr._getDpId()));
	    	Link check2 = makeLightsideExportNameLink("/browsing/action/database/" + databaseName + "/exportLightside",true,"chk:Export to Lightside, with annotations", bcr.getName(), Long.toString(bcr._getDpId()));
	    	bcr.add(check);	
	    	bcr.add(check2);
	    	Link check3 = makeBratExportNameLink("/browsing/action/database/" + databaseName + "/exportBratItem","chk:Export to BRAT", bcr.getName(),  Long.toString(bcr._getDpId()));
	    	bcr.add(check3);
 		});
		
		PagedResources<Resource<BrowsingDiscoursePartResource>> response = praDiscoursePartAssembler.toResource(repoResources);

		return response;
	}
	
	
	//@Secured("ROLE_ADMIN")
	@RequestMapping(value = "/database/{databaseName}/discourses", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscourseResource>> discourse(@PathVariable("databaseName") String databaseName,
															@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   HttpServletRequest hsr, HttpSession session
														   )  {
		registerDb(hsr, session, databaseName);
		PageRequest p = new PageRequest(page,size);
		
			Page<BrowsingDiscourseResource> discourseResources = discourseRepository.findAll(p).map(b -> new BrowsingDiscourseResource(b, discoursePartRepository));
					
/*			discourseResources.forEach(
			    bcr -> {
			    	Long dpId = bcr.getDiscourseId();
				    Link check0 = makeLink2Arg("/browsing/action/exportBratItem","chk:Export to BRAT", "parentDpId", dpId.toString(), "childDpId", Long.toString(bcr._getDpId()));
				    bcr.add(check0);
			    	Link check = makeLink1Arg("/browsing/action/exportLightside","chk:Export to Lightside: no annotations", "parentDpId", Long.toString(bcr._getDpId()));
			    	Link check2 = makeLink2Arg("/browsing/action/exportLightside","chk:Export to Lightside: with annotations", "withAnnotations", "true", "parentDpId", Long.toString(bcr._getDpId()));
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

	
	/*
	@RequestMapping(value = "/database/{databaseName}/repos", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> discourseParts(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("databaseName") String databaseName,
														   @RequestParam("repoType") String repoType,
														   @RequestParam(value="annoType", defaultValue="*") String annoType,
														   HttpServletRequest hsr, HttpSession session) {
		registerDb(hsr, session, databaseName);
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
			//Link check = makeLink1Arg("/browsing/action/exportLightside","chk:Export to Lightside: no annotations", "parentDpId", Long.toString(bcr._getDpId()));
	    	//Link check2 = makeLink2Arg("/browsing/action/exportLightside","chk:Export to Lightside: with annotations", "withAnnotations", "true", "parentDpId", Long.toString(bcr._getDpId()));
			Link check = makeLightsideExportNameLink("/browsing/action/exportLightside",false,"chk:Export to Lightside, no annotations", bcr.getName(), Long.toString(bcr._getDpId()));
	    	Link check2 = makeLightsideExportNameLink("/browsing/action/exportLightside",true,"chk:Export to Lightside, with annotations", bcr.getName(), Long.toString(bcr._getDpId()));
	    	bcr.add(check);	
	    	bcr.add(check2);
	    	Link check3 = makeBratExportNameLink("/browsing/action/exportBratItem","chk:Export to BRAT", repoType,  Long.toString(bcr._getDpId()));
	    	bcr.add(check3);
		});
		
		PagedResources<Resource<BrowsingDiscoursePartResource>> response = praDiscoursePartAssembler.toResource(repoResources);

		return response;
	}
	*/
	
	public String sanitize(String name) {
		return name.replaceAll("[^a-zA-Z0-9]", "_");
	}
	public String sanitize_dirname(String name) {
		return name.replaceAll("[^a-zA-Z0-9\\._]", "_");
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
	public String exportFile2LightSideTempName(String exportFile, boolean withAnnotations) {
		return exportFile.replaceAll("[^a-zA-Z0-9]", "_") + (withAnnotations?"_annotated":"") + ".csv".toString();
	}
	public String exportFile2LightSideDir(String exportFile, boolean withAnnotations) {
		return exportFile.replaceAll("[^a-zA-Z0-9]", "_") + (withAnnotations?"_annotated":"").toString();
	}
	
	@RequestMapping(value = "/action/database/{databaseName}/deleteLightside", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingLightsideStubsResource>> deleteLightside(
			@PathVariable("databaseName") String databaseName,
			@RequestParam(value= "exportFilename") String exportFilename,
			@RequestParam(value="withAnnotations", defaultValue = "false") boolean withAnnotations,
			   HttpServletRequest hsr, HttpSession session) 
					throws IOException {
		registerDb(hsr,session,databaseName);
		String lsDataDirectory = lsDataDirectory();
		File lsOutputFilename = new File(lsDataDirectory , exportFile2LightSideDir(exportFilename, withAnnotations));
		logger.info("DeleteLightside: dd:" + lsDataDirectory + " ef:" + exportFilename + 
				" wa:" + withAnnotations + " => "
				+ lsOutputFilename.toString());
		for (File f: lsOutputFilename.listFiles()) {
			f.delete();
		}
		lsOutputFilename.delete();
		return lightsideExports(databaseName, hsr, session);
	}
	
	@Deprecated
	@CrossOrigin(origins="*", maxAge=3600)
    @RequestMapping(value="/tokensigningoogle_deprecated", method = RequestMethod.POST, headers = "content-type=application/x-www-form-urlencoded")
    public String processRegistration(@RequestParam("idtoken") String idTokenString) //, ModelMap model)
            throws GeneralSecurityException, IOException {
		logger.info("Doing tokensigningoogle");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Arrays.asList(environment.getRequiredProperty("google.client_id"))).setIssuer("accounts.google.com").build();

        GoogleIdToken idToken = verifier.verify(idTokenString);
        if (idToken != null) {
            Payload payload = idToken.getPayload();
            // Print user identifier
            String userId = payload.getSubject();
            // Get profile information from payload
            String email = payload.getEmail();
            logger.info("Logged in " + userId + " " + email);
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            //List<User> users = DbFunction.listHqlNew("FROM User WHERE email = :email", "email", email);

            if (!emailVerified ) { //|| users.isEmpty()) {
                return "/error.html";
            } else {
                //List<String> roles = DbFunction.listSQLNew(
                //        "SELECT role.name FROM user_role_association JOIN role ON role.id = role_id JOIN user on user.id = user_id WHERE user.email = :email",
                //        "email", email);

                List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
                //for (String role : roles) {
                //    authorities.add(new SimpleGrantedAuthority(role));
                //}
                authorities.add(new SimpleGrantedAuthority("USER_AUTH0RITY"));

                UserDetails userDetails = new org.springframework.security.core.userdetails.User(userId,
                                "xxy", true, true, true, true, authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userId, null,
                        userDetails.getAuthorities());
                //UserDetails userDetails = new org.springframework.security.core.userdetails.User(users.get(0).getName(),
                //        "xx", users.get(0).isEnabled(), true, true, true, authorities);
                //Authentication authentication = new UsernamePasswordAuthenticationToken(users.get(0).getName(), null,
                //        userDetails.getAuthorities());
                SecurityContextHolder.clearContext();
                SecurityContextHolder.getContext().setAuthentication(authentication);
                httpSession.setAttribute("sch", userDetails);
        	    logger.info("first check " + httpSession.getAttribute("sch"));

                return "/browsing/databases";
            }
        } else {
            System.out.println("Invalid ID token.");
        }
        return "/error.html";
    }

	
	
	@RequestMapping(value = "/action/database/{databaseName}/uploadLightside", headers="content-type=multipart/*", method=RequestMethod.POST)
	@ResponseBody
	PagedResources<Resource<BrowsingLightsideStubsResource>> uploadLightside(
			@RequestParam("file_annotatedFileForUpload") MultipartFile file_annotatedFileForUpload,
			@PathVariable("databaseName") String databaseName,
			   HttpServletRequest hsr, HttpSession session) 
					throws IOException {
		registerDb(hsr,session, databaseName);
		String lsDataDirectory = lsDataDirectory();
		logger.info("Someone uploaded something!");
		if (!file_annotatedFileForUpload.isEmpty()) {
			try {
				logger.info("Not even empty!");
				File tempUpload = File.createTempFile("temp-file-name", ".csv");
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(tempUpload));
                FileCopyUtils.copy(file_annotatedFileForUpload.getInputStream(), stream);
				stream.close();
				lightsideService.importAnnotatedData(tempUpload.toString());
			} catch (Exception e) {
				logger.error("Error importing to lightside: " + e);
			}
		}
		return lightsideExports(databaseName, hsr, session);
	}
	
	
	@RequestMapping(value = "/action/downloadQueryCsv/discoursedb_data.csv", method=RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	@ResponseBody
	String downloadQueryCsv(
			HttpServletResponse response,
			@RequestParam("query") String query,
			   HttpServletRequest hsr, HttpSession session) 
					throws IOException {
		
		securityUtils.authenticate(hsr, session);
		response.setContentType("application/csv; charset=utf-8");
		response.setHeader( "Content-Disposition", "attachment");
		
		
		try {
			logger.info("Got query for csv: " + query );
		    
			DdbQuery q = new DdbQuery(selector, discoursePartService, query);
			
			Page<BrowsingContributionResource> lbcr =  
					q.retrieveAllContributions().map(c -> new BrowsingContributionResource(c,annoService));
			
			
			StringBuilder output = new StringBuilder();
			ArrayList<String> headers = new ArrayList<String>();
			for(PropertyDescriptor pd: BeanUtils.getPropertyDescriptors(BrowsingContributionResource.class)) {
				String name = pd.getName();
				if (!name.equals("class") && !name.equals("id") && !name.equals("links"))  {
					headers.add(name);
				}
			}
			output.append(String.join(",",  headers));   output.append("\n");
			
			for(BrowsingContributionResource bcr: lbcr.getContent()) {
				String comma = "";
				BeanWrapper wrap = PropertyAccessorFactory.forBeanPropertyAccess(bcr);
				for (String hdr : headers) {
					
					String item =  "";
					try {
						item = wrap.getPropertyValue(hdr).toString();
						item = item.replaceAll("\"", "\"\"");
						item = item.replaceAll("^\\[(.*)\\]$", "$1");
					} catch (Exception e) {
						logger.info(e.toString() + " For header " + hdr + " item " + item);
						item = "";
					}
					if (hdr.equals("annotations") && item.length() > 0) {
						logger.info("Annotation is " + item);
					}
					output.append(comma + "\"" + item + "\"");
					comma = ",";
				}
				output.append("\n");
			}
			return output.toString();
		} catch (Exception e) {
			return "ERROR:" + e.getMessage();
		}
	}
	
	@RequestMapping(value = "/action/downloadQueryCsvExpandible/discoursedb_data.csv", method=RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	@ResponseBody
	String downloadQueryCsvExpandable(
			HttpServletResponse response,
			@RequestParam("query") String query,
			   HttpServletRequest hsr, HttpSession session) 
					throws IOException {
		
		securityUtils.authenticate(hsr, session);
		response.setContentType("application/csv; charset=utf-8");
		response.setHeader( "Content-Disposition", "attachment");
		
		
		try {
			logger.info("Got query for csv: " + query );
		    
			DdbQuery q = new DdbQuery(selector, discoursePartService, query);
			

			Stream<List<String>> csv1 = q.retrieveAllContributions().getContent().stream().map(
					(Contribution cntr) -> q.fillInColumns(cntr, annoService));
					
			StringBuilder output = new StringBuilder();
			List<String> headers = q.getColumns();

			output.append(String.join(",",  headers));   output.append("\n");
			
			csv1.forEach( csv -> {
				String comma = "";
				for (String c : csv) {
					
					String item =  "";
					try {
						item = c.replaceAll("\"", "\"\"");
						item = item.replaceAll("^\\[(.*)\\]$", "$1");
					} catch (Exception e) {
						item = "";
					}
					
					output.append(comma + "\"" + item + "\"");
					comma = ",";
				}
				output.append("\n");
			});
			return output.toString();
		} catch (Exception e) {
			return "ERROR:" + e.getMessage();
		}
	}
	
	@RequestMapping(value = "/action/downloadLightsideQuery/{exportFilename}.csv", method=RequestMethod.GET, produces = "text/csv;charset=UTF-8")
	@ResponseBody
	String downloadLightsideQuery(
			HttpServletResponse response,
			@RequestParam(value="query") String query,
			@PathVariable(value= "exportFilename") String exportFilename,
			@RequestParam(value="withAnnotations", defaultValue = "false") String withAnnotations,
			   HttpServletRequest hsr, HttpSession session) 
					throws IOException, DdbQueryParseException {
		
		DdbQuery q = new DdbQuery(selector, discoursePartService, query);
		registerDb(hsr,session, q.getDatabaseName());

		response.setContentType("application/csv; charset=utf-8");
		response.setHeader( "Content-Disposition", "attachment");
		String lsDataDirectory = lsDataDirectory();
		File lsOutputFile = new File(lsDataDirectory , sanitize(exportFilename) + ".csv");
		if (withAnnotations.equals("true")) {
			//lightsideService.exportAnnotations(q.getDiscourseParts(), lsOutputFileName);
			lightsideService.exportAnnotationsFromContributions(lsOutputFile, q.retrieveAllContributions());
		} else {
			lightsideService.exportDataForAnnotation(lsOutputFile.toString(), 
					q.retrieveAllContributions(), false);
		}
		return FileUtils.readFileToString(lsOutputFile);
	}
	
	/*
	@Deprecated
	@RequestMapping(value = "/action/database/{databaseName}/downloadLightside/{exportFilename}.csv", method=RequestMethod.GET, produces = "text/plain;charset=UTF-8")
	@ResponseBody
	String downloadLightside(
			HttpServletResponse response,
			@PathVariable("databaseName") String databaseName,
			@PathVariable(value= "exportFilename") String exportFilename,
			@RequestParam(value="withAnnotations", defaultValue = "false") String withAnnotations,
			   HttpServletRequest hsr, HttpSession session) 
					throws IOException {
		
		registerDb(hsr,session, databaseName);

		response.setContentType("application/csv; charset=utf-8");
		response.setHeader( "Content-Disposition", "attachment");
		String lsDataDirectory = lsDataDirectory();
		File lsOutputFileDir = new File(lsDataDirectory , sanitize(exportFilename));
		File lsOutputFileName = new File(lsDataDirectory , sanitize(exportFilename) + ".csv");
		logger.info("Looking in directory " + lsOutputFileDir + " derived from " + exportFilename);
		Set<DiscoursePart> dps = Arrays.stream(lsOutputFileDir.listFiles())
				.map((File f) -> discoursePartRepository.findOne(Long.parseLong(f.getName())))
				.filter((Optional<DiscoursePart> o) -> o.isPresent())
				.map(o -> o.get())
				.collect(Collectors.toSet());
				
		if (withAnnotations.equals("true")) {
			logger.info("With annotations ", lsOutputFileName);
			lightsideService.exportAnnotations(dps, lsOutputFileName);
		} else {
			// For multiple discourseParts, need to assemble all the contributions
			logger.info("Without annotations ", lsOutputFileName);
			
			lightsideService.exportDataForAnnotation(lsOutputFileName.toString(), 
					dps.stream()
					.flatMap(targ -> targ.getDiscoursePartContributions().stream())
					.map(dpc -> dpc.getContribution())::iterator, true);
		}
		return FileUtils.readFileToString(lsOutputFileName);
	}
	
	//
	// Given a query string and a query name
	// cycle through all the discourse parts in the query and create a stub file for it
	@RequestMapping(value = "/action/database/{databaseName}/exportLightsideQuery", method=RequestMethod.GET)
	@ResponseBody
	@Deprecated
	String exportLightsideAction(
			@RequestParam("queryname") String queryname,
			@RequestParam("query") String query,
			@RequestParam(value="withAnnotations", defaultValue = "false") boolean withAnnotations,
			   HttpServletRequest hsr, HttpSession session) throws IOException, DdbQueryParseException {
		DdbQuery q = new DdbQuery(selector, discoursePartService, query);
		for (DiscoursePart dp : q.getDiscourseParts()) {
			exportLightsideAction(q.getDatabaseName(), queryname, withAnnotations, dp.getId(), hsr, session);
		}
		return "OK";
	}
	
	@RequestMapping(value = "/action/database/{databaseName}/exportLightside", method=RequestMethod.GET)
	@ResponseBody
	@Deprecated
	PagedResources<Resource<BrowsingLightsideStubsResource>> exportLightsideAction(
			@PathVariable("databaseName") String databaseName,
			@RequestParam(value= "exportFilename") String exportFilename,
			@RequestParam(value="withAnnotations", defaultValue = "false") boolean withAnnotations,
			@RequestParam(value= "dpId") long dpId,
			   HttpServletRequest hsr, HttpSession session) throws IOException {
		Assert.hasText(exportFilename, "No exportFilename specified");
		registerDb(hsr,session, databaseName);

		
		DiscoursePart dp = discoursePartRepository.findOne(dpId).get();
		String lsDataDirectory = lsDataDirectory();
		File lsOutputFilename = new File(lsDataDirectory , exportFile2LightSideDir(exportFilename, withAnnotations));
		logger.info(" Exporting dp " + dp.getName());
		Set<DiscoursePart> descendents = discoursePartService.findDescendentClosure(dp, Optional.empty());
		System.out.println(lsOutputFilename.getAbsoluteFile().toString());
		lsOutputFilename.mkdirs();
		for (DiscoursePart d: descendents) {
			File child = new File(lsOutputFilename, d.getId().toString());
			child.createNewFile();
		}
		return lightsideExports(databaseName, hsr, session);
	}
	

	//
	// We never need to do this.  When you upload a lightside-annotated file, the upload
	// function just imports it and forgets it.
	//
	@RequestMapping(value = "/action/database/{databaseName}/importLightside", method=RequestMethod.GET)
	@ResponseBody
	@Deprecated
	PagedResources<Resource<BrowsingLightsideStubsResource>> importLightsideAction(
			@RequestParam(value= "lightsideDirectory") String lightsideDirectory,
			@PathVariable("databaseName") String databaseName,
			   HttpServletRequest hsr, HttpSession session) throws IOException {
		registerDb(hsr,session, databaseName);

		String liteDataDirectory = lsDataDirectory();		
		lightsideService.importAnnotatedData(liteDataDirectory + "/" + lightsideDirectory);
		Optional<DiscoursePart> dp = lightsideName2DiscoursePartForImport(lightsideDirectory);
		//if (dp.isPresent()) {
		//	return subDiscourseParts(0,20, dp.get().getId());
		//} else {
			return lightsideExports(databaseName, hsr, session);
		//}
	}	*/
	
	@RequestMapping(value = "/action/database/{databaseName}/deleteBrat", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> deleteBrat(
			@RequestParam(value= "bratDirectory") String bratDirectory, 
			@PathVariable("databaseName") String databaseName,
			HttpServletRequest hsr, HttpSession session) 
					throws IOException {
		registerDb(hsr, session,databaseName);
		String bratDataDirectory = bratDataDirectory();
		File bratDir = new File(bratDataDirectory + "/" + sanitize_dirname(bratDirectory));
		if (bratDir.isDirectory()) {
			FileUtils.deleteDirectory(bratDir);
		} else if (bratDir.isFile()) {
			bratDir.delete();
		} 
		return bratExports(databaseName, hsr,session);
	}
	
	public String bratDataDirectory() {
		return environment.getRequiredProperty("brat.data_directory") + "/" + sysUserSvc.getSystemUser().get().getEmail();
	}
	public String lsDataDirectory() {
		return environment.getRequiredProperty("lightside.data_directory") + "/" + sysUserSvc.getSystemUser().get().getEmail();
	}

	
	@RequestMapping(value = "/action/database/{databaseName}/exportBratItem", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> exportBratActionItem(
			@PathVariable("databaseName") String databaseName,
			@RequestParam(value= "exportDirectory") String exportDirectory,
			@RequestParam(value="dpId")  long dpId, HttpServletRequest hsr, HttpSession session
			) throws IOException {
		registerDb(hsr,session, databaseName);

		Assert.hasText(exportDirectory, "No exportDirectory name specified");
		
		String bratDataDirectory = bratDataDirectory();
		DiscoursePart childDp = discoursePartRepository.findOne(dpId).get();
		
		String bratDirectory = bratDataDirectory + "/" + sanitize(exportDirectory);
		logger.info(" Exporting dp " + childDp.getName() + " in BRAT directory " + bratDirectory);
		exportDiscoursePartRecursively(childDp, bratDirectory, new HashSet<DiscoursePart>());
		return bratExports(databaseName, hsr,session);
	}
	

	
	// TODO: MOVE THIS METHOD TO BRAT SERVICE
	Set<DiscoursePart> exportDiscoursePartRecursively(DiscoursePart dp, String bratDirectory, Set<DiscoursePart> exported) 
			throws IOException {
		if (exported.contains(dp)) { return exported; }
		
		Set<DiscoursePart> kids = discoursePartRelationRepository.findAllBySource(dp).
				stream().map(dpr -> dpr.getTarget()).collect(Collectors.toSet());
		//logger.info("Recursive export: " + dp.getId() + " contains " + kids.size() + " kids");
		kids.removeAll(exported);
		//logger.info("Recursive export: " + dp.getId() + " contains " + kids.size() + " NEW kids");
		
		Set<DiscoursePart> exportedNow = exported;

		//NB: used to do this only if len(kids) == 0; but sometimes DPs can contain contributions *and* other DPs
		bratService.exportDiscoursePart(dp, bratDirectory, true);
		exportedNow.add(dp);
		logger.info("Recursive export: " + dp.getId() + " contains " + kids.size() + " NEW kids");
		for(DiscoursePart k: kids) {
			String kidname = bratService.discoursePart2BratName(dp); 
			//dp.getClass().getAnnotation(Table.class).name() + "_"+dp.getId();
			//delete me
			
			//logger.info("About to recurse: kidname = " + kidname + " filename = " + (new File(bratDirectory,kidname)).toString());
			exportedNow.addAll(exportDiscoursePartRecursively(k, (new File(bratDirectory,kidname+"_")).toString(), exportedNow));				
		}
		return exportedNow;
	}

	@RequestMapping(value = "/action/database/{databaseName}/importBrat", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> importBratAction(
			@PathVariable("databaseName") String databaseName,
			@RequestParam(value= "bratDirectory") String bratDirectory, HttpServletRequest hsr, HttpSession session
			) throws IOException {
		registerDb(hsr,session, databaseName);

		
		String bratDataDirectory = bratDataDirectory();

		importBratRecursively(bratDataDirectory + "/" + bratDirectory);
		return bratExports(databaseName, hsr,session);
		/*
		bratService.importDataset(bratDataDirectory + "/" + bratDirectory);
		Optional<DiscoursePart> dp = bratName2DiscoursePart(bratDirectory);
		//if (dp.isPresent()) {
		//	return subDiscourseParts(0,20, dp.get().getId());
		//} else {
			return bratExports();
		//}*/
	}
	
	private void importBratRecursively(String directory) throws IOException {
		bratService.importDataset(directory);
		File dir = new File(directory);
		for (File s : dir.listFiles()) {
			if (s.isDirectory()) {
				importBratRecursively(s.toString());
			}
		}
	}

	@RequestMapping(value = "/database/{databaseName}/lightsideExports", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingLightsideStubsResource>> lightsideExports(
			@PathVariable("databaseName") String databaseName,
			HttpServletRequest hsr, HttpSession session) {
		registerDb(hsr,session, databaseName);

		
		String lightsideDataDirectory = lsDataDirectory();
		List<BrowsingLightsideStubsResource> exported = BrowsingLightsideStubsResource.findPreviouslyExportedLightside(lightsideDataDirectory);
		Page<BrowsingLightsideStubsResource> p = new PageImpl<BrowsingLightsideStubsResource>(exported, new PageRequest(0,100), exported.size());
		for (BrowsingLightsideStubsResource ltstub: p) {
			ltstub.add(makeLink1Arg("/browsing/action/database/" + databaseName + "/deleteLightside", "Delete this export", "exportFilename", ltstub.getName()));
			ltstub.add(makeLightsideDownloadLink("/browsing/action/database/" + databaseName + "/downloadLightside", ltstub.isAnnotated(), "Download", "exportFilename", ltstub.getName()));
		}
		PagedResources<Resource<BrowsingLightsideStubsResource>> ret = praLSAssembler.toResource(p);
		ret.add(makeLink("/browsing/action/database/" + databaseName + "/uploadLightside{?file_annotatedFileForUpload}", "Upload"));
		return ret;
	}
	
	
	@RequestMapping(value = "/database/{databaseName}/bratExports", method=RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingBratExportResource>> bratExports(
			@PathVariable("databaseName") String databaseName,
			HttpServletRequest hsr, HttpSession session) {
		registerDb(hsr,session, databaseName);

		String who = SecurityContextHolder.getContext().
				getAuthentication().getPrincipal().toString();
		String bratDataDirectory = bratDataDirectory();
		List<BrowsingBratExportResource> exported = BrowsingBratExportResource.findPreviouslyExportedBrat(bratDataDirectory);
		Page<BrowsingBratExportResource> p = new PageImpl<BrowsingBratExportResource>(exported, new PageRequest(0,100), exported.size());
		for (BrowsingBratExportResource bber: p) {
			bber.add(makeLink1Arg("/browsing/action/database/" + databaseName + "/importBrat", "Import BRAT markup", "bratDirectory", bber.getName()));
			bber.add(makeBratLink("/index.xhtml#/" + who + "/" + bber.getName(), "Edit BRAT markup"));
			bber.add(makeLink1Arg("/browsing/action/database/" + databaseName + "/deleteBrat", "Delete BRAT markup", "bratDirectory", bber.getName()));
		}
		return praBratAssembler.toResource(p);
	}
	
	@RequestMapping(value = "/database/{databaseName}/subDiscourseParts/{childOf}", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingDiscoursePartResource>> subDiscourseParts(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("databaseName") String databaseName,
														   @PathVariable("childOf") Long dpId,
														   HttpServletRequest hsr, HttpSession session)  {
		registerDb(hsr,session, databaseName);

		
		PageRequest p = new PageRequest(page,size, new Sort("startTime"));
		
		Optional<DiscoursePart> parent = discoursePartRepository.findOne(dpId);
		if (parent.isPresent()) {
			Page<BrowsingDiscoursePartResource> repoResources = 
					discoursePartRelationRepository.findAllTargetsBySource(parent.get(), p)
			/*.map(dpr -> dpr.getTarget())*/.map(dp -> new BrowsingDiscoursePartResource(dp, annoService));
			
			repoResources.forEach(bdp -> bdp.fillInUserInteractions(discoursePartInteractionRepository, annoService));
			repoResources.forEach(
			    bcr -> {
			    	if (bcr.getContainingDiscourseParts().size() > 1) { 
				        bcr._getContainingDiscourseParts().forEach(
				             (childDpId,childDpName) -> {
				            	 bcr.add(
						    		makeLink("/browsing/database/" + databaseName + "/subDiscourseParts/" + childDpId , "Contained in: " + childDpName));
				             }
					    );
				    }
			    	//if (bcr.getContributionCount() > 0) {
				    	Link check3 = makeBratExportNameLink("/browsing/action/database/" + databaseName + "/exportBratItem","chk:Export to BRAT", parent.get().getName(),  Long.toString(bcr._getDpId()));
				    	bcr.add(check3);
			    	//} 
		    		//Link check = makeLink1Arg("/browsing/action/exportLightside","chk:Export to Lightside: no annotations", "parentDpId", Long.toString(bcr._getDpId()));
			    	//Link check2 = makeLink2Arg("/browsing/action/exportLightside","chk:Export to Lightside: with annotations", "withAnnotations", "true", "parentDpId", Long.toString(bcr._getDpId()));
					Link check = makeLightsideExportNameLink("/browsing/action/database/" + databaseName + "/exportLightside", false, "chk:Export to Lightside, no annotations", bcr.getName(), Long.toString(bcr._getDpId()));
			    	Link check2 = makeLightsideExportNameLink("/browsing/action/database/" + databaseName + "/exportLightside", true, "chk:Export to Lightside, with annotations", bcr.getName(), Long.toString(bcr._getDpId()));
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
			response.add(makeLink("/browsing/database/" + databaseName + "/usersInDiscoursePart/" + dpId, "show users"));

			return response;
		} else {
			logger.info("subdiscourseParts(" + dpId + ") : isPresent==false");
			return null;
		}
	}
	
	
	@RequestMapping(value = "/database/{databaseName}/usersInDiscoursePart/{dpId}", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingUserResource>> users(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("databaseName") String databaseName,
														   @PathVariable("dpId") Long dpId,
														   HttpServletRequest hsr, HttpSession session)  {
		registerDb(hsr,session, databaseName);

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
	
	
	@RequestMapping(value = "/database/{databaseName}/dpContributions/{childOf}", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingContributionResource>> dpContributions(@RequestParam(value= "page", defaultValue = "0") int page, 
														   @RequestParam(value= "size", defaultValue="20") int size,
														   @PathVariable("databaseName") String databaseName,
														   @PathVariable("childOf") Long dpId
														   , HttpServletRequest hsr, HttpSession session)  {
		registerDb(hsr,session, databaseName);

		PageRequest p = new PageRequest(page,size, new Sort("startTime"));
		
		Optional<DiscoursePart> parent = discoursePartRepository.findOne(dpId);
		if (parent.isPresent()) {
			Page<BrowsingContributionResource> lbcr = 
					discoursePartContributionRepository.findByDiscoursePartSorted(parent.get(), p)
					.map(dpc -> dpc.getContribution())
					.map( c -> new BrowsingContributionResource(c,annoService));
			lbcr.forEach(bcr -> {if (bcr.getDiscourseParts().size() > 1) { bcr._getDiscourseParts().forEach(
					     (dpId2, dpName2) -> {
					        if (dpId2 != dpId) { bcr.add(
						    	makeLink("/browsing/database/" + databaseName + "/dpContributions/" + dpId2 , "Also in:" + dpName2)); }}  ); }} );
			PagedResources<Resource<BrowsingContributionResource>> response = praContributionAssembler.toResource(lbcr);
			
			//response.add(makeLink("/browsing/subDiscourseParts{?page,size,repoType,annoType}", "search"));
			response.add(makeLink("/browsing/database/" + databaseName + "/usersInDiscoursePart/" + dpId, "show users"));
			return response;
		} else {
			return null;
		}
	}
	
	
	/* TO DO:
	 *     /prop_get  :  (pname, ptype) -> pvalue
	 */
	
	
	/* Set of endpoints for manipulating system_user properties
	*
	*/
	@RequestMapping(value = "/prop_list", method = RequestMethod.GET)
	@ResponseBody
	String prop_list(@RequestParam(value="ptype", defaultValue="*") String ptype,
			                                                HttpServletRequest hsr, HttpSession session)  {
		logger.info("Authenticating /prop_list");
		securityUtils.authenticate(hsr, session);
		logger.info("Authenticated /prop_list");
		
		logger.info("Requested property list of type " + ptype + " for user " + securityUtils.currentUserEmail());
	
		List<SystemUserProperty> props = systemUserService.getPropertyList().stream()
				.filter(p -> p.getPropType().equals(ptype))
	            .collect(Collectors.toList());
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(props);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			logger.info("Could not output property list!");
			e.printStackTrace();
			return "";
		}
	}
	
	@RequestMapping(value = "/prop_add", method = RequestMethod.POST)
	@ResponseBody
	String prop_add(@RequestParam(value="ptype") String ptype,
			@RequestParam(value="pname") String pname,
			@RequestParam(value="pvalue") String pvalue,
			HttpServletRequest hsr, HttpSession session)  {
		logger.info("Authenticating /prop_add");
		securityUtils.authenticate(hsr,  session);
		logger.info("Authenticated /prop_add");
		
		logger.info("Creating property of type " + ptype + " named " + pname + " value= string of length " 
				+ Integer.toString(pvalue.length()) + " for user " + securityUtils.currentUserEmail());
	
		systemUserService.createProperty(ptype, pname, pvalue);
		return prop_list(ptype, hsr, session);
	}
	
	@RequestMapping(value = "/prop_del", method = RequestMethod.GET)
	@ResponseBody
	String prop_del(@RequestParam(value="ptype") String ptype,
			@RequestParam(value="pname") String pname,
			HttpServletRequest hsr, HttpSession session)  {
		logger.info("Authenticating /prop_del");
		securityUtils.authenticate(hsr,  session);
		logger.info("Authenticated /prop_del");
		
		logger.info("Deleting property of type " + ptype + " named " + pname + 
				" for user " + securityUtils.currentUserEmail());
	
		systemUserService.deleteProperty(ptype, pname);
		return prop_list(ptype, hsr, session);
	}
	
	
	// Note: the page/size parameters have different names here: "start" and "length" to make it
	// play well with a convenient Javascript component, dataTables per
	// documentation here: https://datatables.net/manual/server-side
	// Unfortunately it's not very configurable for things like this.
	@RequestMapping(value = "/query", method = RequestMethod.GET)
	@ResponseBody
	PagedResources<Resource<BrowsingContributionResource>> query(@RequestParam(value= "start", defaultValue = "0") int startposn, 
														   @RequestParam(value= "length", defaultValue="20") int size,
														   @RequestParam("query") String query,
														   HttpServletRequest hsr, HttpSession session)  {
		logger.info("Authenticating /query");
		securityUtils.authenticate(hsr,  session);
		logger.info("Authenticated /query");

		
		int page = (startposn/size);
		PageRequest p = new PageRequest(page,size, new Sort("startTime"));
		
		try {
			logger.info("Got query " + query +"    page=" + Integer.toString(page) + "  size=" + Integer.toString(size));
		    
			DdbQuery q = new DdbQuery(selector, discoursePartService, query);
			Page<BrowsingContributionResource> lbcr =  
					q.retrieveAllContributions(Optional.empty(), p).map(c -> new BrowsingContributionResource(c,annoService));

						
				
			PagedResources<Resource<BrowsingContributionResource>> response = praContributionAssembler.toResource(lbcr);
				
			return response;
		} catch (DdbQueryParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		String path = ServletUriComponentsBuilder.fromCurrentServletMapping() //.fromCurrentRequestUri()
				.replacePath(dest)
			.replaceQueryParam(key, URLEncoder.encode(value))
	        .build()
	        .toUriString();
	    Link link = new Link(path,rel);
	    return link;	
    }
	public static Link makeLink2Arg(String dest, String rel, String key, String value,String key2, String value2) {
		String path = ServletUriComponentsBuilder.fromCurrentServletMapping() //.fromCurrentRequestUri()
				.replacePath(dest)
			.replaceQueryParam(key, URLEncoder.encode(value))
			.replaceQueryParam(key2, URLEncoder.encode(value2))
	        .build()
	        .toUriString();
	    Link link = new Link(path,rel);
	    return link;	
    }
	public static Link makeLightsideExportNameLink(String dest, Boolean withAnnotations, String rel, String filename, String dpid) {
		String path = ServletUriComponentsBuilder.fromCurrentServletMapping() //.fromCurrentRequestUri()
				.replacePath(dest)
			.replaceQueryParam("dpId", URLEncoder.encode(dpid))
			.replaceQueryParam("withAnnotations", withAnnotations)
	        .build()
	        .toUriString();
	    Link link = new Link(path + "{?exportFilename}",rel);
	    return link;	
    }
	
	public static Link makeLightsideDownloadLink(String dest, Boolean annotated, String rel, String key, String value) {
		String path = ServletUriComponentsBuilder.fromCurrentServletMapping() //.fromCurrentRequestUri()
				.replacePath(dest + "/" + value + ".csv")
				.replaceQueryParam("withAnnotations", annotated?"true":"false")
		        .build()
		        .toUriString();
		    Link link = new Link(path,rel);
		    return link;	
	    }
	public static Link makeBratExportNameLink(String dest, String rel, String filename, String dpid) {
		String path = ServletUriComponentsBuilder.fromCurrentServletMapping() //fromCurrentRequestUri()
				.replacePath(dest)
			.replaceQueryParam("dpId", URLEncoder.encode(dpid))
	        .build()
	        .toUriString();
	    Link link = new Link(path + "{?exportDirectory}",rel);
	    return link;	
    }
	public static Link makeLink(String dest, String rel) {
			String path = ServletUriComponentsBuilder.fromCurrentServletMapping() //.fromCurrentRequestUri()
					.replacePath(dest)
		        .build()
		        .toUriString();
		    Link link = new Link(path,rel);
		    return link;	
	}
	
	public static Link makePageLink(int page, int size, String rel) {
		String path = ServletUriComponentsBuilder.fromCurrentServletMapping()
				.replaceQueryParam("page", page)
		        .replaceQueryParam("size",size)
		        .build()
		        .toUriString();
		    Link link = new Link(path,rel);
		    return link;
	 }
}
