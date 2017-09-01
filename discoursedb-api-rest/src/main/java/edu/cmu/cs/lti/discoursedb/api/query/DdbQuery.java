package edu.cmu.cs.lti.discoursedb.api.query;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;

public class DdbQuery {
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
	
	private DiscoursePartService discoursePartService;
	
	public class DdbQueryParseException extends Exception {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public DdbQueryParseException() { }
		public DdbQueryParseException(String message) { super(message) ; }
	};
	
	public DdbQuery(DiscoursePartService dps, String query) throws DdbQueryParseException {
		discoursePartService = dps;
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
	
	public void parse(String query) throws JsonParseException, JsonMappingException, IOException {
		
		JsonNode node = new ObjectMapper().readValue(new JsonFactory().createParser(query), JsonNode.class);
		if (node.has("rows")) {
			JsonNode rows = node.get("rows");
			if (rows.has("primary")) {
				mainTable = rows.get("primary").asText();
			}
			if (rows.has("discourse_part")) {
				discourseParts = new HashSet<DiscoursePart>();
				rows.get("discourse_part").forEach((JsonNode dp) -> {
					System.out.println("Dereferencing dp " + dp);
					System.out.println("Looking up in " + discoursePartService);
					Optional<DiscoursePart> dpOpt =discoursePartService.findOne(dp.asLong());
					assert dpOpt.isPresent(): "Unknown discourse_part id " + dp.asText() + " mentioned in query";
					discourseParts.add(dpOpt.get());			
				});
			}
		}
		
	}
	public Page<Contribution> retrieveAll(Optional<DiscoursePartRelationTypes> rel, Pageable p) {
		return discoursePartService.findContributionsRecursively(discourseParts, rel, p);
	}
	public void sanityCheck() {
		assert mainTable == "contribution" || mainTable == "content": "Illegal value for main table: " + mainTable;
	}
	public String unparse() {
		return "";
	}
}
