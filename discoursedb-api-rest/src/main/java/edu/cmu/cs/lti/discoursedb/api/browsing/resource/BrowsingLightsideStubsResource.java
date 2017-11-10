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

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;


public class BrowsingLightsideStubsResource extends ResourceSupport {
	private String name;
	private Date lastExport;
	private int numDiscourseParts;
	private boolean annotated;
	private String baseDirectory;
	
	
	static public List<BrowsingLightsideStubsResource> findPreviouslyExportedLightside(String dir) {
		File folder = new File(dir);
		File[] exports = folder.listFiles();
		List<BrowsingLightsideStubsResource> returns = new ArrayList<>();
		if (exports != null) { 
			for (File exp: exports) {
				if (exp.isDirectory()) {
					returns.add(new BrowsingLightsideStubsResource(dir,exp.getName()));
				}
			}
		}
		return returns;
	}
	
	public BrowsingLightsideStubsResource(String baseDirectory, String name) {
		this.baseDirectory = baseDirectory;
		this.name = name;
		File folder = new File(baseDirectory, name);
		this.lastExport = new Date(folder.lastModified());
		File[] dps = folder.listFiles();
		if (dps == null) {
			this.numDiscourseParts = 0;
		} else {
			this.numDiscourseParts = dps.length;
		}
		this.annotated = name.endsWith("_annotated");
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNote() {
		return "Note: Annotations automatically updated for each download";
	}
	public Date getLastExport() {
		return lastExport;
	}
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
	public void setLastExport(Date lastExport) {
		this.lastExport = lastExport;
	}
	public int getNumDiscourseParts() {
		return numDiscourseParts;
	}
	public void setNumDiscourseParts(int numDiscourseParts) {
		this.numDiscourseParts = numDiscourseParts;
	}
	public boolean isAnnotated() {
		return annotated;
	}
	public void setAnnotated(boolean annotated) {
		this.annotated = annotated;
	}


}
