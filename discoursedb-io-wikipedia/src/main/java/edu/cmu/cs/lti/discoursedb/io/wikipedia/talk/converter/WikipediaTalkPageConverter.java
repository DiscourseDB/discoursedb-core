package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.converter;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import de.tudarmstadt.ukp.wikipedia.api.DatabaseConfiguration;
import de.tudarmstadt.ukp.wikipedia.api.WikiConstants.Language;
import de.tudarmstadt.ukp.wikipedia.api.Wikipedia;
import de.tudarmstadt.ukp.wikipedia.api.hibernate.WikiHibernateUtil;
import de.tudarmstadt.ukp.wikipedia.revisionmachine.api.RevisionApi;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.io.RevisionBasedTalkPageExtractor;
import edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model.TalkPage;

/**
 * 
 * @author Oliver Ferschke
 *
 */
@Component
public class WikipediaTalkPageConverter implements CommandLineRunner {

	private static final Logger logger = LogManager.getLogger(WikipediaTalkPageConverter.class);	

	@Autowired private DiscoursePartService discoursePartService;
	@Autowired private DiscourseService discourseService;
	@Autowired private WikipediaTalkPageConverterService converterService;
	@Autowired private DataSourceService dataSourceService;

	@Override
	public void run(String... args) throws Exception {
		if(args.length!=8){			
			throw new RuntimeException("Incorrect number of launch parameters.");
		}
		final String discourseName=args[0];		

		final String dataSetName=args[1];		
		if(dataSourceService.dataSourceExists(dataSetName)){
			logger.warn("Dataset "+dataSetName+" has already been imported into DiscourseDB. Existing pages will be skipped.");			
		}
		
		final String titleListFilename=args[2];
		File titleListFile = new File(titleListFilename);
		if(!titleListFile.exists()||!titleListFile.isFile()){
			logger.error("Title list file "+titleListFilename+" cannot be read. Aborting ... ");			
			return;			
		}
		List<String> titles = FileUtils.readLines(titleListFile);

		logger.trace("Establishing connection to Wikipedia db...");
		DatabaseConfiguration dbconf = new DatabaseConfiguration();
		dbconf.setHost(args[3]);
		dbconf.setDatabase(args[4]);
		dbconf.setUser(args[5]);
		dbconf.setPassword(args[6]);
		dbconf.setLanguage(Language.valueOf(args[7]));
		Wikipedia wiki = new Wikipedia(dbconf);
		RevisionApi revApi = new RevisionApi(dbconf);
		
		RevisionBasedTalkPageExtractor extractor = null;
		logger.info("Start mapping Talk pages for "+titles.size()+" articles to DiscourseDB...");		
		int tpNum = 1;
		for(String title:titles){
			//first check if we alrady have the discussions from this article from a previous import
			if(discoursePartService.exists(discourseService.createOrGetDiscourse(discourseName), title, DiscoursePartTypes.TALK_PAGE)){
				logger.warn("Discussions for article "+title+ "have already been imported. Skipping ...");
				continue;			
			}			

			logger.info("Segmenting Talk Pages for article "+title);
			extractor = new RevisionBasedTalkPageExtractor(wiki, revApi, title, false, true);
			List<TalkPage> talkPages = extractor.getTalkPages();
			for(TalkPage tp:talkPages){
				if(tp!=null){
					logger.info("Mapping Talk Page #"+tpNum++);
					converterService.mapTalkPage(discourseName, dataSetName, title, tp);									
				}
			}
		}
		logger.info("Finished mapping Talk pages.");

		//manually close the hibernate session for the Wikipedia connection which is not managed by Spring
		WikiHibernateUtil.getSessionFactory(dbconf).close();
	}

}