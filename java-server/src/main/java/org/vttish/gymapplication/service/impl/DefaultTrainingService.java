package org.vttish.gymapplication.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.vttish.gymapplication.dto.training.CreateTrainingDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingFilterDto;
import org.vttish.gymapplication.dto.training.TrainingDetailsDto;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.mapper.TrainingMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingRepository;
import org.vttish.gymapplication.service.TrainingService;

@Service
@Validated
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DefaultTrainingService implements TrainingService {
  private final TrainingRepository trainingRepository;
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  private final TrainingMapper trainingMapper;

  @Override
  @Transactional
  public void create(CreateTrainingDto createTrainingDto) {
    Trainee trainee = traineeRepository.findByUsername(createTrainingDto.getTraineeUsername())
        .orElseThrow(() -> new IllegalArgumentException("Trainee not found"));

    Trainer trainer = trainerRepository.findByUsername(createTrainingDto.getTrainerUsername())
        .orElseThrow(() -> new IllegalArgumentException("Trainer not found"));

    Training training = trainingMapper.create(createTrainingDto);
    training.setTrainee(trainee);
    training.setTrainer(trainer);
    training.setType(trainer.getSpecialization());

    trainingRepository.save(training);
  }

  @Override
  public List<TrainingDetailsDto> getTraineeTrainings(
      String traineeUsername, TraineeTrainingFilterDto filter
  ) {
    return trainingRepository.findTraineeTrainingsByFilter(traineeUsername, filter).stream()
        .map(trainingMapper::toTrainingDetailsDto)
        .toList();
  }

  @Override
  public List<TrainingDetailsDto> getTrainerTrainings(
      String trainerUsername, TrainerTrainingFilterDto filter
  ) {
    return trainingRepository.findTrainerTrainingsByFilter(trainerUsername, filter).stream()
        .map(trainingMapper::toTrainingDetailsDto)
        .toList();
  }
}
