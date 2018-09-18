package edu.cmu.cs.lti.discoursedb.io.csvimporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import edu.cmu.cs.lti.discoursedb.configuration.Utilities;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.Feature;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.io.csvimporter.CsvImportApplication.DataSourceInfo;

@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
 class DataInserter {
  /**
	 * 
	 */
	private final CsvImportApplication csvImportApplication;

	/**
	 * @param csvImportApplication
	 */
	DataInserter(CsvImportApplication csvImportApplication) {
		this.csvImportApplication = csvImportApplication;
	}
	
	@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
	
	public void insertData(Map<String,String> row, String csvFileName) {
		// This is a flexible importer that keys off what columns are available in the CSV file;
		// resorting to defaults when information is not available.
		//
	    Utilities.becomeSuperUser();
		if (!row.containsKey("id")) {
			row.put("id", "row_" + Integer.toString(this.csvImportApplication.rownum));
		}
		

		if (!row.containsKey("dataset_file") || row.get("dataset_file").equals("")) {
			row.put("dataset_file", new File(csvFileName).getName().replaceAll("\\.csv", ""));
		}
		if (!row.containsKey("dataset_name")) {
			row.put("dataset_name", row.get("dataset_file"));
		}
		if (!row.containsKey("dataset_id_col")) {
			row.put("dataset_id_col", "id");
		}
		if (!row.containsKey("username")) {
			row.put("username", "(unknown)");
		}
		if (!row.containsKey("user_email")) {
			row.put("user_email", "");
		}
		if (!row.containsKey("forum")) {
			row.put("forum", row.get("dataset_file"));
		}
		if (!row.containsKey("forum_types")) {
			row.put("forum_types", "FORUM/SUBFORUM");
		}
		if (!row.containsKey("replyto")) {
			if (row.get("forum").equals(this.csvImportApplication.lastforum)) { 
				row.put("replyto", this.csvImportApplication.previous_row_id);
			} else {
				row.put("replyto", "");
			}
		}
		this.csvImportApplication.previous_row_id = row.get("id");
		this.csvImportApplication.lastforum = row.get("forum");
		
		if (!row.containsKey("discourse")) {
			row.put("discourse", row.get("dataset_file"));
		}
		if (!row.containsKey("contribution_type")) {
			row.put("contribution_type","POST");
		}
		if (!row.containsKey("title")) {
			row.put("title", "");
		}
		if (!row.containsKey("when")) {
			this.csvImportApplication.when.add(Calendar.SECOND, 1);
		} else {
			this.csvImportApplication.when.setTime(javax.xml.bind.DatatypeConverter.parseDateTime(row.get("when")).getTime());
		}
		
		Discourse curDiscourse = this.csvImportApplication.discourseService.createOrGetDiscourse(row.get("discourse"));
		
		//TODO: use transactions wisely to make efficient for very large input datasets
		//TODO: add optional DataSourceType, DiscoursePartType, DiscourseRelationType, ContributionType
		//TODO: Make robust to re-import
		//TODO: Make robust to different line endings
	
		
		DiscoursePart dp = this.csvImportApplication.createOrGetDiscoursePartLeaf(row.get("forum"), curDiscourse, row.get("forum_types"),
				new DataSourceInstance(row.get("forum"), row.get("dataset_file")+"#"+row.get("dataset_forum_col") + "#path",
				 DataSourceTypes.OTHER, row.get("dataset_name")));
	
		
		// id,username,when,title,post,replyto,forum,dataset_name,dataset_file,dataset_id_col,dataset_id_value,discourse
		
		User u = this.csvImportApplication.userService.createOrGetUser(curDiscourse, row.get("username"));
		u.setEmail(row.get("user_email"));

		this.csvImportApplication.dataSourceService.addSource(u, new DataSourceInstance(
				row.get("id"), row.get("dataset_file")+"#"+row.get("dataset_id_col") + "#username",
				DataSourceTypes.OTHER, row.get("dataset_name")));
		
		Content curContent = this.csvImportApplication.contentService.createContent();
		curContent.setText(row.get("post"));
		curContent.setTitle(row.get("title"));
		curContent.setAuthor(u);
	
		curContent.setStartTime(this.csvImportApplication.when.getTime());
		CsvImportApplication.log.info(row.get("id") + " -- " + row.get("username") + " ---> " + row.get("post"));
		this.csvImportApplication.dataSourceService.addSource(curContent, new DataSourceInstance(
				row.get("id"),row.get("dataset_file")+"#"+row.get("dataset_id_col")+ "#contribution", 
				DataSourceTypes.OTHER, row.get("dataset_name")));
		
		CsvImportApplication.log.trace("Create Contribution entity");
		Contribution curContribution = this.csvImportApplication.contributionService.createTypedContribution(ContributionTypes.valueOf(row.get("contribution_type")));
		curContribution.setCurrentRevision(curContent);
		curContribution.setFirstRevision(curContent);
		curContribution.setStartTime(this.csvImportApplication.when.getTime());
		this.csvImportApplication.dataSourceService.addSource(curContribution, new DataSourceInstance(
				 row.get("id"),row.get("dataset_file")+"#"+row.get("dataset_id_col")+ "#" + row.get("contribution_type"),
				DataSourceTypes.OTHER, row.get("dataset_name")));
		this.csvImportApplication.discoursepartService.addContributionToDiscoursePart(curContribution, dp);
		
		if (this.csvImportApplication.annos == null) {
			this.csvImportApplication.annos = new ArrayList();
			for (String colname: row.keySet()) {
				if (colname.startsWith("annotation_")) {
					this.csvImportApplication.annos.add(colname.substring(11));
				}
			}
			System.out.println(this.csvImportApplication.annos);
		}
		
		/* Multiple one-feature annotations */
		if (this.csvImportApplication.annos.size() > 0) {
			for (String anno: this.csvImportApplication.annos) {
				if (row.get("annotation_" + anno) != null && !row.get("annotation_" + anno).equals("")) {
					AnnotationInstance newAnno = null;
					if (row.containsKey("annotationOwnerEmail")) {
	  					newAnno = this.csvImportApplication.annoService.createTypedAnnotation(anno);
	  					newAnno.setAnnotatorEmail(row.get("annotationOwnerEmail"));
					} else {
					    newAnno = this.csvImportApplication.annoService.createUnownedTypedAnnotation(anno);
					}
					this.csvImportApplication.annoService.addAnnotation(curContribution, newAnno);
					this.csvImportApplication.annoService.saveAnnotationInstance(newAnno);
				
					if (row.get("annotation_" + anno).startsWith("[")) {
						ArrayNode parts = parse(row.get("annotation_" + anno));
						for (JsonNode part: parts) {
							Feature f = this.csvImportApplication.annoService.createFeature(part.asText());
							this.csvImportApplication.annoService.saveFeature(f);
							this.csvImportApplication.annoService.addFeature(newAnno, f);									
						}
					} else {
						Feature f = this.csvImportApplication.annoService.createFeature(row.get("annotation_" + anno));
						this.csvImportApplication.annoService.saveFeature(f);
						this.csvImportApplication.annoService.addFeature(newAnno, f);		
					}
				}
			}
		}
		
		
		
		this.csvImportApplication.contributionService.save(curContribution); 
		this.csvImportApplication.contCache.put(row.get("id"), curContribution.getId());
		
		if (row.get("replyto") != "") {
			Contribution prior = this.csvImportApplication.getContribution(row.get("replyto"));
			if (prior != null) {
				this.csvImportApplication.contributionService.createDiscourseRelation(curContribution, prior, 
					 DiscourseRelationTypes.REPLY);
			}
		}
	}
	
	ArrayNode parse(String json) {
		try {
			ArrayNode node = new ObjectMapper().readValue(new JsonFactory().createParser(json), ArrayNode.class);
			return node;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
}