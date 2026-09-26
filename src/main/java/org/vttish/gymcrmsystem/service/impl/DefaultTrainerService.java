package org.vttish.gymcrmsystem.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.vttish.gymcrmsystem.dto.CreateTrainerDto;
import org.vttish.gymcrmsystem.dto.TrainerCardDto;
import org.vttish.gymcrmsystem.dto.TrainerDetailsDto;
import org.vttish.gymcrmsystem.dto.UpdateTrainerDto;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.mapper.TrainerMapper;
import org.vttish.gymcrmsystem.persistence.dao.TrainerDao;
import org.vttish.gymcrmsystem.service.ProfileGenerationService;
import org.vttish.gymcrmsystem.service.TrainerService;

@Service
public class DefaultTrainerService implements TrainerService {

  @Autowired
  private TrainerDao trainerDao;

  private ProfileGenerationService profileGenerationService;
  private TrainerMapper mapper;

  @Autowired
  public void setProfileGenerationService(ProfileGenerationService profileGenerationService) {
    this.profileGenerationService = profileGenerationService;
  }

  @Autowired
  public void setMapper(TrainerMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public TrainerDetailsDto create(CreateTrainerDto createTrainerDto) {
    Assert.notNull(createTrainerDto, "The create DTO must not be null");
    Assert.noNullElements(
        List.of(
            createTrainerDto.getFirstName(),
            createTrainerDto.getLastName(),
            createTrainerDto.getSpecialization()
        ),
        "One or more fields are null"
    );
    Assert.isTrue(!createTrainerDto.getFirstName().isBlank(), "First name must not be blank");
    Assert.isTrue(!createTrainerDto.getLastName().isBlank(), "Last name must not be blank");

    String username = profileGenerationService.generateUniqueUsername(
        createTrainerDto.getFirstName(), createTrainerDto.getLastName()
    );
    String password = profileGenerationService.generateRandomPassword();
    Trainer trainer = mapper.create(createTrainerDto);

    trainer.setUsername(username);
    trainer.setPassword(password);

    return mapper.toTrainerDetailsDto(trainerDao.create(trainer));
  }

  @Override
  public TrainerDetailsDto getById(UUID id) {
    return mapper.toTrainerDetailsDto(getEntityById(id));
  }

  private Trainer getEntityById(UUID id) {
    Assert.notNull(id, "Trainer ID must not be null");

    return trainerDao.selectById(id).orElseThrow(
        () -> new IllegalArgumentException("Trainer ID does not exist")
    );
  }

  @Override
  public List<TrainerCardDto> getAll() {
    return trainerDao.selectAll().stream()
        .map(mapper::toTrainerCardDto).toList();
  }

  @Override
  public TrainerDetailsDto update(UpdateTrainerDto updateTrainerDto) {
    Assert.notNull(updateTrainerDto, "The update DTO must not be null");
    Assert.noNullElements(
        List.of(
            updateTrainerDto.getFirstName(),
            updateTrainerDto.getLastName(),
            updateTrainerDto.getUsername(),
            updateTrainerDto.getPassword(),
            updateTrainerDto.getUserId(),
            updateTrainerDto.getSpecialization()
        ),
        "One or more fields are null"
    );
    Assert.isTrue(!updateTrainerDto.getFirstName().isBlank(), "First name must not be blank");
    Assert.isTrue(!updateTrainerDto.getLastName().isBlank(), "Last name must not be blank");
    Assert.isTrue(!updateTrainerDto.getUsername().isBlank(), "Username must not be blank");
    Assert.isTrue(!updateTrainerDto.getPassword().isBlank(), "Password must not be blank");
    Assert.isTrue(
        updateTrainerDto.getPassword().length() >= 10,
        "Password must be at least 10 characters long"
    );
    Assert.isTrue(
        profileGenerationService.existsByUsername(updateTrainerDto.getUsername()),
        "Username already exists"
    );

    Trainer trainer = getEntityById(updateTrainerDto.getUserId());

    mapper.update(trainer, updateTrainerDto);
    return mapper.toTrainerDetailsDto(trainer);
  }
}
