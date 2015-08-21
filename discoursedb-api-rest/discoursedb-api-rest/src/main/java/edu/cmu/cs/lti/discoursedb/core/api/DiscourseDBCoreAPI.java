package edu.cmu.cs.lti.discoursedb.core.api;

import javax.servlet.ServletRegistration;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import edu.cmu.cs.lti.discoursedb.configuration.BaseConfiguration;
import edu.cmu.cs.lti.discoursedb.configuration.WebConfiguration;

@SpringBootApplication
@ComponentScan(value = { "edu.cmu.cs.lti.discoursedb" })
public class DiscourseDBCoreAPI extends AbstractAnnotationConfigDispatcherServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(DiscourseDBCoreAPI.class, args);
	}

	@Override
	protected void customizeRegistration(ServletRegistration.Dynamic registration) {
		registration.setInitParameter("dispatchOptionsRequest", "true");
		registration.setAsyncSupported(true);
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { BaseConfiguration.class };
	}

	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class[] { WebConfiguration.class };
	}

	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

}
