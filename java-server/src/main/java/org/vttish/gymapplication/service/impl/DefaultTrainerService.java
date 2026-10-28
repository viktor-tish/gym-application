package org.vttish.gymapplication.service.impl;

import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileResponseDto;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.TrainingType;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.mapper.TrainerItemMapper;
import org.vttish.gymapplication.mapper.TrainerMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingTypeRepository;
import org.vttish.gymapplication.service.ProfileGenerationService;
import org.vttish.gymapplication.service.TrainerService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultTrainerService implements TrainerService {
  private final TrainerRepository trainerRepository;
  private final TraineeRepository traineeRepository;
  private final TrainingTypeRepository trainingTypeRepository;
  private final ProfileGenerationService profileGenerationService;
  private final TrainerMapper trainerMapper;
  private final TrainerItemMapper trainerItemMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public CredentialsResponseDto register(RegisterTrainerRequestDto request) {
    Trainer trainer = trainerMapper.create(request);

    String username = profileGenerationService.generateUniqueUsername(
        request.firstName(), request.lastName()
    );
    String password = profileGenerationService.generateRandomPassword();

    trainer.getUser().setUsername(username);
    trainer.getUser().setPassword(passwordEncoder.encode(password));
    trainer.getUser().setIsActive(true);

    TrainingType specialization = findTrainingTypeById(request.specializationId());
    trainer.setSpecialization(specialization);

    trainerRepository.save(trainer);

    return new CredentialsResponseDto(username, password);
  }

  @Override
  public TrainerProfileResponseDto getProfile(String username) {
    Trainer trainer = findTrainerByUsername(username);

    return trainerMapper.toTrainerProfileResponseDto(trainer);
  }

  @Override
  @Transactional
  public UpdateTrainerProfileResponseDto updateProfile(
      String username,
      UpdateTrainerProfileRequestDto request
  ) {
    Trainer trainer = findTrainerByUsername(username);
    trainerMapper.update(trainer, request);

    return trainerMapper.toUpdateTrainerProfileResponseDto(trainer);
  }

  @Override
  public List<TrainerItemResponseDto> getUnassignedActiveTrainers(String traineeUsername) {
    if (!traineeRepository.existsByUsername(traineeUsername)) {
      throw new ResourceNotFoundException("Trainee not found with username: " + traineeUsername);
    }

    return trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername).stream()
        .map(trainerItemMapper::toTrainerItemResponseDto)
        .toList();
  }

  private Trainer findTrainerByUsername(String username) {
    return trainerRepository.findByUsername(username).orElseThrow(
        () -> new ResourceNotFoundException("Trainer not found with username: " + username)
    );
  }

  private TrainingType findTrainingTypeById(UUID id) {
    return trainingTypeRepository.findById(id).orElseThrow(
        () -> new ResourceNotFoundException("Specialization not found")
    );
  }
}
