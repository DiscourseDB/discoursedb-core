package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import org.springframework.data.repository.query.spi.EvaluationContextExtensionSupport;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

class SecurityEvaluationContextExtension extends EvaluationContextExtensionSupport {

	  @Override
	  public String getExtensionId() {
	    return "security";
	  }

	  @Override
	  public SecurityExpressionRoot getRootObject() {
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    SecurityExpressionRoot ser = new SecurityExpressionRoot(authentication) {};
	    return ser;
	  }
	}