package org.vttish.gymapplication.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.configuration.SpringDocConfiguration;
import org.springdoc.core.properties.SwaggerUiConfigProperties;
import org.springdoc.core.properties.SwaggerUiOAuthProperties;
import org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration;
import org.springdoc.webmvc.ui.SwaggerConfig;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ComponentScan(basePackages = "org.springdoc")
@Import({
    SpringDocConfiguration.class,
    SpringDocWebMvcConfiguration.class,
    SwaggerConfig.class,
    SwaggerUiConfigProperties.class,
    SwaggerUiOAuthProperties.class,
    JacksonAutoConfiguration.class
})
public class OpenApiConfig {
  private static final String SECURITY_SCHEME_NAME = "Bearer Authentication";

  @Bean
  public OpenAPI openApi() {
    return new OpenAPI()
        .info(new Info()
            .title("Gym Application API")
            .version("1.0")
            .description("REST API documentation for Gym Application"))
        .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
        .components(new Components().addSecuritySchemes(
            SECURITY_SCHEME_NAME,
            new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
        ));
  }
}
