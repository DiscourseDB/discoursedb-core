package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.repository.query.spi.EvaluationContextExtension;
import org.springframework.security.config.annotation.authentication.builders.*;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.openid.OpenIDAuthenticationToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;


@Configuration
@EnableWebSecurity
@EnableOAuth2Sso
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	private static final Logger logger = LogManager.getLogger(SecurityConfig.class);
	private static Boolean USE_HTTPS = null;
	@Autowired Environment env;
	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
			
	  //auth.apply(new )
	  auth.inMemoryAuthentication().withUser("mkyong").password("123456").roles("USER");
	  auth.inMemoryAuthentication().withUser("admin").password("123456").roles("ADMIN");
	  auth.inMemoryAuthentication().withUser("dba").password("123456").roles("DBA");
	  auth.inMemoryAuthentication().withUser("discoursedb").password("123456").roles("DBA");
	}
	
	 @Bean
	    EvaluationContextExtension securityExtension() {
	        return new SecurityEvaluationContextExtension();
	    }
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
	  if (USE_HTTPS == null) {
		  USE_HTTPS = env.getProperty("https.enabled").equals("true");
	  }
	  if (USE_HTTPS) {
		  http.requiresChannel().anyRequest().requiresSecure();
		  
		   //https://github.com/spring-projects/spring-security/issues/2898
		   //  Disable HSTS unless you're *sure* that all uses of this domain
		   //  will use https and not http.  HSTS flags each user's browser
		   //  to silently refuse to complete http connections to this domain for a year.
		  http.headers().httpStrictTransportSecurity().disable();
		  
		  http.sessionManagement()
		  		.sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
		  http.x509()
		    .subjectPrincipalRegex("CN=(.*?)(?:,|$)")
		    .userDetailsService(userDetailsService());
	  }
	  http.authorizeRequests()
	    .antMatchers("/browsing/tokensigningoogle").permitAll()
	    .antMatchers("/browsing/stats").permitAll()
	    .antMatchers("/browsing/discourse").permitAll()
		.antMatchers("/browsing/**").permitAll(); 
	  http.csrf().disable();
	}
	
	


	@Bean
	public UserDetailsService userDetailsService() {
	return new UserDetailsService() {
	    @Override
	    public UserDetails loadUserByUsername(String username) {
	    	logger.info("Got https client name of " + username);
	        if (username.equals("cid") || username.equals("learnsphere")) {
	            return new User(username, "", 
	              AuthorityUtils
	                .commaSeparatedStringToAuthorityList("TRUSTED_USER_AGENT"));
	        } else {
	        	return null;
	        }
	    }
	};
}
	
	
	
	
	
	
	/*@Override
 	protected void configure3(HttpSecurity http) throws Exception {
 		http.authorizeRequests()
 				.antMatchers("/**")
 				.hasRole("USER")
 				.and()
 				.openidLogin()
 				.loginPage("/login")
 				.permitAll()
 				.authenticationUserDetailsService(
 						new AutoProvisioningUserDetailsService())
 				.attributeExchange("https://www.google.com/.*").attribute("email")
 				.type("http://axschema.org/contact/email").required(true).and()
 				.attribute("firstname").type("http://axschema.org/namePerson/first")
 				.required(true).and().attribute("lastname")
 				.type("http://axschema.org/namePerson/last").required(true).and().and()
 				.attributeExchange(".*yahoo.com.*").attribute("email")
 				.type("http://schema.openid.net/contact/email").required(true).and()
 				.attribute("fullname").type("http://axschema.org/namePerson")
 				.required(true).and().and().attributeExchange(".*myopenid.com.*")
 				.attribute("email").type("http://schema.openid.net/contact/email")
 				.required(true).and().attribute("fullname")
 				.type("http://schema.openid.net/namePerson").required(true);
 	}
	
	 public class AutoProvisioningUserDetailsService implements
			AuthenticationUserDetailsService<OpenIDAuthenticationToken> {
		public UserDetails loadUserDetails(OpenIDAuthenticationToken token)
				throws UsernameNotFoundException {
			return new User(token.getName(), "NOTUSED",
					AuthorityUtils.createAuthorityList("ROLE_ADMIN"));
		}
	 }
	
	protected void configure2(HttpSecurity http) throws Exception {
	  http.authorizeRequests()
	    //.antMatchers("/browsing/tokensigningoogle").permitAll()
	    .antMatchers("/browsing/stats").permitAll()
	    .anyRequest().authenticated();
	//    .antMatchers("/browsing/discourse").permitAll()
		//.antMatchers("/browsing/**").permitAll(); //access("hasRole('ROLE_ADMIN')")
		//.antMatchers("/browser/**").access("hasRole('ROLE_ADMIN') or hasRole('ROLE_DBA')");
		
	  //http.csrf().disable();
	  http.csrf().disable();
      http.httpBasic().disable();

	  http
	  
	    .openidLogin()             
	  	.authenticationUserDetailsService(new CustomUserDetailsService())
	   		.failureHandler(myLoginFailureHandler)
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
              .required(true); 
	}
	*/

}