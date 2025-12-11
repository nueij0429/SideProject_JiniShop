package com.jinishop.jinishop.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
@EnableCaching
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration
                .defaultCacheConfig()
                // key는 String으로
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new StringRedisSerializer()
                        )
                )
                // value는 JSON으로
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                )
                .entryTtl(Duration.ofMinutes(5))   // TTL 5분
                //.entryTtl(Duration.ofSeconds(5))   // 테스트용 TTL 5초
                .disableCachingNullValues();

        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(defaultConfig)
                .build();
    }
}