package edu.cmu.cs.lti.discoursedb.io.csvimporter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.core.configuration.DatabaseSelector;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.model.user.User;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscourseRelationTypes;
import edu.cmu.cs.lti.discoursedb.system.service.system.SystemUserService;
import lombok.NonNull;

@SpringBootApplication
@Transactional
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration",
		"edu.cmu.cs.lti.discoursedb.core","edu.cmu.cs.lti.discoursedb.system",
		"edu.cmu.cs.lti.discoursedb.io.oneThreadedForumCsv"})
public class CsvImportApplication  implements CommandLineRunner {

		private static final Logger log = LogManager.getLogger(CsvImportApplication.class);
		@Autowired DatabaseSelector dbSelector;
		/**
		 * @param args 
		 *     DiscourseName    the name of the discourse
		 *     DataSetName      the name of the dataset
		 *     csvfile           the path of a csv file
		 */
		
		String csvFileName = "";
	
		@Autowired DiscoursePartService discoursePartService;
		@Autowired DiscourseService discourseService;
		@Autowired  DataSourceService dataSourceService;
		@Autowired  UserService userService;
		@Autowired  ContentService contentService;
		@Autowired  ContributionService contributionService;
		@Autowired  DiscoursePartService discoursepartService;
		@Autowired @Qualifier("coreEntityManagerFactory") private EntityManager entityManager;
		
		public static void main(String[] args) {		
			Assert.isTrue(args.length==2,"Usage: CsvImporter <csvfile> --jdbc.database=<databasename> ");
			SpringApplication.run(CsvImportApplication.class, args);		
		}

		public void run(String... args) throws Exception {
			Assert.isTrue(args.length==2,"Usage: CsvImporter <csvfile> --jdbc.database=<databasename>");

			
			this.csvFileName = args[0];
			
			log.info("Starting conversion of " + this.csvFileName);
			
			convert();		
			log.info("Finished conversion");
		}
		
		
		private CsvSchema.Builder mkCsvSchema(String fieldlist) {
			CsvSchema.Builder builder = new CsvSchema.Builder();
			for (String colname : fieldlist.split(",")) {
				builder.addColumn(colname);
			}
			builder.setColumnSeparator(',');
			builder.setUseHeader(false);
			return builder;
		}
		
		private Iterable<Map<String,String>> csvIteratorNoHeaders(String filename, String fieldlist) throws JsonProcessingException, IOException {
			InputStream in = new FileInputStream(filename);
	        MappingIterator<Map<String, String>> iterator = new CsvMapper()
	                .readerFor(Map.class)
	                .with(mkCsvSchema(fieldlist).build())
	                .readValues(in);
	        return () -> iterator;
		}
		
		private Iterable<Map<String,String>> csvIteratorExistingHeaders(String filename) throws JsonProcessingException, IOException {
			return csvIteratorExistingHeaders(filename, (char)-1);
		}
		
		private Iterable<Map<String,String>> csvIteratorExistingHeaders(String filename, char escapeChar) throws JsonProcessingException, IOException {
			//InputStream in = new FileInputStream(filename, "UTF-8");
			InputStreamReader in = new InputStreamReader(new BOMInputStream(new FileInputStream(filename)), "UTF-8"); //"ISO-8859-1");
	        MappingIterator<Map<String, String>> iterator = new CsvMapper()
	                .readerFor(Map.class)
	                .with(CsvSchema.emptySchema().withColumnSeparator(',').withHeader().withEscapeChar(escapeChar))
	                .readValues(in);
	        List<Map<String,String>> sortable = iterator.readAll();
	        if (sortable.get(0).containsKey("discussion_answer_created_ts")) {
	        		sortable.sort(new Comparator<Map<String,String>>() {
	        		    @Override
	        		    public int compare(Map<String,String> lhs, Map<String,String> rhs) {
	        		        return lhs.get("discussion_answer_created_ts").compareTo(rhs.get("discussion_answer_created_ts"));
	        		    }
	        		} );
	        }
	        return () -> sortable.iterator();
		}

		public static
		<T extends Comparable<? super T>> List<T> asSortedList(Collection<T> c) {
		  List<T> list = new ArrayList<T>(c);
		  java.util.Collections.sort(list);
		  return list;
		}
		
		public <T> T getProxy(Long id, Class clazz) {
			Session session = entityManager.unwrap(Session.class);
			return (T) session.get(clazz, id);
		}
		//private static SimpleDateFormat sdf = new SimpleDateFormat("M/d/yyyy");
		
		
		HashMap<String,Long> dpCache = new HashMap<String,Long>();
		DiscoursePart getDiscoursePart(Discourse discourse, DataSourceInfo dsinf) {
			
			if (dpCache.containsKey(dsinf.index())) {
				return getProxy(dpCache.get(dsinf.index()), DiscoursePart.class);
			} else {
				DiscoursePart dp = discoursePartService.createOrGetDiscoursePartByDataSource(
						discourse, dsinf.m_id, dsinf.m_descriptor, dsinf.m_dstype, dsinf.m_dataset, dsinf.m_dptype);
				dpCache.put(dsinf.index(), dp.getId());
				return dp;
			}
		}
		
