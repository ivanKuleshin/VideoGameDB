package com.techietester.app;

import com.techietester.resource.VideoGameResource;
import io.swagger.v3.jaxrs2.integration.resources.OpenApiResource;
import io.swagger.v3.oas.integration.SwaggerConfiguration;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.glassfish.jersey.jackson.JacksonFeature;
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

        // JSON serialization via Jackson
        register(JacksonFeature.class);

        // OpenAPI spec endpoint
        OpenAPI openAPI = new OpenAPI()
                .info(new Info()
                        .title("Video Game DB API")
                        .description("REST API for managing video games")
                        .version("1.0"))
                .addServersItem(new Server().url("/app").description("Default"));

        SwaggerConfiguration config = new SwaggerConfiguration()
                .openAPI(openAPI)
                .prettyPrint(true)
                .resourcePackages(Set.of("com.techietester.resource"));

        OpenApiResource openApiResource = new OpenApiResource();
        openApiResource.openApiConfiguration(config);
        register(openApiResource);
    }
}
