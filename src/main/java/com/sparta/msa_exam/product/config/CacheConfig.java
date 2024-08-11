package com.sparta.msa_exam.product.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {
	private final ObjectMapper objectMapper;

	public CacheConfig(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	@Bean
	public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
		RedisCacheConfiguration configuration = RedisCacheConfiguration
				.defaultCacheConfig()
				.disableCachingNullValues()//null을 캐싱하는 지
				.entryTtl(Duration.ofSeconds(60))// 기본 캐시 유지 시간
				.computePrefixWith(CacheKeyPrefix.simple())
				// 캐시에 저장할 값을 어떻게 직렬화
				.serializeValuesWith(
						RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer(objectMapper))//RedisSerializer.json()
				);

		// configuration 기본 설정
		return RedisCacheManager
				.builder(redisConnectionFactory)
				.cacheDefaults(configuration)
				.build();
	}
}