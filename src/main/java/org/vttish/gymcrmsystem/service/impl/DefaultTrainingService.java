package org.vttish.gymcrmsystem.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.vttish.gymcrmsystem.dto.CreateTrainingDto;
import org.vttish.gymcrmsystem.dto.TrainingCardDto;
import org.vttish.gymcrmsystem.dto.TrainingDetailsDto;
import org.vttish.gymcrmsystem.entity.Training;
import org.vttish.gymcrmsystem.mapper.TrainingMapper;
import org.vttish.gymcrmsystem.persistence.dao.TraineeDao;
import org.vttish.gymcrmsystem.persistence.dao.TrainerDao;
import org.vttish.gymcrmsystem.persistence.dao.TrainingDao;
import org.vttish.gymcrmsystem.service.TrainingService;

@Service
public class DefaultTrainingService implements TrainingService {

  @Autowired
  private TrainingDao trainingDao;

  @Autowired
  private TrainerDao trainerDao;

  @Autowired
  private TraineeDao traineeDao;

  private TrainingMapper mapper;

  @Autowired
  public void setMapper(TrainingMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public TrainingDetailsDto create(CreateTrainingDto createTrainingDto) {
    Assert.notNull(createTrainingDto, "The create DTO must not be null");
    Assert.noNullElements(
        List.of(
            createTrainingDto.getTraineeId(),
            createTrainingDto.getTrainerId(),
            createTrainingDto.getTrainingName(),
            createTrainingDto.getTrainingType(),
            createTrainingDto.getTrainingDate(),
            createTrainingDto.getTrainingDuration()
        ),
        "One or more fields are null"
    );
    Assert.isTrue(!createTrainingDto.getTrainingName().isBlank(),
        "Training name must not be blank");

    Assert.isTrue(traineeDao.existsById(createTrainingDto.getTraineeId()),
        "Trainee ID does not exist");
    Assert.isTrue(trainerDao.existsById(createTrainingDto.getTrainerId()),
        "Trainer ID does not exist");

    Training training = mapper.create(createTrainingDto);
    return mapper.toTrainingDetailsDto(trainingDao.create(training));
  }

  @Override
  public TrainingDetailsDto getById(UUID id) {
    return mapper.toTrainingDetailsDto(getEntityById(id));
  }

  private Training getEntityById(UUID id) {
    Assert.notNull(id, "Training ID must not be null");

    return trainingDao.selectById(id).orElseThrow(
        () -> new IllegalArgumentException("Training ID does not exist")
    );
  }

  @Override
  public List<TrainingCardDto> getAll() {
    return trainingDao.selectAll().stream()
        .map(mapper::toTrainingCardDto).toList();
  }
}
