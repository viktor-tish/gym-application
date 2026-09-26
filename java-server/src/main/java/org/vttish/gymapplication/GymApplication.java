package org.vttish.gymapplication;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.vttish.gymapplication.config.ApplicationConfig;
import org.vttish.gymapplication.facade.GymCrmFacade;

public class GymApplication {
  public static void main(String[] args) {
    AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(ApplicationConfig.class);

    GymCrmFacade facade = context.getBean(GymCrmFacade.class);

    facade.getAllTrainees();
    facade.getAllTrainers();
    facade.getAllTrainings();

    context.close();
  }
}