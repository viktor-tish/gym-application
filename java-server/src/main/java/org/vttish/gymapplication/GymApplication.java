package org.vttish.gymapplication;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.vttish.gymapplication.config.ApplicationConfig;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.facade.GymFacade;

@Slf4j
public class GymApplication {

  public static void main(String[] args) {
    log.info("Starting Gym Application...");
    
    AnnotationConfigApplicationContext context =
        new AnnotationConfigApplicationContext(ApplicationConfig.class);

    GymFacade facade = context.getBean(GymFacade.class);

    final CreateTraineeDto traineeDto = new CreateTraineeDto();
    traineeDto.setFirstName("John");
    traineeDto.setLastName("Doe");
    
    CredentialsDto credentialsDto = facade.createTrainee(traineeDto);

    log.info("Successfully created Trainee! Username: {}, Password: {}",
        credentialsDto.getUsername(), credentialsDto.getPassword()
    );

    context.close();
    log.info("Application shut down cleanly.");
  }
}