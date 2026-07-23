package com.reimbursement.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Springdoc OpenAPI / Swagger configuration for the ERS REST API.
 */
@Configuration
public class OpenApiConfig {

    @Value("${ers.api.server-url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI ersOpenAPI() {
        final String sessionScheme = "sessionCookie";

        return new OpenAPI()
                .info(new Info()
                        .title("Employee Reimbursement System API")
                        .description("""
                                Production API for multi-stage reimbursement approval, budget enforcement,
                                and vendor/ERP posting. Authenticate via POST /users/login; subsequent
                                requests must include the JSESSIONID session cookie (withCredentials).
                                """)
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Madasu Rakesh")
                                .url("https://github.com/rakeshrakhi9392/FinFlow"))
                        .license(new License().name("MIT").url("https://opensource.org/licenses/MIT")))
                .servers(List.of(new Server().url(serverUrl).description("API server")))
                .components(new Components()
                        .addSecuritySchemes(sessionScheme, new SecurityScheme()
                                .name("JSESSIONID")
                                .type(SecurityScheme.Type.APIKEY)
                                .in(SecurityScheme.In.COOKIE)
                                .description("Session cookie established by POST /users/login")))
                .addSecurityItem(new SecurityRequirement().addList(sessionScheme));
    }
}
