package edu.cmu.cs.lti.discoursedb.io.salontranscripts.models;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;


public class BulkImportOrder {
	private static final Logger logger = LogManager.getLogger(BulkImportOrder.class);
	
	private String table;
	private String sourceItemKind;
	private String sourceItemId;
	private Map<String,String> parameters;
	
	/**
	 * @param dataset The name for a coherent collection of data being imported
	 * @param table Discoursedb table this record goes into 
	 * @param sourceItemKind  Documents the coding scheme of sourceItemId
	 * @param sourceItemId  Uniquely identifies this datum within the source (non discoursedb) dataset
	 * @param parameters  Key value pairs
	 * 
	 * Key value pairs: key should match discoursedb column names.  In the case of foreign keys
	 * that refer to autonumbered table identities, the value should be a string matching the
	 * sourceItemId; the importerService will take care of hooking these up
	 * 
	 */
	public BulkImportOrder(String dataset, String table, String sourceItemKind, String sourceItemId,
			Map<String, String> parameters) {
		super();
		this.table = table;
		this.sourceItemKind = sourceItemKind;
		this.sourceItemId = sourceItemId;
		this.parameters = parameters;
	}
	
	public BulkImportOrder(String dataset, String table, String sourceItemKind, String sourceItemId) {
		super();
		this.table = table;
		this.sourceItemKind = sourceItemKind;
		this.sourceItemId = sourceItemId;
		this.parameters = new HashMap<String,String>();
	}
	
	
	public BulkImportOrder(String syntax) throws JsonProcessingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode obj = mapper.readTree("[" + syntax + "]");
		assert obj.isArray();
		this.table = obj.get(0).asText();
		this.sourceItemKind = obj.get(1).asText();
		this.sourceItemId = obj.get(2).asText();
		JsonNode params = obj.get(3);
		parameters = new HashMap<String,String>();
		for (String key : (Iterable<String>) () -> params.fieldNames()) {
			parameters.put(key, params.get(key).asText());
		}
	}


	public String getTable() {
		return table;
	}

	public void setTable(String table) {
		this.table = table;
	}

	public String getSourceItemKind() {
		return sourceItemKind;
	}
	
	public String getSourceDescriptor() {
		return this.table + "##" + sourceItemKind;
	}

	public void setSourceItemKind(String sourceItemKind) {
		this.sourceItemKind = sourceItemKind;
	}

	public String getSourceItemId() {
		return sourceItemId;
	}

	public String getKey() { return getTable() + "**" + getSourceItemId(); }
	
	public void setSourceItemId(String sourceItemId) {
		this.sourceItemId = sourceItemId;
	}

	public String getParam(String key) {
		return parameters.get(key);
	}
	public BulkImportOrder put(String key, String value) {
		parameters.put(key, value);
		return this;
	}
	public boolean has(String key) {
		return parameters.containsKey(key);
	}
	
	public Map<String, String> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	

}