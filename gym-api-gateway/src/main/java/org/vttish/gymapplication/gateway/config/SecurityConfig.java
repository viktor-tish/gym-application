package org.vttish.gymapplication.gateway.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.AuthorizationFilter;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.vttish.gymapplication.gateway.security.DelegatingAccessDeniedHandler;
import org.vttish.gymapplication.gateway.security.DelegatingAuthenticationEntryPoint;
import org.vttish.gymapplication.gateway.security.filter.InternalJwtFilter;
import org.vttish.gymapplication.gateway.security.filter.JwtAuthenticationFilter;
import org.vttish.gymapplication.gateway.security.JwtLogoutHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public FilterRegistrationBean<JwtAuthenticationFilter> jwtAuthenticationFilterRegistration(
      JwtAuthenticationFilter filter
  ) {
    FilterRegistrationBean<JwtAuthenticationFilter> registration =
        new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);

    return registration;
  }

  @Bean
  public FilterRegistrationBean<InternalJwtFilter> internalJwtFilterRegistration(
      InternalJwtFilter filter
  ) {
    FilterRegistrationBean<InternalJwtFilter> registration =
        new FilterRegistrationBean<>(filter);
    registration.setEnabled(false);

    return registration;
  }

  @Bean
  @SuppressWarnings("java:S4502")
  public SecurityFilterChain securityFilterChain(
      HttpSecurity http,
      JwtAuthenticationFilter jwtAuthenticationFilter,
      InternalJwtFilter internalJwtFilter,
      DelegatingAccessDeniedHandler accessDeniedHandler,
      DelegatingAuthenticationEntryPoint authenticationEntryPoint,
      JwtLogoutHandler jwtLogoutHandler
  ) {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .cors(Customizer.withDefaults())
        .exceptionHandling(ex -> ex
            .accessDeniedHandler(accessDeniedHandler)
            .authenticationEntryPoint(authenticationEntryPoint))
        .sessionManagement(session ->
            session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .logout(logout -> logout
            .logoutRequestMatcher(PathPatternRequestMatcher.withDefaults()
                .matcher(HttpMethod.POST, "/api/v1/auth/logout"))
            .addLogoutHandler(jwtLogoutHandler)
            .logoutSuccessHandler(
                new HttpStatusReturningLogoutSuccessHandler(HttpStatus.NO_CONTENT)))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(HttpMethod.POST, "/api/v1/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/trainees").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v1/trainers").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v1/trainings/types").permitAll()
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v3/api-docs/**",
                "/actuator/**",
                "/docs/**"
            ).permitAll()
            .anyRequest().authenticated())
        .addFilterBefore(jwtAuthenticationFilter, LogoutFilter.class)
        .addFilterAfter(internalJwtFilter, AuthorizationFilter.class);

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
