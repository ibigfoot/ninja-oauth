package services;

import java.util.Map;

import org.json.JSONObject;

public interface RestAPIService {

	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";
	public static final String REDIRECT_URI = "redirect_uri";
	
	public JSONObject getFromURL(String url, String accessToken, Map<String, String> params);
	public JSONObject postToURL(String url, String accessToken, Map<String, String> params);
	public JSONObject requestAccessToken(String url, String code);
}
