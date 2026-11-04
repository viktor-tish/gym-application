package org.vttish.gymapplication.discovery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class GymDiscoveryApplication {

  public static void main(String[] args) {
    SpringApplication.run(GymDiscoveryApplication.class, args);
  }
}
