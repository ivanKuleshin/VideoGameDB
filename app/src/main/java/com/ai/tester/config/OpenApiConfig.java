package com.ai.tester.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openApi() {
        SecurityScheme basicAuth = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("basic")
            .description("Enter username and password (test / test)");

        return new OpenAPI()
            .info(new Info()
                .title("Video Game DB API")
                .description("REST API for managing video games")
                .version("1.0"))
            .addServersItem(new Server().url("/").description("Default"))
            .components(new Components().addSecuritySchemes("basicAuth", basicAuth))
            .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
    }
}

