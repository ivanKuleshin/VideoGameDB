package com.ai.tester.app;

import com.ai.tester.resource.VideoGameResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.glassfish.jersey.jaxb.internal.JaxbAutoDiscoverable;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class AppResourceConfig extends ResourceConfig {

    public AppResourceConfig() {
        // JAX-RS resource
        register(VideoGameResource.class);

        // XML serialization via JAXB
        register(JaxbAutoDiscoverable.class);

        // JSON serialization via Jackson with LocalDate as ISO string
        ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        register(new JacksonJaxbJsonProvider(
            objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS));
        register(JacksonFeature.class);

        // OpenAPI spec endpoint
        SecurityScheme basicAuth = new SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("basic")
            .description("Enter username and password (test / test)");

        OpenAPI openAPI = new OpenAPI()
            .info(new Info()
                .title("Video Game DB API")
                .description("REST API for managing video games")
                .version("1.0"))
            .addServersItem(new Server().url("/app").description("Default"))
            .components(new Components()
                .addSecuritySchemes("basicAuth", basicAuth))
            .addSecurityItem(new SecurityRequirement().addList("basicAuth"));

        SwaggerConfiguration config = new SwaggerConfiguration()
            .openAPI(openAPI)
            .prettyPrint(true)
            .resourcePackages(Set.of("com.ai.tester.resource"));

        OpenApiResource openApiResource = new OpenApiResource();
        openApiResource.openApiConfiguration(config);
        register(openApiResource);
    }
}
