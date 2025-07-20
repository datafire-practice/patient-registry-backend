package com.medical.registry_backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // Разрешить CORS для всех эндпоинтов
                .allowedOrigins("http://localhost:5173") // Укажите домены фронтенда
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // Разрешённые методы
                .allowedHeaders("*") // Разрешённые заголовки
                .allowCredentials(true) // Разрешить отправку куки, если нужно
                .maxAge(3600); // Время кэширования CORS-запросов
    }
}