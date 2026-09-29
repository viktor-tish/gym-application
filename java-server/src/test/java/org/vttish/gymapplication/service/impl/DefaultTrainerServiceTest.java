package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityNotFoundException;
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
import org.vttish.gymapplication.dto.CreateTrainerDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.TrainerDetailsDto;
import org.vttish.gymapplication.dto.UpdateTrainerDto;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.TrainingType;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.mapper.TrainerMapper;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingTypeRepository;
import org.vttish.gymapplication.service.ProfileGenerationService;
import org.vttish.gymapplication.utils.PasswordHasher;

@ExtendWith(MockitoExtension.class)
class DefaultTrainerServiceTest {

  @Mock
  private TrainerRepository trainerRepository;
  
  @Mock
  private TrainingTypeRepository trainingTypeRepository;
  
  @Mock
  private ProfileGenerationService profileGenerationService;
  
  @Mock
  private TrainerMapper trainerMapper;
  
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
  void create_ShouldGenerateCredentialsAndSave() {
    CreateTrainerDto dto = new CreateTrainerDto();
    dto.setFirstName("firstName");
    dto.setLastName("lastName");
    dto.setSpecializationId(specializationId);

    when(trainerMapper.create(dto)).thenReturn(testTrainer);
    when(profileGenerationService.generateUniqueUsername("firstName", "lastName"))
        .thenReturn("firstName.lastName");
    when(profileGenerationService.generateRandomPassword()).thenReturn("plainPassword");
    when(passwordHasher.hash("plainPassword")).thenReturn("hashedPassword");
    when(trainingTypeRepository.findById(specializationId))
        .thenReturn(Optional.of(testSpecialization));

    CredentialsDto result = trainerService.create(dto);

    assertEquals("firstName.lastName", result.getUsername());
    assertEquals("plainPassword", result.getPassword());
    assertEquals("hashedPassword", testTrainer.getUser().getPassword());
    verify(trainerRepository).save(testTrainer);
  }

  @Test
  void create_ShouldThrowException_WhenSpecializationNotFound() {
    UUID invalidUuid = UUID.randomUUID();
    CreateTrainerDto dto = new CreateTrainerDto();
    dto.setFirstName("firstName");
    dto.setLastName("lastName");
    dto.setSpecializationId(invalidUuid);

    when(trainerMapper.create(dto)).thenReturn(testTrainer);
    when(profileGenerationService.generateUniqueUsername("firstName", "lastName"))
        .thenReturn("firstName.lastName");
    when(profileGenerationService.generateRandomPassword()).thenReturn("plainPassword");
    when(passwordHasher.hash("plainPassword")).thenReturn("hashedPassword");
    when(trainingTypeRepository.findById(invalidUuid)).thenReturn(Optional.empty());

    assertThrows(EntityNotFoundException.class, () -> trainerService.create(dto));
  }

  @Test
  void getByUsername_ShouldReturnDetailsDto_WhenTrainerExists() {
    TrainerDetailsDto detailsDto = new TrainerDetailsDto();
    
    when(trainerRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainer));
    when(trainerMapper.toTrainerDetailsDto(testTrainer)).thenReturn(detailsDto);

    TrainerDetailsDto result = trainerService.getByUsername("firstName.lastName");

    assertNotNull(result);
  }

  @Test
  void getByUsername_ShouldThrowException_WhenTrainerNotFound() {
    when(trainerRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        EntityNotFoundException.class, () -> trainerService.getByUsername("nonexistent.username"));
  }

  @Test
  void update_ShouldUpdateAndReturnDetails_WhenTrainerExists() {
    UpdateTrainerDto dto = new UpdateTrainerDto();
    dto.setSpecializationId(specializationId);
    
    TrainerDetailsDto detailsDto = new TrainerDetailsDto();

    when(trainerRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainer));
    when(trainingTypeRepository.findById(specializationId))
        .thenReturn(Optional.of(testSpecialization));
    when(trainerMapper.toTrainerDetailsDto(testTrainer)).thenReturn(detailsDto);

    TrainerDetailsDto result = trainerService.update("firstName.lastName", dto);

    assertNotNull(result);
    verify(trainerMapper).update(testTrainer, dto);
  }

  @Test
  void getUnassigned_ShouldReturnTrainerCards() {
    List<Trainer> unassignedTrainers = List.of(testTrainer);
    TrainerCardDto cardDto = new TrainerCardDto();

    when(trainerRepository.findTrainersNotAssignedToTrainee("trainee.firstName.lastName"))
        .thenReturn(unassignedTrainers);
    when(trainerMapper.toTrainerCardDto(testTrainer)).thenReturn(cardDto);

    List<TrainerCardDto> result = trainerService.getUnassigned("trainee.firstName.lastName");

    assertEquals(1, result.size());
  }
}
