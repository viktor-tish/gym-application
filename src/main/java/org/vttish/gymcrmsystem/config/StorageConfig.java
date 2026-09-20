package org.vttish.gymcrmsystem.config;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.entity.Training;

@Configuration
public class StorageConfig {

  @Bean
  public Map<UUID, Trainer> trainerStorage() {
    return new HashMap<>();
  }

  @Bean
  public Map<UUID, Trainee> traineeStorage() {
    return new HashMap<>();
  }

  @Bean
  public Map<UUID, Training> trainingStorage() {
    return new HashMap<>();
  }
}
