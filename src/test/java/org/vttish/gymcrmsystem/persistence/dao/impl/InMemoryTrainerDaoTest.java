package org.vttish.gymcrmsystem.persistence.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.entity.Trainer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTrainerDaoTest {
    private InMemoryTrainerDao trainerDao;
    private Map<UUID, Trainer> testStorage;
    private Trainer testTrainer;

    @BeforeEach
    public void setUp() {
        trainerDao = new InMemoryTrainerDao();
        testStorage = new HashMap<>();
        trainerDao.setTrainerStorage(testStorage);

        testTrainer = new Trainer();
        testTrainer.setFirstName("firstName");
        testTrainer.setLastName("lastName");
        testTrainer.setUsername("firstName.lastName");
    }

    @Test
    public void existsByUsername_shouldReturnTrue_whenUsernameMatches() {
        testStorage.put(testTrainer.getUserId(), testTrainer);

        boolean exists = trainerDao.existsByUsername("firstName.lastName");

        assertTrue(exists);
    }

    @Test
    public void existsByUsername_shouldReturnFalse_whenUsernameDoesNotMatch() {
        testStorage.put(testTrainer.getUserId(), testTrainer);

        boolean exists = trainerDao.existsByUsername("newFirstName.lastName");

        assertFalse(exists);
    }
}