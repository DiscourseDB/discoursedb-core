package edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.converter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.cmu.cs.lti.discoursedb.io.wikipedia.talk.model.TalkPage;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
@Service
public class WikipediaTalkPageConverterService{

	private static final Logger logger = LogManager.getLogger(WikipediaTalkPageConverterService.class);
	
	public void mapTalkPage(String discourseName, String dataSetName, String articleTitle, TalkPage tp){
		if(discourseName==null||discourseName.isEmpty()||dataSetName==null||dataSetName.isEmpty()||tp==null){
			logger.error("Cannot map talk page. Data provided is complete. Skipping ...");
			return;
		}		
		System.out.println(articleTitle);
		System.out.println(tp.getTopics().size());
	}

	
}