		public static class DataSourceInfo {
			DataSourceInfo(String descriptor, String id, String dataset, DataSourceTypes dstype, DiscoursePartTypes dptype) {
				m_descriptor = descriptor; m_id=id; m_dataset=dataset; m_dstype=dstype; m_dptype = dptype;
			}
			String m_descriptor;
			DiscoursePartTypes m_dptype;
			String m_id;
			String m_dataset;
			DataSourceTypes m_dstype;
			String index() { return m_id + "@" + m_descriptor; }
			public String toString() { return index() + " type" + m_dptype + " dstype " + m_dstype + " dataset " + m_dataset; }
		}
		
		private DiscoursePart createOrGetDiscoursePartLeaf(String path, Discourse discourse) {
			String dpparts[] = path.split("/");
			DiscoursePart discoursePart = null;
			for (String dppart: dpparts) {
				DiscoursePart tempDiscoursePart = 
						discoursePartService.createOrGetTypedDiscoursePart(discourse, dppart, DiscoursePartTypes.FOLDER);
				if (discoursePart != null) {
					discoursePartService.createDiscoursePartRelation(
							discoursePart, tempDiscoursePart, DiscoursePartRelationTypes.SUBPART);
				} 
				discoursePart = tempDiscoursePart;
			}
			return discoursePart;
		}
		
		/*HashMap<String,Long> dpCache = new HashMap<String,Long>();
		DiscoursePart getDiscoursePartPath(Discourse discourse, String path) {
			
			if (dpCache.containsKey(dsinf.index())) {
				return getProxy(dpCache.get(dsinf.index()), DiscoursePart.class);
			} else {
				DiscoursePart dp = discoursepartService.createOrGetDiscoursePartByDataSource(
						discourse, dsinf.m_id, dsinf.m_descriptor, dsinf.m_dstype, dsinf.m_dataset, dsinf.m_dptype);
				dpCache.put(dsinf.index(), dp.getId());
				return dp;
			}
		}*/
		
		HashMap<String,Long> contCache = new HashMap<String,Long>();
		Contribution getContribution(String id) {
			
			if (dpCache.containsKey(id)) {
				return getProxy(dpCache.get(id), DiscoursePart.class);
			} 
			/*else {
				Contribution dp = contribution.createOrGetDiscoursePartByDataSource(
						discourse, dsinf.m_id, dsinf.m_descriptor, dsinf.m_dstype, dsinf.m_dataset, dsinf.m_dptype);
				dpCache.put(dsinf.index(), dp.getId());
				return dp;
			}*/
			else { return null; }
		}
		
		private void convert() throws ParseException, IOException {
			for (Map<String,String> row : csvIteratorExistingHeaders(
					this.csvFileName, '\\')) {
				
				Discourse curDiscourse = discourseService.createOrGetDiscourse(row.get("discourse"));
				
				if (row.get("dataset_file") == "") {
					row.put("dataset_file", new File(this.csvFileName).getName().replaceAll("\\.csv", ""));
				}
				
				
				// TODO: delete me; probably not needed
				//DataSourceInfo dsi = new DataSourceInfo(row.get("dataset_file")+"#"+row.get("datset_id_col")+ "#discourse",
				//		row.get("id"),row.get("dataset_name"),DataSourceTypes.OTHER,  
				//DiscoursePartTypes.FOLDER);
				
				DiscoursePart dp = createOrGetDiscoursePartLeaf(row.get("forum"), curDiscourse);
			
				// Fields were:  id, username, Date, post, replyto		
				
				//TODO: change fields to:
				// id,username,when,title,post,replyto,forum,dataset_name,dataset_file,dataset_id_col,dataset_id_value,discourse
				
				User u = userService.createOrGetUser(curDiscourse,  row.get("username"));
				dataSourceService.addSource(u, new DataSourceInstance(
						row.get("dataset_file")+"#"+row.get("datset_id_col") + "#username",
						row.get("id"), DataSourceTypes.OTHER, row.get("dataset_name")));
				
				
				Content curContent = contentService.createContent();
				curContent.setText(row.get("post"));
				curContent.setTitle(row.get("title"));
				curContent.setAuthor(u);
			
				curContent.setStartTime(javax.xml.bind.DatatypeConverter.parseDateTime(row.get("when")).getTime());
				log.info(row.get("id") + " -- " + row.get("username") + " ---> " + row.get("post"));
				dataSourceService.addSource(curContent, new DataSourceInstance(
						row.get("dataset_file")+"#"+row.get("datset_id_col")+ "#contribution", row.get("id"),
						DataSourceTypes.OTHER, row.get("dataset_name")));
				
				log.trace("Create Contribution entity");
				Contribution curContribution = contributionService.createTypedContribution(ContributionTypes.POST);
				curContribution.setCurrentRevision(curContent);
				curContribution.setFirstRevision(curContent);
				curContribution.setStartTime(javax.xml.bind.DatatypeConverter.parseDateTime(row.get("when")).getTime());
				dataSourceService.addSource(curContribution, new DataSourceInstance(
						row.get("dataset_file")+"#"+row.get("datset_id_col")+ "#post", row.get("id"),
						DataSourceTypes.OTHER, row.get("dataset_name")));
				discoursepartService.addContributionToDiscoursePart(curContribution, dp);
				
				contCache.put(row.get("id"), curContribution.getId());
				
				if (row.get("replyto") != "") {
					Contribution prior = getContribution(row.get("replyto"));
					if (prior != null) {
						contributionService.createDiscourseRelation(prior, 
							curContribution, DiscourseRelationTypes.REPLY);
					}
				}
			}
			
			
		
		
		
		}
		
	}
