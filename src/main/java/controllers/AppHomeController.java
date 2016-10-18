package controllers;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.RedisService;
import ninja.FilterWith;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import filters.AuthenticationFilter;

@Singleton
public class AppHomeController {

	@Inject
	RedisService redisService;
	
	private Logger logger;
	
    @FilterWith(AuthenticationFilter.class)
	public Result home(Session session) {
	
		logger = LoggerFactory.getLogger(this.getClass());
		
		JSONObject obj = new JSONObject(redisService.getValue(session.getId()));
		Result r = Results.html();
		r.render(obj.toMap());
		
		return r;
	}
}
