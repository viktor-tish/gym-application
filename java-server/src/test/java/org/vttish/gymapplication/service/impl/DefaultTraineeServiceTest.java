package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.dto.TraineeDetailsDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.UpdateTraineeDto;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.mapper.TraineeMapper;
import org.vttish.gymapplication.mapper.TrainerMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.service.ProfileGenerationService;
import org.vttish.gymapplication.utils.PasswordHasher;

@ExtendWith(MockitoExtension.class)
class DefaultTraineeServiceTest {

  @Mock
  private TraineeRepository traineeRepository;

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private ProfileGenerationService profileGenerationService;

  @Mock
  private TraineeMapper traineeMapper;

  @Mock
  private TrainerMapper trainerMapper;

  @Mock
  private PasswordHasher passwordHasher;

  @InjectMocks
  private DefaultTraineeService traineeService;

  private Trainee testTrainee;

  @BeforeEach
  void setUp() {
    User testUser = new User();
    testUser.setUsername("firstName.lastName");
    testUser.setIsActive(true);

    testTrainee = new Trainee();
    testTrainee.setUser(testUser);
    testTrainee.setTrainers(new HashSet<>());
  }

  @Test
  void create_ShouldGenerateCredentialsAndSave() {
    CreateTraineeDto dto = new CreateTraineeDto();
    dto.setFirstName("firstName");
    dto.setLastName("lastName");

    when(traineeMapper.create(dto)).thenReturn(testTrainee);
    when(profileGenerationService.generateUniqueUsername("firstName", "lastName"))
        .thenReturn("firstName.lastName");
    when(profileGenerationService.generateRandomPassword()).thenReturn("plainPassword");
    when(passwordHasher.hash("plainPassword")).thenReturn("hashedPassword");

    CredentialsDto result = traineeService.create(dto);

    assertEquals("firstName.lastName", result.getUsername());
    assertEquals("plainPassword", result.getPassword());
    assertEquals("hashedPassword", testTrainee.getUser().getPassword());
    verify(traineeRepository).save(testTrainee);
  }

  @Test
  void getByUsername_ShouldReturnDetailsDto_WhenTraineeExists() {
    TraineeDetailsDto detailsDto = new TraineeDetailsDto();

    when(traineeRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainee));
    when(traineeMapper.toTraineeDetailsDto(testTrainee)).thenReturn(detailsDto);

    TraineeDetailsDto result = traineeService.getByUsername("firstName.lastName");

    assertNotNull(result);
  }

  @Test
  void getByUsername_ShouldThrowException_WhenTraineeNotFound() {
    when(traineeRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class, () -> traineeService.getByUsername("nonexistent.username"));
  }

  @Test
  void update_ShouldUpdateAndReturnDetails_WhenTraineeExists() {
    UpdateTraineeDto dto = new UpdateTraineeDto();
    TraineeDetailsDto detailsDto = new TraineeDetailsDto();

    when(traineeRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainee));
    when(traineeMapper.toTraineeDetailsDto(testTrainee)).thenReturn(detailsDto);

    TraineeDetailsDto result = traineeService.update("firstName.lastName", dto);

    assertNotNull(result);
    verify(traineeMapper).update(testTrainee, dto);
  }

  @Test
  void deleteByUsername_ShouldCallRepositoryDelete() {
    traineeService.deleteByUsername("firstName.lastName");
    verify(traineeRepository).deleteByUsername("firstName.lastName");
  }

  @Test
  void addTrainer_ShouldAddTrainer_WhenValid() {
    Trainer trainer = new Trainer();
    trainer.setTrainees(new HashSet<>());

    TrainerCardDto cardDto = new TrainerCardDto();

    when(traineeRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainee));
    when(trainerRepository.findByUsername("firstName.lastName1"))
        .thenReturn(Optional.of(trainer));
    when(trainerMapper.toTrainerCardDto(trainer)).thenReturn(cardDto);

    List<TrainerCardDto> result = traineeService.addTrainer(
        "firstName.lastName", "firstName.lastName1");

    assertEquals(1, result.size());
    assertTrue(testTrainee.getTrainers().contains(trainer));
  }

  @Test
  void removeTrainer_ShouldRemoveTrainer_WhenValid() {
    Trainer trainer = new Trainer();
    trainer.setTrainees(new HashSet<>());
    testTrainee.addTrainer(trainer);

    when(traineeRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainee));
    when(trainerRepository.findByUsername("firstName.lastName1"))
        .thenReturn(Optional.of(trainer));

    List<TrainerCardDto> result = traineeService.removeTrainer(
        "firstName.lastName", "firstName.lastName1"
    );

    assertEquals(0, result.size());
    assertFalse(testTrainee.getTrainers().contains(trainer));
  }
}
