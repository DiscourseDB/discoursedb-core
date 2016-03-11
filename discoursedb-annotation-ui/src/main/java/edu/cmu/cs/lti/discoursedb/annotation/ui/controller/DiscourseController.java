package edu.cmu.cs.lti.discoursedb.annotation.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DiscourseController {
	
	/**
	 * Spring Boot’s autoconfigured view resolver will map this to the
	 * corresponding html template (index.html)
	 * 
	 * @return
	 */
	@RequestMapping(value = "/")
	public String index(){
		return "index";
	}
}
