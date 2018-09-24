package edu.cmu.cs.lti.discoursedb.io.csvimporter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.core.configuration.DatabaseSelector;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePartRelation;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.service.user.UserService;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartRelationTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.system.service.system.SystemUserService;
import lombok.NonNull;
import java.util.zip.CRC32;

@SpringBootApplication

@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration",
		"edu.cmu.cs.lti.discoursedb.core","edu.cmu.cs.lti.discoursedb.system",
		"edu.cmu.cs.lti.discoursedb.io.oneThreadedForumCsv"})
@Transactional
public class CsvImportApplication  implements CommandLineRunner {

		static final Logger log = LogManager.getLogger(CsvImportApplication.class);
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
		@Autowired  AnnotationService annoService;
		@Autowired  ContentService contentService;
		@Autowired  ContributionService contributionService;
		@Autowired  DiscoursePartService discoursepartService;
		@Autowired @Qualifier("coreEntityManagerFactory") private EntityManager entityManager;
		
		public static void main(String[] args) {		
			Assert.isTrue(args.length>=2,"Usage: CsvImporter <csvfile> --jdbc.database=<databasename> ");
			SpringApplication.run(CsvImportApplication.class, args);		
		}

		public void run(String... args) throws Exception {
			Assert.isTrue(args.length>=2,"Usage: CsvImporter <csvfile> --jdbc.database=<databasename>");

			
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
		
		public String limit(String identifier, int maxlen) {
			if (identifier.length() > maxlen) {
				checker.reset();
				byte b[] = identifier.getBytes();
				checker.update(b);
				String replacement = identifier.substring(0, maxlen-20) + "#" + String.valueOf(checker.getValue());
				assert replacement.length() <= maxlen;
				return replacement;
			} else {
				return identifier;
			}
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

		HashMap<String,Long> dCache = new HashMap<String,Long>();
		Discourse getDiscourse(String name) {
			if (dpCache.containsKey(name)) {
				return getProxy(dCache.get(name), Discourse.class);
			} else {
				Discourse d = discourseService.createOrGetDiscourse(name);
				dCache.put(name, d.getId());
				return d;
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
		
		CRC32 checker = new java.util.zip.CRC32();
		
		// Given a discourse part specified as a path down the tree from the discourse level,
		// return the discourse part, creating it if necessary.  Discourse parts are specified by
		// name, and dptypes are optionally given.   Use the full path (so far) for each nested
		// discourse part -- this ensures that if you have  CS515/Assign1  and MATH101/Assign1, that
		// Assign1 will be the title of each course's discourse part, but they won't be the same identity in the database.
		DiscoursePart createOrGetDiscoursePartLeaf(String path, Discourse discourse, String dptypes, DataSourceInstance dsi) {
			String dpparts[] = path.split("/");
			DiscoursePart discoursePart = null;
			String dptypeparts[] = dptypes.split("/");
			for (int part = 0; part < dpparts.length; part++) {
				String dppartpath = String.join("/",Arrays.copyOfRange(dpparts, 0, part+1));
				
				dppartpath = limit(dppartpath, 95);
				
				
				String dptype = (dptypeparts.length > part)?dptypeparts[part]:dptypeparts[dptypeparts.length-1];
				DataSourceInfo dsif = new DataSourceInfo(dsi.getEntitySourceDescriptor(),dppartpath,dsi.getDatasetName(), dsi.getSourceType(), DiscoursePartTypes.valueOf(dptype));
				System.out.println("LEN " + String.valueOf(dppartpath.length()) + " -- " + dppartpath);
				DiscoursePart tempDiscoursePart = getDiscoursePart(discourse, dsif);
/*						discoursePartService.createOrGetDiscoursePartByDataSource(discourse, 
								dppartpath, dsi.getEntitySourceDescriptor(), dsi.getSourceType(), dsi.getDatasetName(),
								DiscoursePartTypes.valueOf(dptype));
*/						
				tempDiscoursePart.setName(dpparts[part]);
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
			
			if (contCache.containsKey(id)) {
				return getProxy(contCache.get(id), Contribution.class);
			} 
			/*else {
				Contribution dp = contribution.createOrGetDiscoursePartByDataSource(
						discourse, dsinf.m_id, dsinf.m_descriptor, dsinf.m_dstype, dsinf.m_dataset, dsinf.m_dptype);
				dpCache.put(dsinf.index(), dp.getId());
				return dp;
			}*/
			else { return null; }
		}
		
		int rownum = -1;
		String previous_row_id = "";
		ArrayList<String> annos = null;
		Calendar when = Calendar.getInstance();
		String lastforum = "";
		DataInserter di = new DataInserter(this);
		
		//@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
		private void convert() throws ParseException, IOException {
			when.setTime(new Date());
			for (Map<String,String> row : csvIteratorExistingHeaders(
					this.csvFileName)) { //, '\\')) {
				rownum += 1;
				System.out.println("ROW" + rownum);
				di.insertData(row, this.csvFileName); 
			}
		}
		
		
	}
