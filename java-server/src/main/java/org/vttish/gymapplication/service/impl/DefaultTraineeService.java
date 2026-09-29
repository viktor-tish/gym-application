package org.vttish.gymapplication.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.CredentialsDto;
import org.vttish.gymapplication.dto.TraineeDetailsDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.UpdateTraineeDto;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.mapper.TraineeMapper;
import org.vttish.gymapplication.mapper.TrainerMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.service.ProfileGenerationService;
import org.vttish.gymapplication.service.TraineeService;
import org.vttish.gymapplication.utils.PasswordHasher;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultTraineeService implements TraineeService {
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  private final ProfileGenerationService profileGenerationService;
  private final TraineeMapper traineeMapper;
  private final TrainerMapper trainerMapper;
  private final PasswordHasher passwordHasher;

  @Override
  @Transactional
  public CredentialsDto create(CreateTraineeDto createTraineeDto) {
    Trainee trainee = traineeMapper.create(createTraineeDto);

    String username = profileGenerationService.generateUniqueUsername(
        createTraineeDto.getFirstName(), createTraineeDto.getLastName());
    String password = profileGenerationService.generateRandomPassword();

    trainee.getUser().setUsername(username);
    trainee.getUser().setPassword(passwordHasher.hash(password));
    trainee.getUser().setIsActive(true);

    traineeRepository.save(trainee);

    return new CredentialsDto(username, password);
  }

  @Override
  public TraineeDetailsDto getByUsername(String username) {
    Trainee trainee = findTraineeByUsername(username);

    return traineeMapper.toTraineeDetailsDto(trainee);
  }

  @Override
  @Transactional
  public TraineeDetailsDto update(String username, UpdateTraineeDto updateTraineeDto) {
    Trainee trainee = findTraineeByUsername(username);
    traineeMapper.update(trainee, updateTraineeDto);

    return traineeMapper.toTraineeDetailsDto(trainee);
  }

  @Override
  @Transactional
  public void deleteByUsername(String username) {
    traineeRepository.deleteByUsername(username);
  }

  @Override
  @Transactional
  public List<TrainerCardDto> updateTrainersList(String username, List<String> trainerUsernames) {
    Trainee trainee = findTraineeByUsername(username);

    List<Trainer> trainers = trainerUsernames.stream()
        .map(trainerUsername -> trainerRepository.findByUsername(trainerUsername)
            .orElseThrow(() -> new EntityNotFoundException(
                "Trainer not found: " + trainerUsername)))
        .toList();

    trainee.getTrainers().forEach(trainer -> trainer.getTrainees().remove(trainee));
    trainee.getTrainers().clear();

    for (Trainer trainer : trainers) {
      trainee.addTrainer(trainer);
    }

    return trainee.getTrainers().stream()
        .map(trainerMapper::toTrainerCardDto)
        .toList();
  }

  private Trainee findTraineeByUsername(String username) {
    return traineeRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("Trainee not found"));
  }
}
