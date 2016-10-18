package filters;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.RedisService;

import com.google.inject.Inject;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;
import ninja.Results;
import ninja.session.Session;

public class AuthenticationFilter implements Filter {

	@Inject
	RedisService redisService;
	
	@Override
	public Result filter(FilterChain chain, Context context) {
	
		
		Logger logger = LoggerFactory.getLogger(this.getClass());
		
		String sessionValueString = redisService.getValue(context.getSession().getId());
		if(sessionValueString == null) {
			logger.info("We couldn't find a value from REDIS ");
			context.getSession().clear();
			return Results.forbidden().html().template("/views/system/403forbidden.ftl.html");
		}
		
		JSONObject sessionObject = new JSONObject(sessionValueString);
		logger.info("We have retrieved a session object [{}]", sessionObject.toString());
		return chain.next(context);
	}

}
