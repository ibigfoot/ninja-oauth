package services;

import java.net.URI;
import java.net.URISyntaxException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public interface RedisService {

	public static Jedis getConnection() {
		try {
			
			URI redisURI = getRedisURI();
			Jedis jedis = new Jedis(redisURI);
			return jedis;
			
		} catch (Exception e) {
			e.printStackTrace();
			 // redis is going to handle session security.. crash if it isn't available. TODO - handle
			throw new RuntimeException(e.getMessage());		
		}
	}

	public static JedisPool getPool() {
		try {

			URI redisURI = getRedisURI();			
		    JedisPoolConfig poolConfig = new JedisPoolConfig();
		    poolConfig.setMaxIdle(5);
		    poolConfig.setMinIdle(1);
		    poolConfig.setTestOnBorrow(true);
		    poolConfig.setTestOnReturn(true);
		    poolConfig.setTestWhileIdle(true);
		    
		    JedisPool pool = new JedisPool(poolConfig, redisURI);
		    return pool;
		    
		} catch (Exception e) {
			e.printStackTrace();
			// redis handles session security, crash if it isn't available .. TODO - handle
			throw new RuntimeException(e.getMessage());
		}
	}
	
	public static URI getRedisURI() throws URISyntaxException {
		String redisUrl = System.getenv("REDIS_URL");
		// test otherwise use redis configured for junit tests 
		if(redisUrl == null) {
			redisUrl = "http://localhost:6379";
		}
		return new URI(redisUrl);		
	}
	
	public void setValue(String key, String value);
	public String getValue(String key);
	public void clear(String key);
}
