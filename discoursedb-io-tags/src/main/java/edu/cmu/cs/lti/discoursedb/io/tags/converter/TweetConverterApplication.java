package edu.cmu.cs.lti.discoursedb.io.tags.converter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 
 * This starter class launches the components necessary for importing an TAGS Twitter spreadsheets into DiscourseDB.
 * In particular, it launches the TweetConverter which uses the TweetConverterService to map data to DiscourseDB.
 * 
 * The expected input file is a csv version of the Google sheets produced by see <a href="https://tags.hawksey.info">TAGS v6</a>.
 * Field delimiters should be commas and the encoding should be UTF-8. 
 * 
 * 
 * @author Oliver Ferschke
 * @author Haitian Gong
 *
 */
@SpringBootApplication
@ComponentScan(basePackages={"edu.cmu.cs.lti.discoursedb.configuration","edu.cmu.cs.lti.discoursedb.io.tags"})
public class TweetConverterApplication {
	
	/**
	 * @param args 
	 *     DiscourseName    the name of the dicourse
	 *     DataSetName      the name of the dataset
	 *     TwitterFilePath  the path of TAGS Twitter spreadsheet file
	 */

	public static void main(String[] args) throws Exception {		
		SpringApplication.run(TweetConverterApplication.class, args);
	}

}
