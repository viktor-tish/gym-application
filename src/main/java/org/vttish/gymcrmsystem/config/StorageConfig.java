package org.vttish.gymcrmsystem.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.entity.Trainer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
    public Map<UUID, Trainee> trainingStorage() {
        return new HashMap<>();
    }
}
