package org.vttish.gymapplication.config;

import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.vttish.gymapplication.security.PublicEndpoint;

@Configuration
public class SecurityConfig {

  @Bean
  public List<PublicEndpoint> whiteList() {
    return List.of(
        PublicEndpoint.of(HttpMethod.POST, "/api/v1/auth/login"),
        PublicEndpoint.of(HttpMethod.POST, "/api/v1/trainees"),
        PublicEndpoint.of(HttpMethod.POST, "/api/v1/trainers"),
        PublicEndpoint.of(HttpMethod.GET, "/api/v1/trainings/types"),
        PublicEndpoint.anyMethod("/swagger-ui/**"),
        PublicEndpoint.anyMethod("/swagger-ui.html"),
        PublicEndpoint.anyMethod("/v3/api-docs/**"),
        PublicEndpoint.anyMethod("/actuator/**")
    );
  }
}
