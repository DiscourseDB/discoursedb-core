package edu.cmu.cs.lti.discoursedb.io.salon.converter;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


import edu.cmu.cs.lti.discoursedb.io.salon.converter.SalonConverter;
import edu.cmu.cs.lti.discoursedb.io.salon.converter.SalonConverterService;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonDB;
import edu.cmu.cs.lti.discoursedb.io.salon.models.SalonUser;
import lombok.extern.log4j.Log4j;

@Log4j
@Component

public class SalonConverter implements CommandLineRunner {

	@Autowired SalonConverterService converterService;
	@Autowired Environment env;
	
	public String discourseName;
	public int salonID;
	
	@Override
	public void run(String... args) throws ParseException, SQLException {
		Assert.isTrue(args.length == 2,
				"Usage: SalonConverterApplication <DiscourseName> <SalonID>");

		discourseName = args[0];
		salonID = Integer.parseInt(args[1]);
		// start data import
		
		Assert.hasText(discourseName, "The discourse name has to be specified and cannot be empty.");

		SalonDB salonDB = new SalonDB(env.getRequiredProperty("salon.host"),
        		                      env.getRequiredProperty("salon.database"),
        		                      env.getRequiredProperty("salon.username"),
        		                      env.getRequiredProperty("salon.password"));
		
		log.info("Starting to import Salon data from MySQL database \"" + env.getRequiredProperty("salon.database") + "\" found at \""
				+ env.getRequiredProperty("salon.host") + "\"");
		
		converterService.configure(discourseName, salonID, salonDB);
		long salon = converterService.createSalon(salonID);
		
		// Map from salon doc id to ddb discoursepart id for that document
		Map<Long,Long> docs = converterService.mapDocumentsAsContributions(salon);
		for (long s_doc: docs.keySet()) {
			//List<Long> paras = converterService.mapParagraphs(salon,doc);
			Map<Long,Long> questions = converterService.mapQuestions(salon, s_doc);
			//for (long para: paras) {
			Map<Long,Long> s_annos = converterService.mapSalonAnnotations2DiscourseContributions(salon,s_doc, docs.get(s_doc));
			//}
			
			for (long question:questions.keySet()) {
				Map<Long, Long> responses = converterService.mapResponses(salon, s_doc, question);
				
				//Not implemented yet
				//for (long response: responses) {
				//	converterService.mapResponseParagraphs(salon, s_doc, question, response, paras);
				//}
			}
		}
		List<Long> discs = converterService.mapDiscussions(salon);
		converterService.linkDiscussions(salon, discs);
	
			
	}

}
