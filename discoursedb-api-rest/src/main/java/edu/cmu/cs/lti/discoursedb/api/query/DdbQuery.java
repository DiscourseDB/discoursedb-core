package edu.cmu.cs.lti.discoursedb.api.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingAnnotationResource;
import edu.cmu.cs.lti.discoursedb.core.configuration.DatabaseSelector;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartContribution;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;

public class DdbQuery {
	String database;
	List<Discourse> discourses;
	Set<DiscoursePart> discourseParts;
	List<DiscoursePartTypes> discoursePartTypes;
	List<ContributionTypes> contributionTypes;
	String mainTable;  // content or contribution
	String where; // not parsed yet.  Find existing expr parser that can list all the variables
					// contained in the expression, let me construct a variable frame, then
	                // apply the expression.  I think hibernate's language will do this.
	List<String> contribution_columns;
	List<String> annotations;
	List<String> features;
	
	private DatabaseSelector databaseSelector;
	private DiscoursePartService discoursePartService;
	private AnnotationService annoService;
	
	public class DdbQueryParseException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public DdbQueryParseException() { }
		public DdbQueryParseException(String message) { super(message) ; }
	};
	
	public DdbQuery(DatabaseSelector selector, DiscoursePartService dps,String query) throws DdbQueryParseException {
		discoursePartService = dps;
		databaseSelector = selector;
		try {
			parse(query);
		} catch (JsonParseException e) {
			System.out.println("Json Parse Error parsing " + query);
			e.printStackTrace();
			throw new DdbQueryParseException("Json Parse Error parsing " + query);
		} catch (JsonMappingException e) {
			System.out.println("Json Mapping Error parsing " + query);
			e.printStackTrace();
			throw new DdbQueryParseException("Json Mapping Error parsing " + query);
		} catch (IOException e) {
			System.out.println("IOError parsing " + query);
			e.printStackTrace();
			throw new DdbQueryParseException("IOError parsing " + query);
		}
	}
	
	public DdbQuery(String query) throws DdbQueryParseException {
		
		try {
			parseQuietly(query);
		} catch (JsonParseException e) {
			System.out.println("Json Parse Error parsing " + query);
			e.printStackTrace();
			throw new DdbQueryParseException("Json Parse Error parsing " + query);
		} catch (JsonMappingException e) {
			System.out.println("Json Mapping Error parsing " + query);
			e.printStackTrace();
			throw new DdbQueryParseException("Json Mapping Error parsing " + query);
		} catch (IOException e) {
			System.out.println("IOError parsing " + query);
			e.printStackTrace();
			throw new DdbQueryParseException("IOError parsing " + query);
		}
	}
	
	// Parse the query just enough to see its contents, without touching anything
	// in any database
	public void parseQuietly(String query) throws JsonParseException, JsonMappingException, IOException {
		JsonNode node = new ObjectMapper().readValue(new JsonFactory().createParser(query), JsonNode.class);
		database = node.get("database").asText();
		if (node.has("rows")) {
			JsonNode rows = node.get("rows");
			if (rows.has("primary")) {
				mainTable = rows.get("primary").asText();
			}			
		}
	}
	
	// Parse the query, switch to its database, and look up all the discoursepart IDs so it
	// can be immediately executed
	public void parse(String query) throws JsonParseException, JsonMappingException, IOException {
		JsonNode node = new ObjectMapper().readValue(new JsonFactory().createParser(query), JsonNode.class);
		database = node.get("database").asText();
		databaseSelector.changeDatabase( database);
		if (node.has("columns")) {
			contribution_columns =  new ArrayList<String>();
			node.get("columns").forEach((JsonNode col) -> {
				contribution_columns.add(col.asText());
			});
		} else {
			contribution_columns = Arrays.asList("annotations,content,contributionId,contributor,discoursePartIds,discourseParts,parentId,startTime,title,type".split(","));
		}
		if (node.has("rows")) {
			JsonNode rows = node.get("rows");
			if (rows.has("primary")) {
				mainTable = rows.get("primary").asText();
			}
			if (rows.has("discourse_part")) {
				discourseParts = new LinkedHashSet<DiscoursePart>();
				rows.get("discourse_part").forEach((JsonNode dp) -> {
					System.out.println("Dereferencing dp " + dp);
					System.out.println("Looking up in " + discoursePartService);
					Optional<DiscoursePart> dpOpt =discoursePartService.findOne(dp.get("dpid").asLong());
					assert dpOpt.isPresent(): "Unknown discourse_part id " + dp.asText() + " mentioned in query";
					discourseParts.add(dpOpt.get());			
				});
			}
		}
		
	}
	
	public String getDatabaseName() { return database; }
	public Set<DiscoursePart> getDiscourseParts() { return discourseParts; }
	public List<String> getColumns() { return contribution_columns; }
	public Page<Contribution> retrieveAllContributions() {
		PageRequest p = new PageRequest(0, Integer.MAX_VALUE, new Sort("startTime"));
		return retrieveAllContributions(Optional.empty(), p);
	}
	public Page<Contribution> retrieveAllContributions(Optional<DiscoursePartRelationTypes> rel, Pageable p) {
		databaseSelector.changeDatabase(database);
		return discoursePartService.findContributionsRecursively(discourseParts, rel, p);
	}
	public void sanityCheck() {
		assert mainTable == "contribution" || mainTable == "content": "Illegal value for main table: " + mainTable;
	}
	public String unparse() {
		return "";
	}
	
	public String getColumnValue(Contribution c, String hdr) {

		switch(hdr) {
		case "type": return c.getType(); 
		case "contributionId": return c.getId().toString(); 
		case "parentId": 
			if (c.getSourceOfDiscourseRelations().size() > 0) {
				return c.getSourceOfDiscourseRelations().iterator().next().getTarget().getId().toString();
			} else { 
				return "0";
			}
		case "content": return c.getCurrentRevision().getText();
		case "title": return c.getCurrentRevision().getTitle();
		case "startTime": return c.getStartTime().toString();
		case "contributor": return c.getCurrentRevision().getAuthor().getUsername();
		case "interactions": 
			List<String> userInteractions = c.getContributionInteractions().stream().map(i -> 
			i.getUser().getUsername() + ": " + i.getType() + " at " + i.getStartTime().toString())
				.collect(Collectors.toList());
			return String.join(";", userInteractions);
		case "preceding_text":
			// TODO: if we want preceding_user and preceding_id eventually, we'll have to find  a way
			//       to do this query up the call stack a little to avoid running it multiple times.  Or cache it I guess.
			Optional<Contribution> prior = c.getSourceOfDiscourseRelations().stream()
				.filter(dr -> dr.getType().equals("REPLY"))
				.map(dr -> dr.getTarget()).findFirst();
			if (prior.isPresent()) { return prior.get().getCurrentRevision().getText(); }
			else { return ""; }
		case "preceding_user":
			// TODO: if we want preceding_user and preceding_id eventually, we'll have to find  a way
			//       to do this query up the call stack a little to avoid running it multiple times.  Or cache it I guess.
			Optional<Contribution> prior2 = c.getSourceOfDiscourseRelations().stream()
				.filter(dr -> dr.getType().equals("REPLY"))
				.map(dr -> dr.getTarget()).findFirst();
			if (prior2.isPresent()) { return prior2.get().getCurrentRevision().getAuthor().getUsername(); }
			else { return ""; }
		case "next_text":
			// TODO: if we want preceding_user and preceding_id eventually, we'll have to find  a way
			//       to do this query up the call stack a little to avoid running it multiple times.  Or cache it I guess.
			Optional<Contribution> nextt = c.getTargetOfDiscourseRelations().stream()
				.filter(dr -> dr.getType().equals("REPLY"))
				.map(dr -> dr.getSource()).findFirst();
			if (nextt.isPresent()) { return nextt.get().getCurrentRevision().getText(); }
			else { return ""; }
		case "annotations":
			ArrayList<BrowsingAnnotationResource> myannotations = new ArrayList<BrowsingAnnotationResource>();
			try {
				myannotations.addAll(annoService.findAnnotations(c).stream().map(a ->
					new BrowsingAnnotationResource(a))
					.collect(Collectors.toList()));
				
			} catch (NullPointerException npe) {
				
			}
			try {
				myannotations.addAll(annoService.findAnnotations(c.getCurrentRevision()).stream().map(a ->
					new BrowsingAnnotationResource(a))
					.collect(Collectors.toList()));
				
			} catch (NullPointerException npe) {
				
				
			}
			return myannotations.toString();
		case "discoursePartIds": 

	   	    List<String> myDiscoursePartIds = new ArrayList<String>();
	   	    for (DiscoursePartContribution dpc : c.getContributionPartOfDiscourseParts()) {
	   	    		myDiscoursePartIds.add(dpc.getDiscoursePart().getId().toString());
	   	    }
	   	    return String.join(",", myDiscoursePartIds);
		case "discourseParts": 

	   	    List<String> myDiscourseParts = new ArrayList<String>();
	   	    for (DiscoursePartContribution dpc : c.getContributionPartOfDiscourseParts()) {
	   	    		myDiscourseParts.add(discoursePartService.fullyQualifiedName(dpc.getDiscoursePart()));
	   	    }
	   	    return String.join(",", myDiscourseParts);   // TODO: escape these
		default: 
			return "Column " + hdr + " is unknown";
		}
	}
	public List<String> fillInColumns(Contribution c, AnnotationService thisAnnoService) {
		annoService = thisAnnoService;
		List<String> output = new ArrayList<String>();
		
		//"annotations,content,contributionId,contributor,discoursePartIds,discourseParts,parentId,startTime,title,type"
	
		for (String hdr: contribution_columns) {
			output.add(getColumnValue(c,hdr));
		}
   	    
		return output;
	}

}
