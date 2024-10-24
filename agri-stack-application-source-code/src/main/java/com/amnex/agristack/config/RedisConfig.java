package com.amnex.agristack.config;

import java.io.IOException;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import org.xerial.snappy.Snappy;
import redis.clients.jedis.JedisPoolConfig;

@Configuration
public class RedisConfig {

	@Autowired
	private Environment environment;

	@SuppressWarnings("deprecation")
	@Bean
	JedisConnectionFactory jedisConnectionFactory() throws UnknownHostException {

		JedisPoolConfig poolConfig = new JedisPoolConfig();
		poolConfig.setMaxTotal(20);
		poolConfig.setMaxIdle(5);
		poolConfig.setMinIdle(2);
		poolConfig.setTestOnBorrow(true);
		poolConfig.setTestOnReturn(true);


		JedisConnectionFactory factory = new JedisConnectionFactory(poolConfig);
		factory.setHostName(environment.getProperty("redis.hostname"));
		factory.setPassword(environment.getProperty("redis.password"));
		factory.setPort(Integer.parseInt(environment.getProperty("redis.port")));
		factory.setUsePool(Boolean.parseBoolean(environment.getProperty("redis.usepool")));
		factory.setDatabase(Integer.parseInt(environment.getProperty("redis.databaseid")));

		return factory;
	}

	@Bean
	RedisTemplate<String, Object> redisTemplate() throws UnknownHostException, Exception {
		final RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(jedisConnectionFactory());
		template.setKeySerializer(new StringRedisSerializer());
		template.setHashValueSerializer(new GenericToStringSerializer<Object>(Object.class));
		template.setValueSerializer(new GenericToStringSerializer<Object>(Object.class));

		template.setEnableTransactionSupport(true);
		return template;
	}


}
