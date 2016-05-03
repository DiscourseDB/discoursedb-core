package edu.cmu.cs.lti.discoursedb.io.neuwirth.converter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.opencsv.CSVReader;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.model.system.DataSourceInstance;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.service.system.DataSourceService;
import edu.cmu.cs.lti.discoursedb.core.type.ContributionTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DataSourceTypes;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.neuwirth.model.NeuwirthSourceMapping;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;


@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class NeuwirthConverterService {
	
	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	private final @NonNull DiscourseService discourseService;
	private final @NonNull AnnotationService annoService;
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	
	@SuppressWarnings("resource")
	public void mapFile(String dataSetName, File file){
		String filePath = file.getAbsolutePath();		
		String fileName = file.getName();
		
		CSVReader reader =null;
		try{
			reader = new CSVReader(new FileReader(filePath));			
		}catch(Exception e){
			log.error("Error reading csv file",e);
			return;
		}
		
		String[] nextLine;
		
		// read the first line of csv file, get column names which are used to build annotation entities
		// build a HashMap annoMap, key: annotation column name, value: corresponding index
		// build a HashMap resMap, key: "title", "date_created", "text_prompt", "response" value: corresponding index
		
		try{
			nextLine = reader.readNext();			
		}catch(Exception e){
			log.error("Error parsing csv file. Illegally formatted line.",e);
			return;			
		}
		HashMap<String, Integer> resMap = new HashMap<String, Integer>();
		HashMap<String, Integer> annoMap = new HashMap<String, Integer>();
		for(int i = 0; i < nextLine.length ;i++) {
			if(nextLine[i].length() == 0)
				break;
			if(nextLine[i].equals("title") || nextLine[i].equals("text_prompt") || nextLine[i].equals("date_created")) {
				resMap.put(nextLine[i], i);
			}
			if(nextLine[i].equals("response")) {
				resMap.put(nextLine[i], i);
				for(int j = i+1; j < nextLine.length; j++) {
					if(nextLine[j].length() == 0)
						break;
					annoMap.put(nextLine[j], j);
				}
			}
		}
		
		// read and parse the csv file line by line and map source entities to DiscourseDB entities
		
		int num = 1;		
		try{					
			while((nextLine = reader.readNext()) != null) {
				
				/*
				 *  get necessary information from each line to create DiscourDB entities
				 *  a response is regarded as a contribution in the corresponding discourse
				 *  
				 */
				
				String discourseName = nextLine[resMap.get("title")];
				String response = nextLine[resMap.get("response")];
				String text_prompt = nextLine[resMap.get("text_prompt")];
				String date_created = nextLine[resMap.get("date_created")];
				
				// create or get discourse and discoursepart entity
				
				Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
				
				DiscoursePart question = discoursepartService.createOrGetTypedDiscoursePart(discourse, text_prompt,
						DiscoursePartTypes.CHATROOM);
				
	//			discoursepartService.findOneByDataSource(text_prompt, NeuwirthSourceMapping.ID_STR_TO_DISCOURSEPART, dataSetName)
	//			.orElseGet(() -> {
	//				DiscoursePart question = discoursepartService.createTypedDiscoursePart(discourse,
	//						DiscoursePartTypes.CHATROOM);
	//				dataSourceService.addSource(question, new DataSourceInstance(text_prompt,
	//						NeuwirthSourceMapping.ID_STR_TO_DISCOURSEPART, DataSourceTypes.NEUWIRTH, dataSetName));
	//				return question;
	//				}
	//			);
				
				/*
				 * check whether the response contribution exists in DiscourseDB. if not,
				 * create a new Contribution object and add it to data source and also
				 * create related User and Content object
				 * the contribution id is created by combination of n and filename.
				 * 
				 */
				
				String resId = fileName+"_"+String.valueOf(num);
				Optional<Contribution> existingRes = contributionService.findOneByDataSource(resId, NeuwirthSourceMapping.ID_STR_TO_CONTRIBUTION, dataSetName);
				if(!existingRes.isPresent()) {
					Contribution curRes = contributionService.createTypedContribution(ContributionTypes.POST);
					
					// set start and end time
					
					date_created = date_created.trim();
					String[] strs = date_created.split(" ");
					String[] date = strs[0].split("/");
					String[] time = strs[1].split(":");
					StringBuilder formatDate = new StringBuilder();
					formatDate.append(date[2]+"-");
					if(date[0].length() == 1)
						date[0] = "0"+date[1];
					formatDate.append(date[0]+"-");
					if(date[1].length() == 1)
						date[1] = "0"+date[1];
					formatDate.append(date[1]+" ");
					if(time[0].length() == 1)
						time[0] = "0"+time[0];
					formatDate.append(time[0]+":"+time[1]);
	
					
					// set content
					Content curContent = contentService.createContent();
					curContent.setText(response);
					dataSourceService.addSource(curContent, new DataSourceInstance(resId,
							NeuwirthSourceMapping.ID_STR_TO_CONTENT, DataSourceTypes.NEUWIRTH, dataSetName));
					curRes.setCurrentRevision(curContent);
					curRes.setFirstRevision(curContent);
	
	
					//parse dates
					try{
						Date d = sdf.parse(formatDate.toString());
						curRes.setStartTime(d);
						curRes.setEndTime(d);					
						curContent.setStartTime(d);
						curContent.setEndTime(d);
					}catch(ParseException e){
						log.warn("Error parsing date from String. Not populating start/end time.",e);
					}
					
					
					
					/*
					 * traverse the annoMap
					 * and then set annotation entities
					 * 
					 */
					
					for(Map.Entry<String, Integer> entry : annoMap.entrySet()) {
						if(nextLine[entry.getValue()].length() == 0)
							continue;
						AnnotationInstance newContribAnno = annoService.createTypedAnnotation(entry.getKey());
						annoService.addFeature(newContribAnno, annoService.createFeature(nextLine[entry.getValue()]));
						annoService.addAnnotation(curRes, newContribAnno);
					}
					
					discoursepartService.addContributionToDiscoursePart(curRes, question);
					
				}
				if(num % 10 == 0)
					System.out.println(num/10);
				num++;
			}
		}catch(IOException e){
			log.error("Error reading csv file. Skipping...",e);
			return;		
		}
	}

}
