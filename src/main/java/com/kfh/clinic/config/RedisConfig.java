package com.kfh.clinic.config;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class RedisConfig {

	/**
	 * Creates a Jackson ObjectMapper configured for Java 8 time types.
	 * GenericJackson2JsonRedisSerializer handles type information automatically using @class property.
	 */
	private ObjectMapper createObjectMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());
		mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		return mapper;
	}

	/**
	 * Creates a Jackson ObjectMapper for cache serialization.
	 * The @JsonTypeInfo annotations on DTOs will provide type information.
	 */
	// private ObjectMapper createCacheObjectMapper() {
	// 	ObjectMapper mapper = new ObjectMapper();
	// 	mapper.registerModule(new JavaTimeModule());
	// 	mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
	// 	return mapper;
	// }

	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>();
		template.setConnectionFactory(connectionFactory);
		
		// Use String serializer for keys
		template.setKeySerializer(new StringRedisSerializer());
		// template.setHashKeySerializer(new StringRedisSerializer());
		
		// Use JSON serializer for values with Java 8 time support
		GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(createObjectMapper());
		// Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
		template.setValueSerializer(valueSerializer);
		// template.setHashValueSerializer(valueSerializer);
		
		// template.afterPropertiesSet();
		return template;
	}

	@Bean
	public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
		// Configure Redis cache with ObjectMapper that includes type information for collections
		// This ensures List<DoctorDTO> is properly deserialized
		GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer();
		// Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);

		RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofMinutes(10)) // 10 minutes TTL
				.disableCachingNullValues() // Don't cache null values
				.prefixCacheNameWith("clinic:cache:") // Key prefix
				.serializeKeysWith(RedisSerializationContext.SerializationPair
						.fromSerializer(new StringRedisSerializer()))
				.serializeValuesWith(RedisSerializationContext.SerializationPair
						.fromSerializer(valueSerializer));

		return RedisCacheManager.builder(connectionFactory)
				.cacheDefaults(cacheConfig)
				.build();
	}
}

