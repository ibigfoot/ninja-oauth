package helpers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

public class RestHelper {

	private Logger logger;
	
	public RestHelper() {
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
	public Map<String, Object> getFromUrl(String url, String accessToken, Map<String, String> urlParams)
		throws JSONException, UnsupportedEncodingException, ClientProtocolException, IOException {
		
		if(urlParams != null && !urlParams.isEmpty()) {
			url += "?";
			for(String s : urlParams.keySet()) {
				url += s + "=" + URLEncoder.encode(urlParams.get(s), "UTF-8");
			}
		}
		logger.info("We are calling GET on [{}]"+url);
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpGet get = new HttpGet(url);
		
		get.addHeader("Authorization:", "Bearer "+accessToken);
		HttpResponse response = client.execute(get);
		
		return processResponse(response);
	}
	
	public Map<String, Object> postToUrl(String url, String accessToken, Map<String, String> postParams) 
			throws JSONException, UnsupportedEncodingException, ClientProtocolException, IOException{
		
		HttpClient client = HttpClientBuilder.create().build();
		HttpPost post = new HttpPost(url);

		post.addHeader("Authorization:", "Bearer "+accessToken);
		
		if(postParams != null && !postParams.isEmpty()) {
			List<NameValuePair> urlParams = new ArrayList<NameValuePair>();
			
			for(String s : postParams.keySet()) {
				urlParams.add(new BasicNameValuePair(s, postParams.get(s)));
			}
			post.setEntity(new UrlEncodedFormEntity(urlParams));	
		}
		logger.info("Posting to URL [{}]", post.getURI());
		HttpResponse postResponse = client.execute(post);
		

		return processResponse(postResponse);
		
	}
	
	/*
	 * Processes the HTTPResponse into a JSONObject
	 */
	@SuppressWarnings("serial")
	public Map<String,Object> processResponse(HttpResponse response) throws IOException, JSONException {
		
		logger.info("Response code [{}]", response.getStatusLine());
		
		BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				
		StringBuffer result = new StringBuffer();
		String line = "";
		
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		logger.info(result.toString());
		
		return new Gson().fromJson(result.toString(), new TypeToken<HashMap<String, Object>>() {}.getType());
	}		
}
