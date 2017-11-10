package edu.cmu.cs.lti.discoursedb.configuration;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.util.StringUtils;

public class EnvironmentAwareCacheKeyGenerator
		implements KeyGenerator {

	@Autowired
	private DatabaseSelector databaseSelector;
	
	@Override
	public Object generate(Object target, Method method, Object... params) {
		
        String key = databaseSelector.determineCurrentLookupKey() + "-" + (
            method == null ? "" : method.getName() + "-") + StringUtils
            .collectionToDelimitedString(Arrays.asList(params), "-");

        return key;
	  

	}

}
