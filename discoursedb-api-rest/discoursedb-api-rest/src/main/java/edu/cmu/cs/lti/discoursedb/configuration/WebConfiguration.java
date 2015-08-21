package edu.cmu.cs.lti.discoursedb.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.WebContentInterceptor;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "edu.cmu.cs.lti.discoursedb")
public class WebConfiguration extends WebMvcConfigurerAdapter {

	  @Bean
	  public InternalResourceViewResolver getInternalResourceViewResolver() {
	    InternalResourceViewResolver resolver = new InternalResourceViewResolver();
	    resolver.setPrefix("/WEB-INF/views/");
	    resolver.setSuffix(".jsp");
	    return resolver;
	  }

	  @Override
	  public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	    configurer.enable();
	  }

	  @Bean
	  public WebContentInterceptor webContentInterceptor() {
	    WebContentInterceptor interceptor = new WebContentInterceptor();
	    interceptor.setCacheSeconds(0);
	    interceptor.setUseExpiresHeader(true);
	    interceptor.setUseCacheControlHeader(true);
	    interceptor.setUseCacheControlNoStore(true);

	    return interceptor;
	  }

	  @Override
	  public void addResourceHandlers(ResourceHandlerRegistry registry) {
	    registry.addResourceHandler("/libs/**").addResourceLocations("/libs/");
	    registry.addResourceHandler("/app/**").addResourceLocations("/app/");
	    registry.addResourceHandler("/assets/**").addResourceLocations("/assets/");
	  }

	  @Override
	  public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(webContentInterceptor());
	  }
}
