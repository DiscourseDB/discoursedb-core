package edu.cmu.cs.lti.discoursedb.core.configuration;

import org.springframework.context.annotation.Configuration;

import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;

@Configuration
@EnableCaching
public class CachingConfiguration extends CachingConfigurerSupport {

    @Override
    public KeyGenerator keyGenerator() {
        return new EnvironmentAwareCacheKeyGenerator();
    }

}
