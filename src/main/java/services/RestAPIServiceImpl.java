package services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import ninja.utils.NinjaProperties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class RestAPIServiceImpl implements RestAPIService{
	
	private Logger logger;
	
	@Inject
	NinjaProperties props;
	
	public RestAPIServiceImpl() {
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	@Override
	public JSONObject getFromURL(String url, String accessToken, Map<String, String> params) {
		
		try {
			if(params != null && !params.isEmpty()) {
				url += "?";
				for(String s : params.keySet()) {
					url += s + "=" + URLEncoder.encode(params.get(s), "UTF-8");
				}
			}
			logger.info("We are calling GET on [{}]"+url);
			
			HttpClient client = HttpClientBuilder.create().build();
			HttpGet get = new HttpGet(url);
			
			get.addHeader("Authorization:", "Bearer "+accessToken);
			HttpResponse response = client.execute(get);
			return processResponse(response);
			
		} catch(Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e); //TODO - handle gracefully
		}
		
	}
	@Override
	public JSONObject postToURL(String url, String accessToken, Map<String, String> params) {
		
		try {
			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(url);
	
			post.addHeader("Authorization:", "Bearer "+accessToken);
			
			if(params != null && !params.isEmpty()) {
				List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
				
				for(String s : params.keySet()) {
					urlParams.add(new BasicNameValuePair(s, params.get(s)));
				}
				post.setEntity(new UrlEncodedFormEntity(urlParams));	
			}
			logger.info("Posting to URL [{}]", post.getURI());
			HttpResponse postResponse = client.execute(post);
			
	
			return processResponse(postResponse);
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e); // TODO - handle gracefully
		}
	}
	
	@Override
	public JSONObject requestAccessToken(String url, String code) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);
		
		List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
		
		urlParams.add(new BasicNameValuePair("code", code));
		urlParams.add(new BasicNameValuePair("grant_type", "authorization_code"));
		urlParams.add(new BasicNameValuePair("client_id", props.get(CLIENT_ID)));
		urlParams.add(new BasicNameValuePair("client_secret", props.get(CLIENT_SECRET)));
		urlParams.add(new BasicNameValuePair("redirect_uri", props.get(REDIRECT_URI)));
		
		try {
			post.setEntity(new UrlEncodedFormEntity(urlParams));
			return processResponse(client.execute(post));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e); //TODO handle gracefully
		}
	}	
	
	private JSONObject processResponse(HttpResponse response) throws IOException{
		
		logger.info("Response code [{}]", response.getStatusLine());
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				
		StringBuffer result = new StringBuffer();
		String line = "";
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		logger.debug(result.toString());
		return new JSONObject(result.toString());
	}



}
