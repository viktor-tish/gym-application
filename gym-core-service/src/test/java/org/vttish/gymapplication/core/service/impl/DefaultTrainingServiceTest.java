package org.vttish.gymapplication.core.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.vttish.gymapplication.core.actuator.metrics.GymMetrics;
import org.vttish.gymapplication.core.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.core.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainingDetailsResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.core.entity.Trainee;
import org.vttish.gymapplication.core.entity.Trainer;
import org.vttish.gymapplication.core.entity.Training;
import org.vttish.gymapplication.core.entity.TrainingType;
import org.vttish.gymapplication.core.entity.User;
import org.vttish.gymapplication.core.event.TrainingCreatedEvent;
import org.vttish.gymapplication.core.event.TrainingDeletedEvent;
import org.vttish.gymapplication.core.exception.ResourceNotFoundException;
import org.vttish.gymapplication.core.mapper.TrainingMapper;
import org.vttish.gymapplication.core.mapper.TrainingTypeMapper;
import org.vttish.gymapplication.core.repository.TraineeRepository;
import org.vttish.gymapplication.core.repository.TrainerRepository;
import org.vttish.gymapplication.core.repository.TrainingRepository;
import org.vttish.gymapplication.core.repository.TrainingTypeRepository;
import org.vttish.gymapplication.core.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.core.repository.filter.TrainerTrainingsFilter;

@ExtendWith(MockitoExtension.class)
class DefaultTrainingServiceTest {

  @Mock
  private TrainingRepository trainingRepository;

  @Mock
  private TraineeRepository traineeRepository;

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private TrainingTypeRepository trainingTypeRepository;

  @Mock
  private TrainingTypeMapper trainingTypeMapper;

  @Mock
  private TrainingMapper trainingMapper;

  @Mock
  private ApplicationEventPublisher eventPublisher;

  @Mock
  private GymMetrics gymMetrics;

  @InjectMocks
  private DefaultTrainingService trainingService;

  @Test
  void create_ShouldSaveTrainingAndPublishEvent_WhenValidRequest() {
    User user = new User();
    user.setUsername("trainer.username");
    user.setFirstName("trainer");
    user.setLastName("username");
    user.setIsActive(true);

    Trainer trainer = new Trainer();
    trainer.setUser(user);
    TrainingType specialization = new TrainingType();
    trainer.setSpecialization(specialization);
    Trainee trainee = new Trainee();
    Training training = new Training();
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "trainer.username", "trainee.username", "Cardio", LocalDate.of(2025, 1, 1), 60
    );

    when(trainerRepository.findByUsername("trainer.username"))
        .thenReturn(Optional.of(trainer));
    when(traineeRepository.findByUsername("trainee.username"))
        .thenReturn(Optional.of(trainee));
    when(trainingMapper.create(request)).thenReturn(training);

    trainingService.create(request);

