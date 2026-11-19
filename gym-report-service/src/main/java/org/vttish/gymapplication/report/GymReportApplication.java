package org.vttish.gymapplication.report;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class GymReportApplication {

  public static void main(String[] args) {
    SpringApplication.run(GymReportApplication.class, args);
  }

}
