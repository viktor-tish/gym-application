package org.vttish.gymapplication.config;

import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.vttish.gymapplication.logging.TracingHeadersInterceptor;
import org.vttish.gymapplication.security.AuthorizationHeaderInterceptor;

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
        .baseUrl("http://gym-workload-report")
        .build();
  }
}
