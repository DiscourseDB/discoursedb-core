/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Author: Oliver Ferschke
 *
 * This file is part of DiscourseDB.
 *
 * DiscourseDB is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * DiscourseDB is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with DiscourseDB.  If not, see <http://www.gnu.org/licenses/> 
 * or write to the Free Software Foundation, Inc., 51 Franklin Street, 
 * Fifth Floor, Boston, MA 02110-1301  USA
 *******************************************************************************/
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
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
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
import lombok.extern.log4j.Log4j;

@Log4j
@Service
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class HabworldsConverterService {

	private final @NonNull DataSourceService dataSourceService;
	private final @NonNull UserService userService;
	private final @NonNull ContentService contentService;
	private final @NonNull ContributionService contributionService;
	private final @NonNull DiscoursePartService discoursepartService;
	private final @NonNull DiscourseService discourseService;

	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

	public void mapPost(HabWorldPost post, String discourseName, String datasetName) throws ParseException {

		//parse XML and skip post if XML is malformed
		String postText = null;
		try{
			postText = parseXml(post.getSnapshot());
		}catch(Exception e){
			log.error("Unable to parse XML of post: "+post.getSnapshot());
			return;
		}

		Discourse discourse = discourseService.createOrGetDiscourse(discourseName);

		/*
		 * check whether the question already exists in DiscourseDB if not,
		 * create a new DiscoursePart object and add it to data source
		 * 
		 */

		discoursepartService.findOneByDataSource(post.getQuestionID(), HabworldsSourceMapping.ID_STR_TO_DISCOURSEPART, datasetName)
				.orElseGet(() -> {
					DiscoursePart question = discoursepartService.createTypedDiscoursePart(discourse,
							DiscoursePartTypes.CHATROOM);
					dataSourceService.addSource(question, new DataSourceInstance(post.getQuestionID(),
							HabworldsSourceMapping.ID_STR_TO_DISCOURSEPART, DataSourceTypes.HABWORLDS, datasetName));
					return question;
				}
		);

		/*
		 * check whether the post contribution all exists in DiscourseDB if not,
		 * create a new Contribution object and add it to data source and also
		 * create related User and Content object
		 * 
		 */

		Optional<Contribution> existingPost = contributionService.findOneByDataSource(String.valueOf(post.getInteractionID()), HabworldsSourceMapping.ID_STR_TO_CONTRIBUTION, datasetName);

		if (!existingPost.isPresent()) {
			Contribution curContribution = contributionService.createTypedContribution(ContributionTypes.POST);

			// set start and end time

			Date date = sdf.parse(post.getServerTime());
			curContribution.setStartTime(date);
			curContribution.setEndTime(date);

			// set content

			User author = userService.createOrGetUser(discourse, String.valueOf(post.getUserID()));
			dataSourceService.addSource(author, new DataSourceInstance(String.valueOf(post.getUserID()),
					HabworldsSourceMapping.FROM_USER_ID_STR_TO_USER, DataSourceTypes.HABWORLDS, datasetName));

			Content curContent = contentService.createContent();
			curContent.setText(postText);
			curContent.setStartTime(date);
			curContent.setEndTime(date);
			curContent.setAuthor(author);
			dataSourceService.addSource(curContent, new DataSourceInstance(String.valueOf(post.getInteractionID()),
					HabworldsSourceMapping.ID_STR_TO_CONTENT, DataSourceTypes.HABWORLDS, datasetName));

			curContribution.setFirstRevision(curContent);
			curContribution.setCurrentRevision(curContent);
			dataSourceService.addSource(curContribution, new DataSourceInstance(String.valueOf(post.getInteractionID()),
					HabworldsSourceMapping.ID_STR_TO_CONTRIBUTION, DataSourceTypes.HABWORLDS, datasetName));

			// add the contribution to the question it belongs to

			DiscoursePart question = discoursepartService.createOrGetTypedDiscoursePart(discourse, post.getQuestionID(),
					DiscoursePartTypes.CHATROOM);

			discoursepartService.addContributionToDiscoursePart(curContribution, question);
		}

	}

	/**
	 * TODO: create test case for this
	 * 
	 * @param xml
	 *            xml String
	 * @return the text extracted from the xml String
	 */
	private String parseXml(String xml) throws Exception{
		String text = null;

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);
		NodeList nodes = doc.getElementsByTagName("entry");

		for (int i = 0; i < nodes.getLength(); i++) {
			Element element = (Element) nodes.item(i);
			NodeList name = element.getElementsByTagName("string");
			if (name.getLength() == 2) {
				Element line = (Element) name.item(1);
				text = new String(getCharacterDataFromElement(line));
				break;
			}
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
