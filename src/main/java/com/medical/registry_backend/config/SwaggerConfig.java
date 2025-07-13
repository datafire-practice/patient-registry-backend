package com.medical.registry_backend.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Medical Registry API", version = "1.0", description = "API для управления медицинскими картами"))
public class SwaggerConfig {
}