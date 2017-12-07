package edu.cmu.cs.lti.discoursedb.api.browsing.controller;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemDatabase;
import edu.cmu.cs.lti.discoursedb.system.model.system.SystemUser;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemDatabaseRepository;
import edu.cmu.cs.lti.discoursedb.system.repository.system.SystemUserRepository;
import edu.cmu.cs.lti.discoursedb.system.service.system.SystemUserService;

@Component
public class SecurityUtils {

	private static  Logger logger = LogManager.getLogger(SecurityUtils.class);
	@Autowired private SystemUserRepository sysUserRepo;
	@Autowired private SystemUserService sysUserService;
	@Autowired private SystemDatabaseRepository sysDbRepo;
	@Autowired private Environment env;
    private static  String GOOGLE_CLIENT_ID = null;
	private static  String GOOGLE_CLIENT_SECRET = null;
	private static String  URL = null;
    

	SecurityUtils() {
	}
	
	private void init() {
		//SystemUserAuthentication.securityEnabled = false;
		if (URL == null) {
			GOOGLE_CLIENT_ID = env.getRequiredProperty("google.client_id");
			GOOGLE_CLIENT_SECRET = env.getRequiredProperty("google.client_secret");
			URL = env.getRequiredProperty("google.registered.url");
			SystemUserAuthentication.securityEnabled = env.getProperty("https.enabled").equals("true");
		}
	}
	public String currentUserEmail() {
		return loggedInUser().getPrincipal().toString();
	}
	public  void authenticate(HttpServletRequest req,  HttpSession s) {
		
		init();
		if (!SystemUserAuthentication.securityEnabled) { return; }
		
		// Should do several things:
		// If it's a google token, check with google, turn it into an email address, and put it back out as a cookie
		// If it's a cookie, turn it into an email address
		// If it's a secure connection from Learnsphere, extract email from header
		// In any case, then, create an authentication dealie, and stick it in the securitycontextholder
		// Look up a list of discourses that this person is allowed to access, and associate that as "authorities"
		logger.info("Got a request...");
		logger.info("Session attributes: " + String.join(",",Collections.list(s.getAttributeNames())) + ";" + s.getAttribute("email"));
		if (s.getAttribute("email") != null && s.getAttribute("email") != "") {
			return;
		}
		logger.info("BEFORE AUTHENTICATE: " + SecurityContextHolder.getContext().toString());
		if (isTrustedUserProxy()) {
			String[] userAndPass = getBasicAuthentication(req);
			if (userAndPass != null) {
				logger.info("     -> accepting user by proxy: " + userAndPass[0]);
				setupUser(userAndPass[0], userAndPass[1]);
			} else {
				logger.info("   Trusted user proxy, but no basic authentication received");
				throw new BrowsingRestController.UnauthorizedDatabaseAccess();
			}
		} else {
			logger.info("Not trusted user proxy");
			logger.info("Session " + (s.isNew()?"existed":"did not exist"));
			logger.info("Preexisting user info was " + String.join(",",Collections.list(s.getAttributeNames())) + ";" + s.getAttribute("email"));
			if (s.isNew() || s.getAttribute("email") == null || s.getAttribute("email").toString() == "") {
				String auth = isGoogleSignIn(req);
				if (auth != null) {
					String email = validateGoogleUserAndAddIfNeeded(auth);
					logger.info("     -> accepting google login" + email);
					//setupUser(email, "plugh");
					s.setAttribute("email", email);
					logger.info("Postexisting user info was " + String.join(",",Collections.list(s.getAttributeNames())) + ";" + s.getAttribute("email"));
				} else {
					logger.info("Not google sign in");
					throw new BrowsingRestController.UnauthorizedDatabaseAccess();
				}
			} else {
				logger.info("Recalling session; restoring user " + s.getAttribute("email").toString());
				setupUser(s.getAttribute("email").toString(),"");
			}
		}
		logger.info("AFTER AUTHENTICATE: " + SecurityContextHolder.getContext().toString());
        logger.info("Logging in2 with [{}]", SecurityContextHolder.getContext().getAuthentication().getPrincipal());
 
	}
	
	public void setupUser(String email, String password) {
		Authentication auth = getUser(email);
		if (auth != null) {
	        SecurityContextHolder.clearContext();
	        SecurityContextHolder.getContext().setAuthentication(auth);
	        logger.info("Logging in with [{}]", auth.getPrincipal());
		}
	}
	
	public void setupUserEvenIfNew(String email, String password, String realname) {
		Authentication auth = getOrMakeUser(email, realname);
		if (auth != null) {
	        SecurityContextHolder.clearContext();
	        SecurityContextHolder.getContext().setAuthentication(auth);
	        logger.info("Logging in with [{}]", auth.getPrincipal());
		}
	}
	 
	public SystemUserAuthentication loggedInUser() {
		return (SystemUserAuthentication)SecurityContextHolder.getContext().getAuthentication();
	}
	
	
	public boolean isTrustedUserProxy() {
		return SystemUserAuthentication.authoritiesContains("TRUSTED_USER_AGENT", SecurityContextHolder.getContext().getAuthentication());
	}
	
	
	
