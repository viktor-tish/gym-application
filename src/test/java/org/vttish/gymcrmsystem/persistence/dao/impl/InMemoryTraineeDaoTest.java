package org.vttish.gymcrmsystem.persistence.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.vttish.gymcrmsystem.entity.Trainee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryTraineeDaoTest {

    private InMemoryTraineeDao traineeDao;
    private Map<UUID, Trainee> testStorage;
    private Trainee testTrainee;

    @BeforeEach
    public void setUp() {
        traineeDao = new InMemoryTraineeDao();
        testStorage = new HashMap<>();
        traineeDao.setTraineeStorage(testStorage);

        testTrainee = new Trainee();
        testTrainee.setFirstName("firstName");
        testTrainee.setLastName("lastName");
        testTrainee.setUsername("firstName.lastName");
    }

    @Test
    public void create_shouldAddTrainee_whenValidAndNew() {
        Trainee result = traineeDao.create(testTrainee);

        assertSame(testTrainee, result);
        assertTrue(testStorage.containsKey(testTrainee.getUserId()));
        assertEquals(1, testStorage.size());
    }

    @Test
    public void create_shouldThrowException_whenIdAlreadyExists() {
        testStorage.put(testTrainee.getUserId(), testTrainee);

        assertThrows(IllegalArgumentException.class, () -> traineeDao.create(testTrainee));
    }

    @Test
    public void create_shouldThrowException_whenTraineeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> traineeDao.create(null));
    }

    @Test
    public void selectById_shouldReturnTrainee_whenExists() {
        testStorage.put(testTrainee.getUserId(), testTrainee);

        Optional<Trainee> result = traineeDao.selectById(testTrainee.getUserId());

        assertTrue(result.isPresent());
        assertSame(testTrainee, result.get());
    }

    @Test
    public void selectById_shouldReturnEmpty_whenDoesNotExist() {
        Optional<Trainee> result = traineeDao.selectById(UUID.randomUUID());

        assertTrue(result.isEmpty());
    }

    @Test
    public void selectAll_shouldReturnAllTrainees() {
        Trainee trainee2 = new Trainee();
        testStorage.put(testTrainee.getUserId(), testTrainee);
        testStorage.put(trainee2.getUserId(), trainee2);

        List<Trainee> result = traineeDao.selectAll();

        assertEquals(2, result.size());
        assertTrue(result.contains(testTrainee));
        assertTrue(result.contains(trainee2));
    }

    @Test
    public void update_shouldUpdateTrainee_whenExists() {
        testStorage.put(testTrainee.getUserId(), testTrainee);
        testTrainee.setFirstName("newFirstName");

        Trainee result = traineeDao.update(testTrainee);

        assertSame(testTrainee, result);
        assertEquals("newFirstName", testStorage.get(testTrainee.getUserId()).getFirstName());
    }

    @Test
    public void update_shouldThrowException_whenDoesNotExist() {
        assertThrows(IllegalArgumentException.class, () -> traineeDao.update(testTrainee));
    }

    @Test
    public void delete_shouldRemoveTrainee_whenExists() {
        testStorage.put(testTrainee.getUserId(), testTrainee);

        traineeDao.delete(testTrainee.getUserId());

        assertFalse(testStorage.containsKey(testTrainee.getUserId()));
        assertEquals(0, testStorage.size());
    }

    @Test
    public void delete_shouldNotFail_whenDoesNotExist() {
        assertDoesNotThrow(() -> traineeDao.delete(UUID.randomUUID()));
    }

    @Test
    public void existsByUsername_shouldReturnTrue_whenUsernameMatches() {
        testStorage.put(testTrainee.getUserId(), testTrainee);

        boolean exists = traineeDao.existsByUsername("firstName.lastName");

        assertTrue(exists);
    }

    @Test
    public void existsByUsername_shouldReturnFalse_whenUsernameDoesNotMatch() {
        testStorage.put(testTrainee.getUserId(), testTrainee);

        boolean exists = traineeDao.existsByUsername("newFirstName.lastName");

        assertFalse(exists);
    }
}
