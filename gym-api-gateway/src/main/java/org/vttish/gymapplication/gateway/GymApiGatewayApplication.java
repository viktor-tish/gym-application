package org.vttish.gymapplication.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GymApiGatewayApplication {

  public static void main(String[] args) {
    SpringApplication.run(GymApiGatewayApplication.class, args);
  }

}
