package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymapplication.dto.training.CreateTrainingDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainingDetailsDto;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.entity.TrainingType;
import org.vttish.gymapplication.mapper.TrainingMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingRepository;

@ExtendWith(MockitoExtension.class)
class DefaultTrainingServiceTest {

  @Mock
  private TrainingRepository trainingRepository;

  @Mock
  private TraineeRepository traineeRepository;

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private TrainingMapper trainingMapper;

  @InjectMocks
  private DefaultTrainingService trainingService;

  @Test
  void create_ShouldSaveTraining_WhenValidDto() {
    CreateTrainingDto dto = new CreateTrainingDto();

    dto.setTraineeUsername("firstName.lastName");
    dto.setTrainerUsername("firstName.lastName1");
    dto.setName("Cardio");
    dto.setDate(LocalDate.of(2000, 1, 1));
    dto.setDuration(60);

    Trainee trainee = new Trainee();
    Trainer trainer = new Trainer();
    trainer.setSpecialization(new TrainingType());

    Training training = new Training();

    when(traineeRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(trainee));
    when(trainerRepository.findByUsername("firstName.lastName1")).thenReturn(Optional.of(trainer));
    when(trainingMapper.create(dto)).thenReturn(training);

    trainingService.create(dto);

    verify(trainingRepository).save(training);
    assertEquals(trainee, training.getTrainee());
    assertEquals(trainer, training.getTrainer());
    assertEquals(trainer.getSpecialization(), training.getType());
  }

  @Test
  void create_ShouldThrowException_WhenTraineeNotFound() {
    CreateTrainingDto dto = new CreateTrainingDto();

    dto.setTraineeUsername("nonexistent.username");

    when(traineeRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> trainingService.create(dto));
  }

  @Test
  void create_ShouldThrowException_WhenTrainerNotFound() {
    CreateTrainingDto dto = new CreateTrainingDto();

    dto.setTraineeUsername("firstName.lastName");
    dto.setTrainerUsername("nonexistent.username");

    Trainee trainee = new Trainee();
    when(traineeRepository.findByUsername("firstName.lastName")).thenReturn(Optional.of(trainee));
    when(trainerRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> trainingService.create(dto));
  }

  @Test
  void getTraineeTrainings_ShouldReturnMappedDtos() {
    TraineeTrainingFilterDto filter = new TraineeTrainingFilterDto();
    Training training = new Training();
    TrainingDetailsDto dto = new TrainingDetailsDto();

    when(trainingRepository.findTraineeTrainingsByFilter("firstName.lastName", filter))
        .thenReturn(List.of(training));
    when(trainingMapper.toTrainingDetailsDto(training)).thenReturn(dto);

    List<TrainingDetailsDto> result = trainingService.getTraineeTrainings(
        "firstName.lastName", filter);

    assertEquals(1, result.size());
    assertEquals(dto, result.get(0));
  }

  @Test
  void getTrainerTrainings_ShouldReturnMappedDtos() {
    TrainerTrainingFilterDto filter = new TrainerTrainingFilterDto();
    Training training = new Training();
    TrainingDetailsDto dto = new TrainingDetailsDto();

    when(trainingRepository.findTrainerTrainingsByFilter("firstName.lastName1", filter))
        .thenReturn(List.of(training));
    when(trainingMapper.toTrainingDetailsDto(training)).thenReturn(dto);

    List<TrainingDetailsDto> result = trainingService.getTrainerTrainings(
        "firstName.lastName1", filter);

    assertEquals(1, result.size());
    assertEquals(dto, result.get(0));
  }
}
