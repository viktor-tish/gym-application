package org.vttish.gymapplication.apigateway.config;

import org.springframework.cloud.gateway.server.mvc.config.GatewayMvcProperties;
import org.springframework.cloud.gateway.server.mvc.handler.ProxyExchange;
import org.springframework.cloud.gateway.server.mvc.handler.RestClientProxyExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.vttish.gymapplication.apigateway.logging.TracingHeadersInterceptor;

@Configuration
public class GatewayProxyConfig {

  @Bean
  public ProxyExchange gatewayProxyExchange(
      RestClient.Builder builder,
      GatewayMvcProperties properties,
      TracingHeadersInterceptor tracingInterceptor
  ) {
    return new RestClientProxyExchange(
        builder.requestInterceptor(tracingInterceptor).build(), properties
    );
  }
}