    verify(trainingRepository).save(training);
    verify(gymMetrics).incrementTrainingCreated();
    verify(eventPublisher).publishEvent(any(TrainingCreatedEvent.class));
    assertEquals(trainer, training.getTrainer());
    assertEquals(trainee, training.getTrainee());
    assertEquals(specialization, training.getType());
  }

  @Test
  void create_ShouldThrowException_WhenTrainerNotFound() {
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "nonexistent.trainer", "trainee.username", "Cardio", LocalDate.of(2025, 1, 1), 60
    );

    when(trainerRepository.findByUsername("nonexistent.trainer"))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> trainingService.create(request)
    );
  }

  @Test
  void create_ShouldThrowException_WhenTraineeNotFound() {
    CreateTrainingRequestDto request = new CreateTrainingRequestDto(
        "trainer.username", "nonexistent.trainee", "Cardio", LocalDate.of(2025, 1, 1), 60
    );

    Trainer trainer = new Trainer();
    when(trainerRepository.findByUsername("trainer.username"))
        .thenReturn(Optional.of(trainer));
    when(traineeRepository.findByUsername("nonexistent.trainee"))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> trainingService.create(request)
    );
  }

  @Test
  void getDetails_ShouldReturnMappedDto_WhenCallerIsTrainer() {
    UUID trainingId = UUID.randomUUID();
    String trainerUsername = "trainer.username";
    String traineeUsername = "trainee.username";

    User trainerUser = new User();
    trainerUser.setUsername(trainerUsername);
    Trainer trainer = new Trainer();
    trainer.setUser(trainerUser);

    User traineeUser = new User();
    traineeUser.setUsername(traineeUsername);
    Trainee trainee = new Trainee();
    trainee.setUser(traineeUser);

    Training training = new Training();
    training.setTrainer(trainer);
    training.setTrainee(trainee);

    TrainingDetailsResponseDto responseDto = mock(TrainingDetailsResponseDto.class);

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
    when(trainingMapper.toTrainingDetailsResponseDto(training)).thenReturn(responseDto);

    TrainingDetailsResponseDto result = trainingService.getDetails(trainingId, trainerUsername);

    assertEquals(responseDto, result);
    verify(trainingRepository).findById(trainingId);
    verify(trainingMapper).toTrainingDetailsResponseDto(training);
  }

  @Test
  void getDetails_ShouldReturnMappedDto_WhenCallerIsTrainee() {
    UUID trainingId = UUID.randomUUID();
    String trainerUsername = "trainer.username";
    String traineeUsername = "trainee.username";

    User trainerUser = new User();
    trainerUser.setUsername(trainerUsername);
    Trainer trainer = new Trainer();
    trainer.setUser(trainerUser);

    User traineeUser = new User();
    traineeUser.setUsername(traineeUsername);
    Trainee trainee = new Trainee();
    trainee.setUser(traineeUser);

    Training training = new Training();
    training.setTrainer(trainer);
    training.setTrainee(trainee);

    TrainingDetailsResponseDto responseDto = mock(TrainingDetailsResponseDto.class);

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));
    when(trainingMapper.toTrainingDetailsResponseDto(training)).thenReturn(responseDto);

    TrainingDetailsResponseDto result = trainingService.getDetails(trainingId, traineeUsername);

    assertEquals(responseDto, result);
    verify(trainingRepository).findById(trainingId);
    verify(trainingMapper).toTrainingDetailsResponseDto(training);
  }

  @Test
  void getDetails_ShouldThrowException_WhenTrainingNotFound() {
    UUID trainingId = UUID.randomUUID();
    String username = "any.user";

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainingService.getDetails(trainingId, username)
    );
  }

  @Test
  void getDetails_ShouldThrowException_WhenCallerIsNotTrainerOrTrainee() {
    UUID trainingId = UUID.randomUUID();
    String trainerUsername = "trainer.username";
    String traineeUsername = "trainee.username";
    String otherUsername = "other.user";

    User trainerUser = new User();
    trainerUser.setUsername(trainerUsername);
    Trainer trainer = new Trainer();
    trainer.setUser(trainerUser);

    User traineeUser = new User();
    traineeUser.setUsername(traineeUsername);
    Trainee trainee = new Trainee();
    trainee.setUser(traineeUser);

    Training training = new Training();
    training.setTrainer(trainer);
    training.setTrainee(trainee);

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainingService.getDetails(trainingId, otherUsername)
    );
  }

  @Test
  void delete_ShouldDeleteTrainingAndPublishEvent_WhenValidRequest() {
    UUID trainingId = UUID.randomUUID();
    String trainerUsername = "trainer.username";

    User user = new User();
    user.setUsername(trainerUsername);

    Trainer trainer = new Trainer();
    trainer.setUser(user);

    Training training = new Training();
    training.setTrainer(trainer);

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

    trainingService.delete(trainingId, trainerUsername);

    verify(trainingRepository).delete(training);
    verify(eventPublisher).publishEvent(any(TrainingDeletedEvent.class));
  }

  @Test
  void delete_ShouldThrowException_WhenTrainingNotFound() {
    UUID trainingId = UUID.randomUUID();
    String trainerUsername = "trainer.username";

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainingService.delete(trainingId, trainerUsername)
    );
  }

  @Test
  void delete_ShouldThrowException_WhenTrainerUsernameDoesNotMatch() {
    UUID trainingId = UUID.randomUUID();
    String trainerUsername = "trainer.username";

    User user = new User();
    user.setUsername("other.trainer");

    Trainer trainer = new Trainer();
    trainer.setUser(user);

    Training training = new Training();
    training.setTrainer(trainer);

    when(trainingRepository.findById(trainingId)).thenReturn(Optional.of(training));

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainingService.delete(trainingId, trainerUsername)
    );
  }

  @Test
  void getTraineeTrainings_ShouldReturnMappedDtos_WhenTraineeExists() {
    String traineeUsername = "trainee.username";
    GetTraineeTrainingsRequestDto request = new GetTraineeTrainingsRequestDto(
        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "trainer.name", null
    );
    TraineeTrainingsFilter filter = new TraineeTrainingsFilter(
        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "trainer.name", null
    );

    Training training = new Training();
    TraineeTrainingItemResponseDto itemResponseDto = new TraineeTrainingItemResponseDto(
        null, "Cardio", LocalDate.of(2025, 1, 1), null, 60, "trainer.username"
    );

    when(traineeRepository.existsByUsername(traineeUsername)).thenReturn(true);
    when(trainingMapper.toTraineeTrainingsFilter(request)).thenReturn(filter);
    when(trainingRepository.findTraineeTrainingsByFilter(traineeUsername, filter))
        .thenReturn(List.of(training));
    when(trainingMapper.toTraineeTrainingItemResponseDto(training)).thenReturn(itemResponseDto);

    List<TraineeTrainingItemResponseDto> result =
        trainingService.getTraineeTrainings(traineeUsername, request);

    assertEquals(1, result.size());
    assertEquals(itemResponseDto, result.get(0));
  }

  @Test
  void getTraineeTrainings_ShouldThrowException_WhenTraineeNotFound() {
    String traineeUsername = "nonexistent.trainee";
    GetTraineeTrainingsRequestDto request = new GetTraineeTrainingsRequestDto(
        null, null, null, null
    );

    when(traineeRepository.existsByUsername(traineeUsername)).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainingService.getTraineeTrainings(traineeUsername, request)
    );
  }

  @Test
  void getTrainerTrainings_ShouldReturnMappedDtos_WhenTrainerExists() {
    String trainerUsername = "trainer.username";
    GetTrainerTrainingsRequestDto request = new GetTrainerTrainingsRequestDto(
        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "trainee.name"
    );
    TrainerTrainingsFilter filter = new TrainerTrainingsFilter(
        LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "trainee.name"
    );

    Training training = new Training();
    TrainerTrainingItemResponseDto itemResponseDto = new TrainerTrainingItemResponseDto(
        null, "Cardio", LocalDate.of(2025, 1, 1), null, 60, "trainee.username"
    );

    when(trainerRepository.existsByUsername(trainerUsername)).thenReturn(true);
    when(trainingMapper.toTrainerTrainingsFilter(request)).thenReturn(filter);
    when(trainingRepository.findTrainerTrainingsByFilter(trainerUsername, filter))
        .thenReturn(List.of(training));
    when(trainingMapper.toTrainerTrainingItemResponseDto(training)).thenReturn(itemResponseDto);

    List<TrainerTrainingItemResponseDto> result =
        trainingService.getTrainerTrainings(trainerUsername, request);

    assertEquals(1, result.size());
    assertEquals(itemResponseDto, result.get(0));
  }

  @Test
  void getTrainerTrainings_ShouldThrowException_WhenTrainerNotFound() {
    String trainerUsername = "nonexistent.trainer";
    GetTrainerTrainingsRequestDto request = new GetTrainerTrainingsRequestDto(
        null, null, null
    );

    when(trainerRepository.existsByUsername(trainerUsername)).thenReturn(false);

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainingService.getTrainerTrainings(trainerUsername, request)
    );
  }

  @Test
  void getTypes_ShouldReturnMappedDtos() {
    TrainingType trainingType = new TrainingType();
    UUID typeId = UUID.randomUUID();
    TrainingTypeResponseDto responseDto = new TrainingTypeResponseDto(typeId, "Fitness");

    when(trainingTypeRepository.findAll()).thenReturn(List.of(trainingType));
    when(trainingTypeMapper.toTrainingTypeResponseDto(trainingType)).thenReturn(responseDto);

    List<TrainingTypeResponseDto> result = trainingService.getTypes();

    assertEquals(1, result.size());
    assertEquals(responseDto, result.get(0));
  }
}
