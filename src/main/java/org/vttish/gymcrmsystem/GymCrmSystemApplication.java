package org.vttish.gymcrmsystem;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.vttish.gymcrmsystem.config.ApplicationConfig;
import org.vttish.gymcrmsystem.facade.GymCrmFacade;

public class GymCrmSystemApplication {
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