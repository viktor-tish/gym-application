package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymapplication.persistence.dao.TraineeDao;
import org.vttish.gymapplication.persistence.dao.TrainerDao;

@ExtendWith(MockitoExtension.class)
class DefaultProfileGenerationServiceTest {

  @Mock
  private TraineeDao traineeDao;

  @Mock
  private TrainerDao trainerDao;

  @InjectMocks
  private DefaultProfileGenerationService profileGenerationService;

  @Test
  void generateUniqueUsername_shouldReturnBaseName_whenNoCollisions() {
    when(traineeDao.existsByUsername("firstName.lastName")).thenReturn(false);
    when(trainerDao.existsByUsername("firstName.lastName")).thenReturn(false);

    String result = profileGenerationService.generateUniqueUsername("firstName", "lastName");

    assertEquals("firstName.lastName", result);
    verify(traineeDao).existsByUsername("firstName.lastName");
    verify(trainerDao).existsByUsername("firstName.lastName");
  }

  @Test
  void generateUniqueUsername_shouldAddSuffix_whenExistsInTraineeDao() {
    when(traineeDao.existsByUsername("firstName.lastName")).thenReturn(true);

    when(traineeDao.existsByUsername("firstName.lastName1")).thenReturn(false);
    when(trainerDao.existsByUsername("firstName.lastName1")).thenReturn(false);

    String result = profileGenerationService.generateUniqueUsername("firstName", "lastName");

    assertEquals("firstName.lastName1", result);
  }

  @Test
  void generateUniqueUsername_shouldAddSuffix_whenExistsInTrainerDao() {
    when(traineeDao.existsByUsername("firstName.lastName")).thenReturn(false);
    when(trainerDao.existsByUsername("firstName.lastName")).thenReturn(true);
    when(traineeDao.existsByUsername("firstName.lastName1")).thenReturn(false);
    when(trainerDao.existsByUsername("firstName.lastName1")).thenReturn(false);

    String result = profileGenerationService.generateUniqueUsername("firstName", "lastName");

    assertEquals("firstName.lastName1", result);
  }

  @Test
  void generateUniqueUsername_shouldIncrementSuffix_whenMultipleCollisionsOccur() {
    when(traineeDao.existsByUsername("firstName.lastName")).thenReturn(true);
    when(traineeDao.existsByUsername("firstName.lastName1")).thenReturn(false);
    when(trainerDao.existsByUsername("firstName.lastName1")).thenReturn(true);
    when(traineeDao.existsByUsername("firstName.lastName2")).thenReturn(false);
    when(trainerDao.existsByUsername("firstName.lastName2")).thenReturn(false);

    String result = profileGenerationService.generateUniqueUsername("firstName", "lastName");

    assertEquals("firstName.lastName2", result);
  }

  @Test
  void generateRandomPassword_shouldReturn10CharacterAlphanumericString() {
    String password = profileGenerationService.generateRandomPassword();

    assertNotNull(password);
    assertEquals(10, password.length());
    assertTrue(password.matches("[a-zA-Z0-9]+"),
        "Password should only contain alphanumeric or numeric characters");
  }
}
