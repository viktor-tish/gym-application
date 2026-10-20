package org.vttish.gymapplication.service.impl;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.vttish.gymapplication.dto.auth.CredentialsDto;
import org.vttish.gymapplication.dto.trainer.CreateTrainerDto;
import org.vttish.gymapplication.dto.trainer.TrainerCardDto;
import org.vttish.gymapplication.dto.trainer.TrainerDetailsDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerDto;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.TrainingType;
import org.vttish.gymapplication.mapper.TrainerMapper;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.repository.TrainingTypeRepository;
import org.vttish.gymapplication.service.ProfileGenerationService;
import org.vttish.gymapplication.service.TrainerService;
import org.vttish.gymapplication.utils.PasswordHasher;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class DefaultTrainerService implements TrainerService {
  private final TrainerRepository trainerRepository;
  private final TrainingTypeRepository trainingTypeRepository;
  private final ProfileGenerationService profileGenerationService;
  private final TrainerMapper trainerMapper;
  private final PasswordHasher passwordHasher;

  @Override
  @Transactional
  public CredentialsDto create(CreateTrainerDto createTrainerDto) {
    Trainer trainer = trainerMapper.create(createTrainerDto);

    String username = profileGenerationService.generateUniqueUsername(
        createTrainerDto.getFirstName(), createTrainerDto.getLastName()
    );
    String password = profileGenerationService.generateRandomPassword();

    trainer.getUser().setUsername(username);
    trainer.getUser().setPassword(passwordHasher.hash(password));
    trainer.getUser().setIsActive(true);

    TrainingType specialization = findTrainingTypeById(
        createTrainerDto.getSpecializationId()
    );
    trainer.setSpecialization(specialization);

    trainerRepository.save(trainer);

    return new CredentialsDto(username, password);
  }

  @Override
  public TrainerDetailsDto getByUsername(String username) {
    Trainer trainer = findTrainerByUsername(username);

    return trainerMapper.toTrainerDetailsDto(trainer);
  }

  @Override
  @Transactional
  public TrainerDetailsDto update(String username, UpdateTrainerDto updateTrainerDto) {
    Trainer trainer = findTrainerByUsername(username);

    trainerMapper.update(trainer, updateTrainerDto);

    TrainingType specialization = findTrainingTypeById(
        updateTrainerDto.getSpecializationId()
    );
    trainer.setSpecialization(specialization);

    return trainerMapper.toTrainerDetailsDto(trainer);
  }

  @Override
  public List<TrainerCardDto> getUnassigned(String traineeUsername) {
    return trainerRepository.findTrainersNotAssignedToTrainee(traineeUsername).stream()
        .map(trainerMapper::toTrainerCardDto)
        .toList();
  }

  private Trainer findTrainerByUsername(String username) {
    return trainerRepository.findByUsername(username)
        .orElseThrow(() -> new EntityNotFoundException("Trainer not found"));
  }

  private TrainingType findTrainingTypeById(UUID id) {
    return trainingTypeRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException("Specialization not found"));
  }
}
