/**
 * 
 */
package com.amnex.agristack.gateway.config;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author majid.belim
 *
 */

@Service
public class RedisService {
	@Autowired
	private RedisTemplate<String, Object> template;

	private HashOperations<String, Object, Object> hashOperations;

	@PostConstruct
	public void init() {
		hashOperations = template.opsForHash();
	}

	public Object getValue(String key) throws Exception {
		return template.opsForValue().get(key);
	}

	public void setValue(String key, Object value) throws Exception {
		template.opsForValue().set(key, value);
	}

	public RedisConnectionFactory getRedisConnectionFactory() throws Exception {
		return template.getConnectionFactory();
	}

	public RedisConnection getRedisConnection() throws Exception {
		return getRedisConnectionFactory().getConnection();
	}

	public boolean checkKey(String key) {
		return template.hasKey(key);
	}

	public void disconnect() throws Exception {
		RedisConnection connection = getRedisConnection();
		if (!connection.isClosed()) {
			connection.close();
		}
	}

	public boolean deleteKey(String key) {
		template.delete(key);
		return checkKey(key);
	}

	public void addHashSet(String mainKey, Object key, Object value) {
		hashOperations.put(mainKey, key, value);
	}

	public Object findHashSetValue(String mainKey, Object key) {
		return hashOperations.get(mainKey, key);
	}

	public Map<Object, Object> findHashMap(String mainKey) {
		return hashOperations.entries(mainKey);
	}

	public RedisTemplate<String, Object> getJadis() {
		return template;
	}

	public Set<String> getKeys() {
		return template.keys("*");
	}

	public Set<String> getWildcardKeys(String wildcard) {
		return template.keys(wildcard);
	}
	
	public void expireKey(String key, Integer second){
		template.expire(key, second, TimeUnit.SECONDS);
	}

}