	public SystemUserAuthentication getUser(String email) {
		
		Optional<SystemUser> su = email != null?sysUserRepo.findOneByEmail(email):Optional.empty();
		
		if (su.isPresent()) {
			SystemUserAuthentication authentication = new SystemUserAuthentication(
					su.get(), "",
	                su.get().getAuthorities(),getAllowedDatabases(su.get().getAuthorities()));
				
			return authentication;
		} else {
			return null;
		}
		// TODO Auto-generated constructor stub
	}
	
	private List<String> getAllowedDatabases(Collection<? extends GrantedAuthority> gas) {
			List<String> allowed = new ArrayList<String>();
			
			for (GrantedAuthority ga : gas) {
				String authority = ga.getAuthority();
				if (authority == "ROLE_ANONYMOUS") {
					return new ArrayList<String>();   // Short circuit and return nothing, if the person is anonymous
				}
				if (!authority.startsWith("ROLE:")) {
					allowed.add(ga.getAuthority());
				}
			}
			for (SystemDatabase sd : sysDbRepo.findAll()) {
				if (sd.getIsPublic() > 0) {
					allowed.add(sd.getName());
				}
			}
			return allowed;
	}

	public SystemUserAuthentication getOrMakeUser(String email, String name) {
		
		Optional<SystemUser> su = email != null?sysUserRepo.findOneByEmail(email):Optional.empty();
		SystemUserAuthentication authentication;
		if (su.isPresent()) {
			authentication = new SystemUserAuthentication(
					su.get(), "",
	                su.get().getAuthorities(), getAllowedDatabases(su.get().getAuthorities()));
		} else {
			SystemUser newu = sysUserService.createSystemUser(email,name,email);
			
			authentication = new SystemUserAuthentication(
					newu, "",
					newu.getAuthorities(), getAllowedDatabases(newu.getAuthorities()));
			
		}
		return authentication;
		// TODO Auto-generated constructor stub
	}
	
	
	/**
     * Handles the HTTP post.
     * @param req HttpServletRequest.
     * @param resp HttpServletResponse.
     * @throws IOException an IO exception
     * @throws ServletException a servlet exception
     *
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException, ServletException {
        logDebug("doPost begin :: ", getDebugParamsString(req));
        try {
            HttpSession httpSession = req.getSession(true);
            UserDao userDao = DaoFactory.DEFAULT.getUserDao();
            String accountId = null, password = null;
            boolean loginSuccessful = false;

            boolean isWebIso = isWebIso(req);

            // Get username and password
            if (isWebIso) {
                accountId = req.getRemoteUser();
                if (isGoogleSignIn(req)) {
                    accountId = validateGoogleUser(req.getParameter("googleAuthCode"));
                }
            } else {
                // get the form parameters from login box/page
                accountId = getAccountId(req);
                password = getPassword(req);
            }

            if (accountId == null) { // If no account id entered, go to login page
                // user hasn't tried to login yet, so login has not failed yet
                logDebug("accountId is null");
                LoginInfo loginInfo = new LoginInfo();
                loginInfo.setAccountId(accountId);
                loginInfo.setWebIso(isWebIso);
                loginInfo.setLoginFailed(false);
                httpSession.setAttribute("ls_loginInfo", loginInfo);
                setUserAndCleanSession(req, null);

                RequestDispatcher disp;
                disp = getServletContext().getRequestDispatcher(LOGIN_JSP_NAME);
                disp.forward(req, resp);
		return;
            }

            // account id entered
            // but does the account exist?
            UserItem userItem = userDao.get(accountId);
            LoginInfo loginInfo = new LoginInfo();
            loginInfo.setAccountId(accountId);
            loginInfo.setWebIso(isWebIso);
            if (userItem == null) { // account does not exist, create it
                boolean createSuccessful = false;
                if (isWebIso) {
                    logger.info("WebISO user logged in, creating account: "
                                + accountId);

                    // create user item
                    userItem = new UserItem();
                    userItem.setId(accountId);
                    userItem.setEmail(accountId);

                    if (OliUserServices.isOliEnabled()) {
                        boolean userCreated = createOliUserAccount(accountId);
                        if (userCreated) {
                            AuthInfo authInfo = OliUserServices.login(
                                    req, resp, accountId, null, true);
                            if (authInfo == null) {
                                logger.warn("Failed to login new user: " + accountId);
                                loginSuccessful = false;
                            } else {
                                logger.debug("OliUserServices.login is successful.");
                                loginSuccessful = true;
                            }
                        } else {
                            logger.warn("Failed to create new user: " + accountId);
                            loginSuccessful = false;
                        }
                    } else {
                        loginSuccessful = true;
                    }

                    if (loginSuccessful) {
                        userDao.saveOrUpdate(userItem);
                        setUserAndCleanSession(req, userItem);
                        logger.info("Created new user: " + accountId);
                    } else {
                        logger.warn("User not created in OLI user DB: " + accountId);
                    }
                } else {
                    // Not an option for Workflows bc no local-login option.
                }
            } else {
                // User exists... log in.
                if (OliUserServices.isOliEnabled()) {
                    AuthInfo authInfo =
                        OliUserServices.login(req, resp, accountId, password, isWebIso);
                    String loginException = authInfo.getException();

                    if (loginException != null
                        && loginException.indexOf("UserNotFoundException") >= 0
                        && isWebIso) {
                        logger.warn("Webiso user not found:" + accountId);
                        loginSuccessful = false;
                        setUserAndCleanSession(req, null);
                    } else if (authInfo.getException() != null) {
                        logger.warn("User failed to login: " + accountId);
                        loginSuccessful = false;
                        setUserAndCleanSession(req, null);
                    } else {
                        logger.info("User logged in: " + accountId);
                        loginSuccessful = true;
                        setUserAndCleanSession(req, userItem);
                    }
                } else {
                    logger.info("OLI services not available, logging in user: "
                                + accountId);
                    loginSuccessful = true;
                    setUserAndCleanSession(req, userItem);
                }
            }

            loginInfo.setLoginFailed(!loginSuccessful);
            httpSession.setAttribute("ls_loginInfo", loginInfo);

            if (loginSuccessful) {
                logger.info("Login successful for user: " + accountId);

                // Ensure user has the WF role.
                grantWorkflowRole(userItem);

                // log that the user logged in in the dataset_user_log table
                UserLogger.log(userItem, UserLogger.WORKFLOWS_LOGIN);
                
                // forward to appropriate LearnSphere...
                Long workflowId =
                    (Long)req.getSession().getAttribute("workflowId");
                
                StringBuffer newUrl = new StringBuffer(LearnSphereServlet.SERVLET_NAME);
                if (workflowId != null) {
                    newUrl.append("?workflowId=" + workflowId);
                }
                logDebug("Redirecting to " + newUrl.toString());
                resp.sendRedirect(newUrl.toString());

            } else {
                logger.info("Login failed! for user: " + accountId);
                RequestDispatcher disp;
                disp = getServletContext().getRequestDispatcher(LOGIN_JSP_NAME);
                disp.forward(req, resp);
            }

        } catch (Exception exception) {
            forwardError(req, resp, logger, exception);
        } finally {
            logger.debug("doPost end");
        }
    } //end doPost()
    */

