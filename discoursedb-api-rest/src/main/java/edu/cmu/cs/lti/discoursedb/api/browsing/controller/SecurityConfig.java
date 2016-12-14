package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			
	  auth.inMemoryAuthentication().withUser("mkyong").password("123456").roles("USER");
	  auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
	  auth.inMemoryAuthentication().withUser("dba").password("123456").roles("DBA");
	}
	

	@Override
	protected void configure(HttpSecurity http) throws Exception {
	  http.authorizeRequests()
	    .antMatchers("/browsing/tokensigningoogle").permitAll()
	    .antMatchers("/browsing/stats").permitAll()
	    .antMatchers("/browsing/discourse").permitAll()
		.antMatchers("/browsing/**").permitAll(); //access("hasRole('ROLE_ADMIN')")
		//.antMatchers("/browser/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DBA')");
		
	  http.csrf().disable();/*
	  http
	   .openidLogin()             
	   .permitAll()
      .authenticationUserDetailsService(new CustomUserDetailsService())
      .attributeExchange("https://www.google.com/.*")
          .attribute("email")
              .type("http://axschema.org/contact/email")
              .required(true)
              .and()
          .attribute("firstname")
              .type("http://axschema.org/namePerson/first")
              .required(true)
              .and()
          .attribute("lastname")
              .type("http://axschema.org/namePerson/last")
              .required(true);*/
	}
	

}