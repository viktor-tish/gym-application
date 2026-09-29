package org.vttish.gymapplication;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.vttish.gymapplication.config.ApplicationConfig;

public class GymApplication {
  public static void main(String[] args) {
    AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(ApplicationConfig.class);

    context.close();
  }
}