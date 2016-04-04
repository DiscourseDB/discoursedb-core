package edu.cmu.cs.lti.discoursedb.io.habworlds.converter;

import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import edu.cmu.cs.lti.discoursedb.core.model.macro.Content;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Contribution;
import edu.cmu.cs.lti.discoursedb.core.model.macro.Discourse;
import edu.cmu.cs.lti.discoursedb.core.model.macro.DiscoursePart;
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
import edu.cmu.cs.lti.discoursedb.core.type.DiscoursePartTypes;
import edu.cmu.cs.lti.discoursedb.io.habworlds.model.HabWorldPost;
import edu.cmu.cs.lti.discoursedb.io.habworlds.model.HabworldsSourceMapping;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;


@Service
@Transactional(propagation=Propagation.REQUIRED, readOnly=false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired) )
public class HabworldsConverterService {
	
	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	private final @NonNull DiscourseService discourseService;
	
	public void mapPost(HabWorldPost post, String discourseName, String datasetName) throws ParseException {
		
		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);
		
		/* 
		 * check whether the question already exists in DiscourseDB
		 * if not, create a new DiscoursePart object and add it to data source
		 * 
		 */
		
		Optional<DiscoursePart> existingQuestion = discoursepartService.findOneByDataSource(
				post.getQuestionID(), HabworldsSourceMapping.ID_STR_TO_DISCOURSEPART, datasetName);
		
		if(!existingQuestion.isPresent()) {
			DiscoursePart question = discoursepartService.createTypedDiscoursePart(discourse, DiscoursePartTypes.CHATROOM);
			dataSourceService.addSource(question, new DataSourceInstance(
					String.valueOf(post.getQuestionID()), 
					HabworldsSourceMapping.ID_STR_TO_DISCOURSEPART, 
					DataSourceTypes.HABWORLDS, 
					datasetName));
		}
		
		/* 
		 * check whether the post contribution all exists in DiscourseDB
		 * if not, create a new Contribution object and add it to data source
		 * and also create related User and Content object
		 * 
		 */
		
		Optional<Contribution> existingPost = contributionService.findOneByDataSource(
				String.valueOf(post.getInteractionID()), HabworldsSourceMapping.ID_STR_TO_CONTRIBUTION, datasetName);
		
		if(!existingPost.isPresent()) {
			Contribution curPost = contributionService.createTypedContribution(ContributionTypes.POST);
			
			// set start and end time
			
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date date = sdf.parse(post.getServerTime());
			curPost.setStartTime(date);
			curPost.setEndTime(date);
			
			// set content
			
			User author = userService.createOrGetUser(discourse, String.valueOf(post.getUserID()));
			dataSourceService.addSource(author,
					new DataSourceInstance(
							String.valueOf(post.getUserID()), 
							HabworldsSourceMapping.FROM_USER_ID_STR_TO_USER, 
							DataSourceTypes.HABWORLDS, 
							datasetName));

			Content curContent = contentService.createContent();
			curContent.setText(this.parseXml(post.getSnapshot()));
			curContent.setStartTime(date);
			curContent.setEndTime(date);
			curContent.setAuthor(author);
			dataSourceService.addSource(curContent,
					new DataSourceInstance(
							String.valueOf(post.getInteractionID()), 
							HabworldsSourceMapping.ID_STR_TO_CONTENT, 
							DataSourceTypes.HABWORLDS, 
							datasetName));
			
			curPost.setFirstRevision(curContent);
			curPost.setCurrentRevision(curContent);
			dataSourceService.addSource(curPost,
					new DataSourceInstance(
							String.valueOf(post.getInteractionID()), 
							HabworldsSourceMapping.ID_STR_TO_CONTRIBUTION, 
							DataSourceTypes.HABWORLDS, 
							datasetName));
			
			// add the contribution to the question it belongs to
			
			DiscoursePart question = 
					discoursepartService.createOrGetTypedDiscoursePart(
							discourse, post.getQuestionID(), DiscoursePartTypes.CHATROOM);
			
			discoursepartService.addContributionToDiscoursePart(curPost, question);
			
		}
		
	}
	
	private String parseXml(String xml) {
		String text = null;
		
		try {
	        DocumentBuilderFactory dbf =
	            DocumentBuilderFactory.newInstance();
	        DocumentBuilder db = dbf.newDocumentBuilder();
	        InputSource is = new InputSource();
	        is.setCharacterStream(new StringReader(xml));

	        Document doc = db.parse(is);
	        NodeList nodes = doc.getElementsByTagName("entry");

	        for(int i=0;i<nodes.getLength();i++) {
	        	Element element = (Element) nodes.item(i);
		        NodeList name = element.getElementsByTagName("string");
		        if(name.getLength()==2) {
		        	Element line = (Element) name.item(1);
			        text = new String(getCharacterDataFromElement(line));
			        break;
		        }
	        }
	    }
	    catch (Exception e) {
	        e.printStackTrace();
	    }
		return text;
	}
	
	private String getCharacterDataFromElement(Element e) {
	    Node child = e.getFirstChild();
	    if (child instanceof CharacterData) {
	       CharacterData cd = (CharacterData) child;
	       return cd.getData();
	    }
	    return "?";
	  }

}
