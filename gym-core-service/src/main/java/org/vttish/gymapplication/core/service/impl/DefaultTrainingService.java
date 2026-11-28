package org.vttish.gymapplication.core.service.impl;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
import org.vttish.gymapplication.core.event.TrainingCreatedEvent;
import org.vttish.gymapplication.core.event.TrainingDeletedEvent;
import org.vttish.gymapplication.core.exception.ResourceNotFoundException;
import org.vttish.gymapplication.core.logging.Loggable;
import org.vttish.gymapplication.core.mapper.TrainingMapper;
import org.vttish.gymapplication.core.mapper.TrainingTypeMapper;
import org.vttish.gymapplication.core.repository.TraineeRepository;
import org.vttish.gymapplication.core.repository.TrainerRepository;
import org.vttish.gymapplication.core.repository.TrainingRepository;
import org.vttish.gymapplication.core.repository.TrainingTypeRepository;
import org.vttish.gymapplication.core.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.core.repository.filter.TrainerTrainingsFilter;
import org.vttish.gymapplication.core.service.TrainingService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Loggable
public class DefaultTrainingService implements TrainingService {
  private final TrainingRepository trainingRepository;
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  private final TrainingTypeRepository trainingTypeRepository;
  private final TrainingTypeMapper trainingTypeMapper;
  private final TrainingMapper trainingMapper;
  private final ApplicationEventPublisher eventPublisher;
  private final GymMetrics gymMetrics;

  @Override
  @Transactional
  public TrainingDetailsResponseDto create(CreateTrainingRequestDto request) {
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
    eventPublisher.publishEvent(TrainingCreatedEvent.from(trainer, training));

    return trainingMapper.toTrainingDetailsResponseDto(training);
  }

  @Override
  @Transactional
  public void delete(UUID id, String trainerUsername) {
    Training training = trainingRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Training not found with id: " + id));

    Trainer trainer = training.getTrainer();

    if (!Objects.equals(trainer.getUser().getUsername(), trainerUsername)) {
      throw new ResourceNotFoundException("Training not found with id: " + id);
    }

    trainingRepository.delete(training);
    eventPublisher.publishEvent(TrainingDeletedEvent.from(trainer, training));
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
