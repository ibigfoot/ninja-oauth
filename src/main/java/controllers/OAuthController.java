package controllers;

import helpers.RestHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.params.Param;
import ninja.params.PathParam;
import ninja.session.Session;
import ninja.utils.NinjaProperties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.RedisService;
import services.RestAPIService;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Controller for handling the OAuth login dance
 * @author tsellers
 *
 * TODO - inject configuration for Salesforce to make this independent of Idp
 */
@Singleton
public class OAuthController {

	@Inject
	NinjaProperties props;
	
	@Inject
	RedisService redisService;
	
	@Inject
	RestAPIService restAPIService;
	
	private Logger logger;

	
	/*
	 * Current accepted values for env PathParam 
	 * TODO - accept a custom domain URL
	 */
	private static final String ENV_PROD = "prod";
	private static final String ENV_SANDBOX = "sandbox";
	private static final String ENV_CUSTOM = "custom";
	
	private static final String PROD_URL  = "https://login.salesforce.com";
	private static final String SANDBOX_URL = "https://login.salesforce.com";
	private static final String CUSTOM_URL = ".my.salesforce.com";
	
	/**
	 *  Constructor. Initialises logger
	 */
	public OAuthController() {
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public Result logout(Session session) {
	
		redisService.clear(session.getId());
		session.clear();
		
		return Results.redirect("/");
	}
	
	/**
	 * OAuth route handler method. 
	 * Will throw a RuntimeExcpetion if it receives an env parameter that is not understood
	 * 
	 * TODO - handle custom domains rather than switch on prod or sandbox.
	 * @param env
	 * @return 
	 */
	public Result oauth(@PathParam("env") String env, @Param("custom") String custom) {
		
		if(env.equals("prod") || env.equals("sandbox")) {
			return Results.redirect(buildURL(env));
		} else if(env.equals(ENV_CUSTOM)) {
			return Results.redirect(buildURL(custom));
		}else {
			logger.error("We have received an environment parameter that is not understood [{}]", env);
			//TODO - handle custom domains!
			throw new RuntimeException("We have received an environment parameter that is not understood");
		}
	
	}

	/**
	 * OAuth route handler for the callback from IdP
	 * 
	 * @param code
	 * @param state
	 * @param context
	 * @return
	 */
	public Result callback(@Param("code") String code, @Param("state") String state, Session session) {
		
		logger.info("We have received a callback with a code [{}] and state [{}]", code, state);
		
		StringBuilder url = new StringBuilder();
		
		if(state.equals(ENV_PROD)) {
			url.append(PROD_URL);
		} else if(state.equals(ENV_SANDBOX)) {
			url.append(SANDBOX_URL);
		} else {
			logger.info("We are now handling a custom domain state parameter [{}]", state);
			url.append("https://"+state+CUSTOM_URL);
		} 
 		
		url.append("/services/oauth2/token");
		
		try {
			JSONObject loginResult = restAPIService.requestAccessToken(url.toString(), code);
			// call to the SF Identity service and add these params to the result map as well 
			JSONObject identity = restAPIService.postToURL((String)loginResult.getString("id"), (String)loginResult.getString("access_token"), null);
			
			JSONObject  merged = new JSONObject(loginResult, JSONObject.getNames(loginResult));
			for(String key : JSONObject.getNames(identity)){
				merged.put(key, identity.get(key));
			}

			redisService.setValue(session.getId(), merged.toString());

			return Results.redirect("/app/appHome");

		} catch (Exception e) {
			throw new RuntimeException(e); //TODO handle exceptions cleaner
		}
		
	}
	/*
	 * Build the Salesforce URL to redirect to.
	 * TODO - externalise into an injectable handler
	 */
	private String buildURL(String env) {
		
		StringBuilder redirectURL = new StringBuilder();
		if(env.equals(ENV_PROD)) {
			redirectURL.append(PROD_URL);
		} else if (env.equals(ENV_SANDBOX)) {
			redirectURL.append(SANDBOX_URL);
		} else {
			logger.info("We should have a custom domain to login to [{}]", env);
			redirectURL.append("https://"+env + CUSTOM_URL);
		}
		
		redirectURL.append("/services/oauth2/authorize?");
		redirectURL.append("response_type=code&client_id=");
		
		try {
			redirectURL.append(URLEncoder.encode(props.get(RestAPIService.CLIENT_ID),"UTF-8"));
			redirectURL.append("&redirect_uri=");
			redirectURL.append(URLEncoder.encode(props.get(RestAPIService.REDIRECT_URI), "UTF-8"));
			redirectURL.append("&state=");
			redirectURL.append(env);
		} catch (UnsupportedEncodingException use) {
			throw new RuntimeException(use); // this error is unrecoverable.. crash.
		}
		
		logger.info("Redirecting the user to [{}]", redirectURL.toString());
		
		return redirectURL.toString();
	}	
}
