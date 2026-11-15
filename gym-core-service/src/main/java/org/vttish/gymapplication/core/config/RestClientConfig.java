package org.vttish.gymapplication.core.config;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.vttish.gymapplication.core.logging.TracingHeadersInterceptor;
import org.vttish.gymapplication.core.security.AuthorizationHeaderInterceptor;

@Configuration
public class RestClientConfig {

  @Bean
  @LoadBalanced
  public RestClient.Builder internalRestClientBuilder(
      RestClientBuilderConfigurer configurer,
      AuthorizationHeaderInterceptor authInterceptor,
      TracingHeadersInterceptor tracingInterceptor
  ) {
    return configurer.configure(RestClient.builder())
        .requestInterceptor(authInterceptor)
        .requestInterceptor(tracingInterceptor);
  }

  @Bean
  public RestClient workloadReportClient(RestClient.Builder internalRestClientBuilder) {
    return internalRestClientBuilder.clone()
        .baseUrl("http://gym-report-service")
        .build();
  }
}
