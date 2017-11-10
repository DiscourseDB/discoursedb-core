/*******************************************************************************
 * Copyright (C)  2015 - 2016  Carnegie Mellon University
 * Authors: Oliver Ferschke and Chris Bogart
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
package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.ResourceSupport;

import edu.cmu.cs.lti.discoursedb.core.model.annotation.AnnotationInstance;

public class BrowsingAnnotationResource extends ResourceSupport {
	private String type;
	private String range = "";
	private List<BrowsingFeatureResource> features;
	
	
	public BrowsingAnnotationResource(AnnotationInstance ai) {
		this.setType(ai.getType());
		if (ai.getBeginOffset() == ai.getEndOffset() && ai.getEndOffset() == 0) {
			this.setRange("(all)");
		} else {
			this.setRange(ai.getBeginOffset() + "-" + ai.getEndOffset());
		}
		this.setFeatures(ai.getFeatures().stream().map(f -> new BrowsingFeatureResource(f)).collect(Collectors.toList()));
	}


	public List<BrowsingFeatureResource> getFeatures() {
		return features;
	}


	public void setFeatures(List<BrowsingFeatureResource> features) {
		this.features = features;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getRange() {
		return range;
	}


	public void setRange(String range) {
		this.range = range;
	}

}
