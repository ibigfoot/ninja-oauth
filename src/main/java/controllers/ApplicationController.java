/**
 * Copyright (C) 2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package controllers;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import services.RedisService;
import ninja.Context;
import ninja.Result;
import ninja.Results;

import com.google.inject.Inject;
import com.google.inject.Singleton;



@Singleton
public class ApplicationController {

	@Inject
	RedisService redisService;
	
	private Logger logger;
	
	public ApplicationController () {
		logger = LoggerFactory.getLogger(this.getClass());
	}
	
    public Result index(Context context) {
    	
    	String redisSessionString = redisService.getValue(context.getSession().getId());
    	
    	Result r = Results.html();
    	
    	// if we have a valid session in memory.. populate for template
		if(redisSessionString != null) {
			JSONObject obj = new JSONObject(redisSessionString);
			r.render(obj.toMap());
		}
		return r;

    }
    
    public Result notFound() {
    	
    	return Results.notFound().html().template("/views/system/404notFound.ftl.html");
    }
    
    public Result helloWorldJson() {
        
        SimplePojo simplePojo = new SimplePojo();
        simplePojo.content = "Hello World! Hello Json!";
        logger.info("Setting content [{}]", simplePojo.content);
        
        return Results.json().render(simplePojo);

    }
    
    public static class SimplePojo {

        public String content;
        
    }
}
