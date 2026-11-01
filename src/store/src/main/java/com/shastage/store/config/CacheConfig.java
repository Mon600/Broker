package com.shastage.store.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import java.time.Duration;

@Configuration
@Slf4j
public class CacheConfig implements CachingConfigurer {

    @Override
    public CacheErrorHandler errorHandler() {
        return new CacheErrorHandler() {
            @Override
            public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
                log.error("Redis не ответил на чтение (GET). Кэш: {}, Ключ: {}. Ошибка: {}", cache.getName(), key, exception.getMessage());
                return;
            }

            @Override
            public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
                log.error("Redis не ответил на запись (PUT). Кэш: {}, Ключ: {}. Ошибка: {}", cache.getName(), key, exception.getMessage());
                return;
            }

            @Override
            public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
                log.error("Redis не ответил на удаление (EVICT). Кэш: {}, Ключ: {}. Ошибка: {}", cache.getName(), key, exception.getMessage());
                return;
            }

            @Override
            public void handleCacheClearError(RuntimeException exception, Cache cache) {
                log.error("Redis не ответил на очистку (CLEAR). Кэш: {}. Ошибка: {}", cache.getName(), exception.getMessage());
                return;
            }
        };
    }

    @Bean("cacheManager")
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10));
        return RedisCacheManager.builder(redisConnectionFactory).
                cacheDefaults(defaultConfig)
                .build();
    }
}
