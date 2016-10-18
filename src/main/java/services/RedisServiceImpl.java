package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import redis.clients.jedis.Jedis;

public class RedisServiceImpl implements RedisService{

	
	private Logger logger;
	
	public RedisServiceImpl(){
		logger = LoggerFactory.getLogger(this.getClass());
	}
	/**
	 * Set a Key, Value tuple into the Redis service
	 * @param key
	 * @param value
	 */
	public void setValue(String key, String value) {
		Jedis jedis = null; 
		try {
			logger.info("Set key [{}] value [{}] into redis", key, value);
			jedis = RedisService.getPool().getResource();
			jedis.set(key, value);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	/**
	 * Return a value for given key from Redis service
	 */
	public String getValue(String key) {
		Jedis jedis = null;
		try {
			
			jedis = RedisService.getPool().getResource();
			String value = jedis.get(key);
			logger.info("Getting key [{}]  with value [{}] from redis", key,value);
			return jedis.get(key);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
	/**
	 * Removes a tuple from the Redis store
	 * @param key
	 */
	public void clear(String key) {
		Jedis jedis = null;
		try {
			jedis = RedisService.getPool().getResource();
			logger.info("Deleting key [{}] from Redis store", key);
			jedis.del(key);
		} finally {
			if(jedis != null) {
				jedis.close();
			}
		}
	}
} 
