package com.example.springgateway.config;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public GroupedOpenApi customOpenApi() {
        return GroupedOpenApi.builder()
                .group("gateway")
                .pathsToMatch("/users/**", "/ads/**")
                .displayName("Gateway API")
                .build();
    }
}