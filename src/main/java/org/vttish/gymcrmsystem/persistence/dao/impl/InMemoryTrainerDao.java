package org.vttish.gymcrmsystem.persistence.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.persistence.dao.TrainerDao;

import java.util.Map;
import java.util.UUID;

@Component
public class InMemoryTrainerDao implements TrainerDao {
    private Map<UUID, Trainer> trainerStorage;

    @Autowired
    public void setTrainerStorage(Map<UUID, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public boolean existsByUsername(String username) {
        return trainerStorage.values().stream()
                .anyMatch(trainer -> trainer.getUsername().equals(username));
    }
}
