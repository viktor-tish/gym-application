package org.vttish.gymapplication.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.mapper.TraineeMapper;
import org.vttish.gymapplication.mapper.TrainerItemMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.service.ProfileGenerationService;
import org.vttish.gymapplication.service.TraineeService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultTraineeService implements TraineeService {
  private final TraineeRepository traineeRepository;
  private final TrainerRepository trainerRepository;
  private final ProfileGenerationService profileGenerationService;
  private final TraineeMapper traineeMapper;
  private final TrainerItemMapper trainerItemMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public CredentialsResponseDto register(RegisterTraineeRequestDto request) {
    Trainee trainee = traineeMapper.create(request);

    String username = profileGenerationService.generateUniqueUsername(
        request.firstName(), request.lastName()
    );
    String password = profileGenerationService.generateRandomPassword();

    trainee.getUser().setUsername(username);
    trainee.getUser().setPassword(passwordEncoder.encode(password));
    trainee.getUser().setIsActive(true);

    traineeRepository.save(trainee);

    return new CredentialsResponseDto(username, password);
  }

  @Override
  public TraineeProfileResponseDto getProfile(String username) {
    Trainee trainee = findTraineeByUsername(username);

    return traineeMapper.toTraineeProfileResponseDto(trainee);
  }

  @Override
  @Transactional
  public UpdateTraineeProfileResponseDto updateProfile(
      String username,
      UpdateTraineeProfileRequestDto request
  ) {
    Trainee trainee = findTraineeByUsername(username);
    traineeMapper.update(trainee, request);

    return traineeMapper.toUpdateTraineeProfileResponseDto(trainee);
  }

  @Override
  @Transactional
  public void delete(String username) {
    traineeRepository.deleteByUsername(username);
  }

  @Override
  @Transactional
  public List<TrainerItemResponseDto> updateTrainers(
      String username,
      UpdateTraineeTrainersRequestDto request
  ) {
    Trainee trainee = findTraineeByUsername(username);

    List<String> requestedUsernames = request.trainers();
    List<Trainer> foundTrainers = trainerRepository.findAllByUsernameIn(requestedUsernames);
    Set<String> requestedUsernamesSet = new HashSet<>(request.trainers());

    if (foundTrainers.size() != requestedUsernamesSet.size()) {
      Set<String> foundUsernames = foundTrainers.stream()
          .map(trainer -> trainer.getUser().getUsername())
          .collect(Collectors.toSet());

      String missingUsername = requestedUsernamesSet.stream()
          .filter(trainerUsername -> !foundUsernames.contains(trainerUsername))
          .findFirst()
          .orElse("unknown");

      throw new ResourceNotFoundException(
          "Trainer not found with username: " + missingUsername
      );
    }

    Set<Trainer> newTrainers = new HashSet<>(foundTrainers);
    trainee.setTrainers(newTrainers);

    return trainee.getTrainers().stream()
        .map(trainerItemMapper::toTrainerItemResponseDto)
        .toList();
  }

  private Trainee findTraineeByUsername(String username) {
    return traineeRepository.findByUsername(username).orElseThrow(
        () -> new ResourceNotFoundException("Trainee not found with username: " + username)
    );
  }
}
