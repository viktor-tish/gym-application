package org.vttish.gymcrmsystem.persistence.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.persistence.dao.TraineeDao;

import java.util.*;

@Component
public class InMemoryTraineeDao implements TraineeDao {
    private Map<UUID, Trainee> traineeStorage;

    @Autowired
    public void setTraineeStorage(Map<UUID, Trainee> traineeStorage) {
        this.traineeStorage = traineeStorage;
    }

    @Override
    public Trainee create(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee must not be null");
        }

        if (trainee.getUserId() == null) {
            throw new IllegalArgumentException("Trainee ID must not be null");
        }

        if (traineeStorage.containsKey(trainee.getUserId())) {
            throw new IllegalArgumentException("Trainee already exists with such ID");
        }

        traineeStorage.put(trainee.getUserId(), trainee);
        return trainee;
    }

    @Override
    public Optional<Trainee> selectById(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee ID must not be null");
        }

        return Optional.ofNullable(traineeStorage.get(id));
    }

    @Override
    public List<Trainee> selectAll() {
        return new ArrayList<>(traineeStorage.values());
    }

    @Override
    public Trainee update(Trainee trainee) {
        if (trainee == null) {
            throw new IllegalArgumentException("Trainee must not be null");
        }

        if (trainee.getUserId() == null) {
            throw new IllegalArgumentException("Trainee ID must not be null");
        }

        if (!traineeStorage.containsKey(trainee.getUserId())) {
            throw new IllegalArgumentException("Trainee does not exist with such ID");
        }

        traineeStorage.put(trainee.getUserId(), trainee);
        return trainee;
    }

    @Override
    public void delete(UUID id) {
        if (id == null) {
            throw new IllegalArgumentException("Trainee ID must not be null");
        }

        traineeStorage.remove(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return traineeStorage.values().stream()
                .anyMatch(trainee -> trainee.getUsername().equals(username));
    }
}
