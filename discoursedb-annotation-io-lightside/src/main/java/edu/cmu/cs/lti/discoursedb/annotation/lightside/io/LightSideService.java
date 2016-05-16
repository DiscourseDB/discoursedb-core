package edu.cmu.cs.lti.discoursedb.annotation.lightside.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.persistence.EntityNotFoundException;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;

import edu.cmu.cs.lti.discoursedb.annotation.lightside.model.RawDataInstance;
import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
import edu.cmu.cs.lti.discoursedb.core.service.annotation.AnnotationService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContentService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.ContributionService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscoursePartService;
import edu.cmu.cs.lti.discoursedb.core.service.macro.DiscourseService;
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
public class LightSideService {
	
	private final @NonNull DiscourseService discourseService;
	private final @NonNull ContributionService contribService;
	private final @NonNull ContentService contentService;
	private final @NonNull AnnotationService annoService;
	private final @NonNull DiscoursePartService dpService;
	
	
	@Transactional(readOnly=true)
	public void exportAnnotations(String discourseName, DiscoursePartTypes dptype, File outputFolder){
		Discourse discourse = discourseService.findOne(discourseName).orElseThrow(()->new EntityNotFoundException("Discourse with name "+discourseName+" does not exist."));
		exportAnnotations(discourse, dptype, outputFolder);	
	}
	
	@Transactional(readOnly=true)
	public void exportAnnotations(Discourse discourse, DiscoursePartTypes dptype, File outputFolder){
		log.info("Processing discourse "+discourse.getName()+". Extracting DiscourseParts of type "+dptype.name());
		exportAnnotations(dpService.findAllByDiscourseAndType(discourse, dptype), outputFolder);	
	}
	
		
	@Transactional(readOnly=true)
	public void exportAnnotations(Iterable<DiscoursePart> discourseParts, File outputFolder){
		List<RawDataInstance> data = StreamSupport.stream(discourseParts.spliterator(), false).flatMap(dp -> extractAnnotations(dp).stream()).collect(Collectors.toList());			
		write(data, outputFolder);
	}
	
	@Transactional(readOnly=true)
	public List<RawDataInstance> extractAnnotations(DiscoursePart dp){
		log.info("Processing DiscoursePart "+dp.getName());
		return extractAnnotations(contribService.findAllByDiscoursePart(dp));
	}
	
	
	@Transactional(readOnly=true)
	public List<RawDataInstance> extractAnnotations(Iterable<Contribution> contribs){
		//if this is very slow, we could implement a native query for this		
		List<RawDataInstance> outputList = new ArrayList<>();

		for(Contribution contrib: contribs){
			Content curRevision = contrib.getCurrentRevision();
			
			//one instance per contribution for entity label annotations
			RawDataInstance newContribData = new RawDataInstance();
			newContribData.setText(curRevision.getText());
			newContribData.setSpanAnnotation(false);
			newContribData.setAnnotations(convertAnnotationInstances(annoService.findAnnotations(contrib)));
			outputList.add(newContribData);								
			
			//one instance per annotation for span annotations
			for(AnnotationInstance anno:annoService.findAnnotations(curRevision)){
				RawDataInstance newContentData = new RawDataInstance();
				newContentData.setText(curRevision.getText());
				newContentData.setSpanAnnotation(true);
				newContentData.setAnnotations(convertAnnotationInstance(anno));
				outputList.add(newContentData);					
			}
		}		
		return outputList;
	}
	
	@Transactional(readOnly=true)
	private Map<String, String> convertAnnotationInstance(AnnotationInstance annotation){
		Set<AnnotationInstance> annos = new HashSet<>();
		annos.add(annotation);
		return convertAnnotationInstances(annos);
	}
	

	@Transactional(readOnly=true)
	private Map<String, String> convertAnnotationInstances(Set<AnnotationInstance> annotations){
		Map<String, String> pairs = new HashMap<>();
		if(annotations==null){
			return pairs;
		}
		//convert each annotation into a FeatureValuePair
		for(AnnotationInstance anno:annotations){
			if(anno.getFeatures()!=null&&!anno.getFeatures().isEmpty()){
				Assert.isTrue(anno.getFeatures().size()==1, "Annotations with more than one features are not supported.");
				pairs.put(anno.getType().toLowerCase(), anno.getFeatures().iterator().next().getValue());					
			}else{
				//if there is no feature, we treat the annotation as a binary label (set to true)
				pairs.put(anno.getType().toLowerCase(), "true");					
			}
		}
		return pairs;
	}
	
	/**
	 * 
	 * @param data the list of data instances
	 * @param outputFolder the folder to which the lightside files are supposed to be written
	 */
	private void write(List<RawDataInstance> data, File outputFolder){
		
		//entity labels
		List<RawDataInstance> entityLabelData = data.stream().parallel().filter(instance->!instance.isSpanAnnotation()).collect(Collectors.toList());
		//span annotations
		List<RawDataInstance> spanLabelData = data.stream().parallel().filter(instance->instance.isSpanAnnotation()).collect(Collectors.toList());

		//process entity labels
		try{
			FileUtils.writeLines(new File(outputFolder, "entitylabels.csv"), generateLightSideOutput(entityLabelData));			
			FileUtils.writeLines(new File(outputFolder, "spanannotations.csv"), generateLightSideOutput(spanLabelData));
		}catch(IOException e){
			log.error("Error writing LightSide file to disk",e);
		}
		
	}
	
	
	private List<String> generateLightSideOutput(List<RawDataInstance> data) throws JsonProcessingException{
		List<String> output = new ArrayList<>();
		CsvMapper mapper = new CsvMapper();

		//generate header
		Set<String> types = data.stream().parallel().flatMap(instance -> instance.getAnnotations().entrySet().stream())
				.map(m -> m.getKey().toLowerCase()).collect(Collectors.toSet());
		Assert.isTrue(!types.contains("text"), "No feature with the name \"text\" is allowed.");
		
		List<String> header = new ArrayList<>(types.size()+1);
		header.add("text");
		header.addAll(types);		
		output.add(mapper.writeValueAsString(header));
		
		//generate data vectors
		for(RawDataInstance instance:data){
			List<String> featVector = new ArrayList<>(header.size());
			featVector.add(instance.getText());
			Map<String,String> curInstAnnos = instance.getAnnotations();
			for(String type:types){
				if(curInstAnnos.containsKey(type)){
					featVector.add(curInstAnnos.get(type));
				}else{
					//TODO check if this can be serialized to CSV correctly
					featVector.add(null); //missing value
				}					
			}
			Assert.isTrue(featVector.size()==header.size(), "Error writing feature vector. Wrong size.");
			output.add(mapper.writeValueAsString(featVector));
	    }		

		return output;
	}	
	
}
