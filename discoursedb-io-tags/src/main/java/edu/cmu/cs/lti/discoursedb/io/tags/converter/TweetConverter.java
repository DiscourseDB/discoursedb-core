package edu.cmu.cs.lti.discoursedb.io.tags.converter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import edu.cmu.cs.lti.discoursedb.io.tags.model.TweetInfo;

/**
 * @author Haitian Gong
 *
 */
@Component
public class TweetConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(TweetConverter.class);

	private String dataSetName;
	private String discourseName;

	@Autowired TweetConverterService converterService;

	@Override
	public void run(String... args) throws Exception {
		if (args.length < 3) {
			logger.error("Usage: TweetConverterApplication <DataSourceType> <DataSetName> </path/to/*-prod.mongo>");
			System.exit(1);
		}
		this.dataSetName = args[0];
		String inFileName = args[1];
		this.discourseName = args[2];

		File infile = new File(inFileName);
		if (!infile.exists() || !infile.isFile() || !infile.canRead()) {
			logger.error("Input file does not exist or is not readable.");
			System.exit(1);
		}

		logger.info("Starting twitter conversion");
		convert(inFileName);
	}

	private void convert(String inFile) throws IOException, ParseException {
		
		HashMap<String, ArrayList<String[]>> idContributionMap = new HashMap<String, ArrayList<String[]>>();

		//Phase 1: read through input file once and map all entities
		try(InputStream in = new FileInputStream(inFile)){
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator(',');
			MappingIterator<TweetInfo> it = mapper.readerFor(TweetInfo.class).with(schema).readValues(in);
			while (it.hasNextValue()) {
				TweetInfo t = it.next();
				converterService.map(t,dataSetName,discourseName);
				//create hashmap to build relation between contribution Id and contribution content
				if(idContributionMap.containsKey(t.getText())) {
					String[] origInfo = new String[2];
					origInfo[0] = t.getId_str();
					origInfo[1] = t.getFrom_user();
					idContributionMap.get(t.getText()).add(origInfo);
				}
				else {
					idContributionMap.put(t.getText(), new ArrayList<String[]>());
					String[] origInfo = new String[2];
					origInfo[0] = t.getId_str();
					origInfo[1] = t.getFrom_user();
					idContributionMap.get(t.getText()).add(origInfo);
				}
			}
		}
		
		//Phase 2: read through input file a second time and map all entity relationships
		try(InputStream in = new FileInputStream(inFile)){
			CsvMapper mapper = new CsvMapper();
			CsvSchema schema = mapper.schemaWithHeader().withColumnSeparator(',');
			MappingIterator<TweetInfo> it = mapper.readerFor(TweetInfo.class).with(schema).readValues(in);
			while (it.hasNextValue()) {
				converterService.mapRelation(it.next(),dataSetName, idContributionMap);
			}
		}
	}	

}
