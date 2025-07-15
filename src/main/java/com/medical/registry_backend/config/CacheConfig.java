package com.medical.registry_backend.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();
        cacheManager.setCaffeine(caffeineCacheBuilder());
        // Укажите имена кэшей, если хотите ограничить их
//        cacheManager.setCacheNames("patients", "diseases", "mkb10");
        return cacheManager;
    }

    private Caffeine<Object, Object> caffeineCacheBuilder() {
        return Caffeine.newBuilder()
                .initialCapacity(100) // Начальная емкость кэша
                .maximumSize(500) // Максимальное количество записей в кэше
                .expireAfterAccess(10, TimeUnit.MINUTES) // Время жизни записи после последнего доступа
                .expireAfterWrite(15, TimeUnit.MINUTES) // Время жизни записи после записи
                .recordStats(); // Включение статистики (опционально)
    }
}