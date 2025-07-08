package Spring_AdamStore.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;


@Configuration
@Profile({"dev", "test"})
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI(@Value("${open.api.title}") String title,
                           @Value("${open.api.version}") String version,
                           @Value("${open.api.description}") String description,
                           @Value("${open.api.serverUrl}") String serverUrl,
                           @Value("${open.api.serverName}") String serverName,
                           @Value("${server.servlet.context-path}") String contextPath) {

        final String securitySchemeName = "bearerAuth";

        return new OpenAPI().info(new Info().title(title)
                        .version(version)
                        .description(description)
                        .license(new License().name("Apache 2.0").url("https://springdoc.org")))
                .servers(List.of(new Server().url(serverUrl + contextPath).description(serverName)))
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT"))
                )
                .security(List.of(new SecurityRequirement().addList(securitySchemeName)));
    }

    @Bean
    public GroupedOpenApi groupedOpenApi(@Value("${open.api.api-docs}") String apiDocs){
        return GroupedOpenApi.builder()
                .group(apiDocs)
                .packagesToScan("Spring_AdamStore.controller")
                .build();
    }

}
