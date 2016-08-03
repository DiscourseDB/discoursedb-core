package edu.cmu.cs.lti.discoursedb.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingD2DP;
import edu.cmu.cs.lti.discoursedb.api.browsing.resource.BrowsingDiscourseResource;
@Configuration
public  class RestConfig extends RepositoryRestConfigurerAdapter {

    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        //config.getProjectionConfiguration().addProjection(BrowsingDiscourseResource.class);
        //config.getProjectionConfiguration().addProjection(BrowsingD2DP.class);
    }


}