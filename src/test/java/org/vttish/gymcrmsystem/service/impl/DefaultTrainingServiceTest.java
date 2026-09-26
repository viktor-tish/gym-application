package org.vttish.gymcrmsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymcrmsystem.dto.CreateTrainingDto;
import org.vttish.gymcrmsystem.dto.TrainingCardDto;
import org.vttish.gymcrmsystem.dto.TrainingDetailsDto;
import org.vttish.gymcrmsystem.entity.Training;
import org.vttish.gymcrmsystem.entity.TrainingType;
import org.vttish.gymcrmsystem.mapper.TrainingMapper;
import org.vttish.gymcrmsystem.persistence.dao.TraineeDao;
import org.vttish.gymcrmsystem.persistence.dao.TrainerDao;
import org.vttish.gymcrmsystem.persistence.dao.TrainingDao;

@ExtendWith(MockitoExtension.class)
class DefaultTrainingServiceTest {

  @Mock
  private TrainingDao trainingDao;

  @Mock
  private TrainerDao trainerDao;

  @Mock
  private TraineeDao traineeDao;

  @Mock
  private TrainingMapper mapper;

  @InjectMocks
  private DefaultTrainingService trainingService;

  @Test
  void create_shouldReturnTrainingDetailsDto_whenValidDtoProvided() {
    UUID traineeId = UUID.randomUUID();
    UUID trainerId = UUID.randomUUID();
    CreateTrainingDto dto = new CreateTrainingDto(
        traineeId,
        trainerId,
        "trainingName",
        TrainingType.YOGA,
        LocalDate.of(2000, 1, 1),
        Duration.ofMinutes(60)
    );
    Training training = new Training();
    TrainingDetailsDto expectedResult = new TrainingDetailsDto();

    when(traineeDao.existsById(traineeId)).thenReturn(true);
    when(trainerDao.existsById(trainerId)).thenReturn(true);
    when(mapper.create(dto)).thenReturn(training);
    when(trainingDao.create(training)).thenReturn(training);
    when(mapper.toTrainingDetailsDto(training)).thenReturn(expectedResult);

    TrainingDetailsDto result = trainingService.create(dto);

    assertEquals(expectedResult, result);
    verify(trainingDao).create(training);
  }

  @Test
  void create_shouldThrowException_whenDtoIsNull() {
    assertThrows(IllegalArgumentException.class, () -> trainingService.create(null));
  }

  @Test
  void create_shouldThrowException_whenTraineeDoesNotExist() {
    UUID traineeId = UUID.randomUUID();
    UUID trainerId = UUID.randomUUID();
    CreateTrainingDto dto = new CreateTrainingDto(
        traineeId,
        trainerId,
        "trainingName",
        TrainingType.YOGA,
        LocalDate.of(2000, 1, 1),
        Duration.ofMinutes(60)
    );

    when(traineeDao.existsById(traineeId)).thenReturn(false);

    assertThrows(IllegalArgumentException.class, () -> trainingService.create(dto));
  }

  @Test
  void getById_shouldReturnTrainingDetailsDto_whenIdExists() {
    UUID id = UUID.randomUUID();
    Training training = new Training();
    TrainingDetailsDto expectedResult = new TrainingDetailsDto();

    when(trainingDao.selectById(id)).thenReturn(Optional.of(training));
    when(mapper.toTrainingDetailsDto(training)).thenReturn(expectedResult);

    TrainingDetailsDto result = trainingService.getById(id);

    assertEquals(expectedResult, result);
  }

  @Test
  void getById_shouldThrowException_whenIdDoesNotExist() {
    UUID id = UUID.randomUUID();
    when(trainingDao.selectById(id)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> trainingService.getById(id));
  }

  @Test
  void getAll_shouldReturnListOfTrainingCardDtos() {
    Training training = new Training();
    TrainingCardDto cardDto = new TrainingCardDto();

    when(trainingDao.selectAll()).thenReturn(List.of(training));
    when(mapper.toTrainingCardDto(training)).thenReturn(cardDto);

    List<TrainingCardDto> result = trainingService.getAll();

    assertEquals(1, result.size());
    assertEquals(cardDto, result.get(0));
  }
}
