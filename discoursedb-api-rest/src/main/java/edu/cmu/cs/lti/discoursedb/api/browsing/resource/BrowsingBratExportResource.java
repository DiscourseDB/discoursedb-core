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

	static public List<BrowsingBratExportResource> findPreviouslyExported(String dir) {
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

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd' 'HH:mm:ss")
	public void setLastExport(Date lastExport) {
		this.lastExport = lastExport;
	}


}