    /**
     * Find out whether this is a WebIso log in attempt by looking at the URL.
     * @param req the HTTP Servlet Request
     * @return true if it is WebIso, false otherwise
     */
    private boolean isWebIso(HttpServletRequest req) {
        // Get isWebIso flag
        boolean isWebIso = false;
        StringBuffer url = req.getRequestURL();
        if (url.indexOf("WorkflowsSSO") >= 0) {
            isWebIso = true;
        } else if (isGoogleSignIn(req) != null) {
            // Overloading webiso variable...
            isWebIso = true;
        } else {
            isWebIso = false;
        }
        return isWebIso;
    }

    /**
     * Helper method for determining if login is from Google.
     * @param req HttpServletRequest
     * @return boolean true iff Google sign-in
     */
    private  String isGoogleSignIn(HttpServletRequest req) {
        String googleAuthCode = req.getParameter("googleAuthCode");
        if (googleAuthCode != null) return googleAuthCode;
        if (req.getHeader("Authorization") == null) { return null; }
        if (req.getHeader("Authorization").startsWith("BEARER ")) {
        	return req.getHeader("Authorization").substring(7);
        }
        return null;
    }

    /* Kyped from Stackoverflow user Akhilesh Singh
     * http://stackoverflow.com/questions/16000517/how-to-get-password-from-http-basic-authentication
     */
    private  String[] getBasicAuthentication(HttpServletRequest req) {
    	String authorization = req.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = authorization.substring("Basic".length()).trim();
            String credentials = new String(Base64.getDecoder().decode(base64Credentials),
                    Charset.forName("UTF-8"));
            // credentials = username:password
        	logger.info("Got basic authentication: " + credentials);
            return credentials.split(":",2);
        } else {
        	logger.info("No basic authentication");
        }
        return null;
    }
    

    private String validateGoogleUserAndAddIfNeeded(String authCode)  {
		logger.info("Doing validateGoogleUserMethod2("  + authCode + ")");
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Arrays.asList(GOOGLE_CLIENT_ID)).setIssuer("accounts.google.com").build();

        GoogleIdToken idToken;
		try {
			idToken = verifier.verify(authCode);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.info("google verify failed gse=" + e);
			return null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.info("google verify failed io=" + e);
			e.printStackTrace();
			return null;
		}
        
        if (idToken != null) {
            Payload payload = idToken.getPayload();
            // Print user identifier
            String userId = payload.getSubject();
            // Get profile information from payload
            String email = payload.getEmail();
            String name = payload.get("name").toString();
            
            logger.info("Logged in " + userId + " " + email + " name=" + payload.get("name"));
            boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
            
            if (!emailVerified ) {
                return null;
            } else {
            		setupUserEvenIfNew(email, "plugh",name);
            		return email;
            }
        } else {
        		return null;
        }
                
    }
    
    


  
}
