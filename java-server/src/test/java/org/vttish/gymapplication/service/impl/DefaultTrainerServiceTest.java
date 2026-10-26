package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileResponseDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.TrainingType;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.mapper.TrainerItemMapper;
import org.vttish.gymapplication.mapper.TrainerMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingTypeRepository;
import org.vttish.gymapplication.security.PasswordHasher;
import org.vttish.gymapplication.service.ProfileGenerationService;

@ExtendWith(MockitoExtension.class)
class DefaultTrainerServiceTest {

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private TraineeRepository traineeRepository;

  @Mock
  private TrainingTypeRepository trainingTypeRepository;

  @Mock
  private ProfileGenerationService profileGenerationService;

  @Mock
  private TrainerMapper trainerMapper;

  @Mock
  private TrainerItemMapper trainerItemMapper;

  @Mock
  private PasswordHasher passwordHasher;

  @InjectMocks
  private DefaultTrainerService trainerService;

  private Trainer testTrainer;
  private TrainingType testSpecialization;
  private UUID specializationId;

  @BeforeEach
  void setUp() {
    specializationId = UUID.randomUUID();

    User testUser = new User();
    testUser.setUsername("firstName.lastName");
    testUser.setIsActive(true);

    testSpecialization = new TrainingType();
    ReflectionTestUtils.setField(testSpecialization, "id", specializationId);
    testSpecialization.setName("Fitness");

    testTrainer = new Trainer();
    testTrainer.setUser(testUser);
    testTrainer.setSpecialization(testSpecialization);
  }

  @Test
  void register_ShouldGenerateCredentialsAndSave() {
    RegisterTrainerRequestDto request = new RegisterTrainerRequestDto(
        "firstName", "lastName", specializationId
    );

    when(trainerMapper.create(request)).thenReturn(testTrainer);
    when(profileGenerationService.generateUniqueUsername("firstName", "lastName"))
        .thenReturn("firstName.lastName");
    when(profileGenerationService.generateRandomPassword()).thenReturn("plainPassword");
    when(passwordHasher.hash("plainPassword")).thenReturn("hashedPassword");
    when(trainingTypeRepository.findById(specializationId))
        .thenReturn(Optional.of(testSpecialization));

    CredentialsResponseDto result = trainerService.register(request);

    assertEquals("firstName.lastName", result.username());
    assertEquals("plainPassword", result.password());
    assertEquals("hashedPassword", testTrainer.getUser().getPassword());
    verify(trainerRepository).save(testTrainer);
  }

  @Test
  void register_ShouldThrowException_WhenSpecializationNotFound() {
    UUID invalidUuid = UUID.randomUUID();
    RegisterTrainerRequestDto request = new RegisterTrainerRequestDto(
        "firstName", "lastName", invalidUuid
    );

    when(trainerMapper.create(request)).thenReturn(testTrainer);
    when(profileGenerationService.generateUniqueUsername("firstName", "lastName"))
        .thenReturn("firstName.lastName");
    when(profileGenerationService.generateRandomPassword()).thenReturn("plainPassword");
    when(passwordHasher.hash("plainPassword")).thenReturn("hashedPassword");
    when(trainingTypeRepository.findById(invalidUuid)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> trainerService.register(request)
    );
  }

  @Test
  void getProfile_ShouldReturnProfile_WhenTrainerExists() {
    TrainingTypeResponseDto specDto = new TrainingTypeResponseDto(specializationId, "Fitness");
    TrainerProfileResponseDto profileDto = new TrainerProfileResponseDto(
        "firstName", "lastName", specDto, true, List.of()
    );

    when(trainerRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainer));
    when(trainerMapper.toTrainerProfileResponseDto(testTrainer)).thenReturn(profileDto);

    TrainerProfileResponseDto result = trainerService.getProfile("firstName.lastName");

    assertNotNull(result);
    assertEquals("firstName", result.firstName());
  }

  @Test
  void getProfile_ShouldThrowException_WhenTrainerNotFound() {
    when(trainerRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> trainerService.getProfile("nonexistent.username")
    );
  }

  @Test
  void updateProfile_ShouldUpdateAndReturnProfile_WhenTrainerExists() {
    UpdateTrainerProfileRequestDto request = new UpdateTrainerProfileRequestDto(
        "firstName", "lastName", true
    );
    TrainingTypeResponseDto specDto = new TrainingTypeResponseDto(specializationId, "Fitness");
    UpdateTrainerProfileResponseDto responseDto = new UpdateTrainerProfileResponseDto(
        "firstName.lastName", "firstName", "lastName", specDto, true, List.of()
    );

    when(trainerRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainer));
    when(trainerMapper.toUpdateTrainerProfileResponseDto(testTrainer)).thenReturn(responseDto);

    UpdateTrainerProfileResponseDto result = trainerService.updateProfile(
        "firstName.lastName", request
    );

    assertNotNull(result);
    assertEquals("firstName.lastName", result.username());
    verify(trainerMapper).update(testTrainer, request);
  }

  @Test
  void updateProfile_ShouldThrowException_WhenTrainerNotFound() {
    UpdateTrainerProfileRequestDto request = new UpdateTrainerProfileRequestDto(
        "firstName", "lastName", true
    );

    when(trainerRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainerService.updateProfile("nonexistent.username", request)
    );
  }

  @Test
  void getUnassignedActiveTrainers_ShouldReturnTrainers_WhenTraineeExists() {
    TrainerItemResponseDto itemDto = new TrainerItemResponseDto(
        "firstName.lastName", "firstName", "lastName", null
    );

    when(traineeRepository.existsByUsername("trainee.username")).thenReturn(true);
    when(trainerRepository.findTrainersNotAssignedToTrainee("trainee.username"))
        .thenReturn(List.of(testTrainer));
    when(trainerItemMapper.toTrainerItemResponseDto(testTrainer)).thenReturn(itemDto);

    List<TrainerItemResponseDto> result =
        trainerService.getUnassignedActiveTrainers("trainee.username");

    assertEquals(1, result.size());
    assertEquals("firstName.lastName", result.get(0).username());
  }

  @Test
  void getUnassignedActiveTrainers_ShouldThrowException_WhenTraineeNotFound() {
    when(traineeRepository.existsByUsername("nonexistent.trainee")).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainerService.getUnassignedActiveTrainers("nonexistent.trainee")
    );
  }
}
