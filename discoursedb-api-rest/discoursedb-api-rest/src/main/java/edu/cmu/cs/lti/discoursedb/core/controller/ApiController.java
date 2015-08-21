package edu.cmu.cs.lti.discoursedb.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class ApiController {
	  @RequestMapping(method = RequestMethod.GET)
	  public String viewApplication() {
	    return "index";
	  }
}
