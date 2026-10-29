package org.vttish.gymapplication.service.impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.mapper.TrainingMapper;
import org.vttish.gymapplication.mapper.TrainingTypeMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingRepository;
import org.vttish.gymapplication.repository.TrainingTypeRepository;
import org.vttish.gymapplication.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.repository.filter.TrainerTrainingsFilter;
import org.vttish.gymapplication.service.TrainingService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultTrainingService implements TrainingService {
  private final TrainingRepository trainingRepository;
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  private final TrainingTypeRepository trainingTypeRepository;
  private final TrainingTypeMapper trainingTypeMapper;
  private final TrainingMapper trainingMapper;
  private final GymMetrics gymMetrics;

  @Override
  @Transactional
  public void create(CreateTrainingRequestDto request) {
    Trainer trainer = trainerRepository.findByUsername(request.trainerUsername()).orElseThrow(
        () -> new ResourceNotFoundException(
            "Trainer not found with username: " + request.trainerUsername()
        )
    );
    Trainee trainee = traineeRepository.findByUsername(request.traineeUsername()).orElseThrow(
        () -> new ResourceNotFoundException(
            "Trainee not found with username: " + request.traineeUsername()
        )
    );

    Training training = trainingMapper.create(request);

    training.setTrainer(trainer);
    training.setTrainee(trainee);
    training.setType(trainer.getSpecialization());

    trainingRepository.save(training);

    gymMetrics.incrementTrainingCreated();
  }

  @Override
  public List<TraineeTrainingItemResponseDto> getTraineeTrainings(
      String traineeUsername,
      GetTraineeTrainingsRequestDto request
  ) {
    if (!traineeRepository.existsByUsername(traineeUsername)) {
      throw new ResourceNotFoundException("Trainee not found with username: " + traineeUsername);
    }

    TraineeTrainingsFilter filter = trainingMapper.toTraineeTrainingsFilter(request);

    return trainingRepository.findTraineeTrainingsByFilter(traineeUsername, filter).stream()
        .map(trainingMapper::toTraineeTrainingItemResponseDto)
        .toList();
  }

  @Override
  public List<TrainerTrainingItemResponseDto> getTrainerTrainings(
      String trainerUsername,
      GetTrainerTrainingsRequestDto request
  ) {
    if (!trainerRepository.existsByUsername(trainerUsername)) {
      throw new ResourceNotFoundException("Trainer not found with username: " + trainerUsername);
    }

    TrainerTrainingsFilter filter = trainingMapper.toTrainerTrainingsFilter(request);

    return trainingRepository.findTrainerTrainingsByFilter(trainerUsername, filter).stream()
        .map(trainingMapper::toTrainerTrainingItemResponseDto)
        .toList();
  }

  @Override
  public List<TrainingTypeResponseDto> getTypes() {
    return trainingTypeRepository.findAll().stream()
        .map(trainingTypeMapper::toTrainingTypeResponseDto)
        .toList();
  }
}
