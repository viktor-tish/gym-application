package org.vttish.gymapplication.core.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
import org.vttish.gymapplication.core.security.DelegatingAccessDeniedHandler;
import org.vttish.gymapplication.core.security.DelegatingAuthenticationEntryPoint;
import org.vttish.gymapplication.core.security.filter.InternalJwtAuthenticationFilter;
import org.vttish.gymapplication.core.security.filter.LoginAttemptFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Bean
  public FilterRegistrationBean<LoginAttemptFilter> loginAttemptFilterRegistration(
      LoginAttemptFilter filter
  ) {
    FilterRegistrationBean<LoginAttemptFilter> registration =
        new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);

    return registration;
  }

  @Bean
  public FilterRegistrationBean<InternalJwtAuthenticationFilter> jwtAuthFilterRegistration(
      InternalJwtAuthenticationFilter filter
  ) {
    FilterRegistrationBean<InternalJwtAuthenticationFilter> registration =
        new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);

    return registration;
  }

  @Bean
  @SuppressWarnings("java:S4502")
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      LoginAttemptFilter loginAttemptFilter,
      InternalJwtAuthenticationFilter internalJwtAuthenticationFilter,
      DelegatingAuthenticationEntryPoint authenticationEntryPoint,
      DelegatingAccessDeniedHandler accessDeniedHandler
  ) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .exceptionHandling(ex -> ex
            .authenticationEntryPoint(authenticationEntryPoint)
            .accessDeniedHandler(accessDeniedHandler))
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .logout(AbstractHttpConfigurer::disable)
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
        .addFilterBefore(internalJwtAuthenticationFilter,
            UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(loginAttemptFilter, InternalJwtAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource(
      @Value("${app.security.cors.allowed-origins}") List<String> allowedOrigins
  ) {
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
