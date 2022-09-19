package com.microtf.framework.configure;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * 项目redis配置，
 * 主要是设置redis序列花，以方便阅读
 *
 * @author glzaboy
 */
@Configuration
@Slf4j
public class RedisConfiguration {
    /**
     * RedisTemplate配置
     *
     * @param connectionFactory RedisConnectionFactory工厂
     * @return RedisTemplate实例
     */
    @Bean
    @ConditionalOnClass(value = {RedisConnectionFactory.class})
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        log.info("初始化RedisTemplate");
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(connectionFactory);
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

    /**
     * 缓存配置
     *
     * @param connectionFactory RedisConnectionFactory工厂
     * @return CacheManager缓存配置
     */
    @Bean
    @ConditionalOnClass(value = {RedisConnectionFactory.class})
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        log.info("初始化CacheManager");
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofDays(30))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();
        return RedisCacheManager.builder(connectionFactory).cacheDefaults(redisCacheConfiguration).build();
    }
}
