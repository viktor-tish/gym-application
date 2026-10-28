package org.vttish.gymapplication.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.vttish.gymapplication.security.JwtAuthenticationFilter;
import org.vttish.gymapplication.security.PublicEndpoint;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final List<String> allowedOrigins;

  public SecurityConfig(@Value("${cors.allowed-origins}") List<String> allowedOrigins) {
    this.allowedOrigins = allowedOrigins;
  }

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

  @Bean
  @SuppressWarnings("java:S4502")
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter
  ) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/trainees").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/trainers").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/trainings/types").permitAll()
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/actuator/**"
            ).permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();

    configuration.setAllowedOriginPatterns(allowedOrigins);
    configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(List.of("*"));
    configuration.setExposedHeaders(List.of("Authorization", "X-Transaction-Id"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
