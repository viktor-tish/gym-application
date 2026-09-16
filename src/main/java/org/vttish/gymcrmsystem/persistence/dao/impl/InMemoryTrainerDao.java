package org.vttish.gymcrmsystem.persistence.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.persistence.dao.TrainerDao;

import java.util.*;

@Component
public class InMemoryTrainerDao implements TrainerDao {
    private Map<UUID, Trainer> trainerStorage;

    @Autowired
    public void setTrainerStorage(Map<UUID, Trainer> trainerStorage) {
        this.trainerStorage = trainerStorage;
    }

    @Override
    public Trainer create(Trainer trainer) {
        Assert.notNull(trainer, "Trainer must not be null");
        Assert.notNull(trainer.getUserId(), "Trainer ID must not be null");
        Assert.isTrue(!trainerStorage.containsKey(trainer.getUserId()), "Trainer already exists with such ID");

        trainerStorage.put(trainer.getUserId(), trainer);
        return trainer;
    }

    @Override
    public Optional<Trainer> selectById(UUID id) {
        Assert.notNull(id, "Trainer ID must not be null");

        return Optional.ofNullable(trainerStorage.get(id));
    }

    @Override
    public List<Trainer> selectAll() {
        return new ArrayList<>(trainerStorage.values());
    }

    @Override
    public boolean existsById(UUID id) {
        Assert.notNull(id, "Trainer ID must not be null");

        return trainerStorage.containsKey(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return trainerStorage.values().stream()
                .anyMatch(trainer -> trainer.getUsername().equals(username));
    }
}
