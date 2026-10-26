package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.vttish.gymapplication.actuator.metrics.GymMetrics;
import org.vttish.gymapplication.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.entity.TrainingType;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.mapper.TrainingMapper;
import org.vttish.gymapplication.mapper.TrainingTypeMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingRepository;
import org.vttish.gymapplication.repository.TrainingTypeRepository;
import org.vttish.gymapplication.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.repository.filter.TrainerTrainingsFilter;

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
  private GymMetrics gymMetrics;

  @InjectMocks
  private DefaultTrainingService trainingService;

  @Test
  void create_ShouldSaveTraining_WhenValidRequest() {
    Trainer trainer = new Trainer();
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
        "Cardio", LocalDate.of(2025, 1, 1), null, 60, "trainer.username"
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
        "Cardio", LocalDate.of(2025, 1, 1), null, 60, "trainee.username"
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
