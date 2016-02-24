package edu.cmu.cs.lti.discoursedb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(BaseConfiguration.class)
public class ApiConfig {}
