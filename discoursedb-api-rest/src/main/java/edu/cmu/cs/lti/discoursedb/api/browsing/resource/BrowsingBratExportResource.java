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
package edu.cmu.cs.lti.discoursedb.api.browsing.resource;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.hateoas.ResourceSupport;

import com.fasterxml.jackson.annotation.JsonFormat;


public class BrowsingBratExportResource extends ResourceSupport {
	private static final Logger logger = LogManager.getLogger(BrowsingBratExportResource.class);
	private String name;
	private Date lastExport;

	static public List<BrowsingBratExportResource> findPreviouslyExportedBrat(String dir) {
		logger.info("Looking for files in " + dir);
		File folder = new File(dir);
		File[] exports = folder.listFiles();
		List<BrowsingBratExportResource> returns = new ArrayList<>();
		if (exports != null) { 
			for (File exp: exports) {
				if (!exp.getName().startsWith(".")) {
					returns.add(new BrowsingBratExportResource(exp.getName(), new Date(exp.lastModified())));
				}
			}
		}
		return returns;
	}

	
	public BrowsingBratExportResource(String name, Date lastExport) {
		setName(name);
		setLastExport(lastExport);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getLastExport() {
		return lastExport;
	}

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss XXX")
		//, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	public void setLastExport(Date lastExport) {
		this.lastExport = lastExport;
	}


}
