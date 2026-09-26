package org.vttish.gymapplication.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.TraineeCardDto;
import org.vttish.gymapplication.dto.TraineeDetailsDto;
import org.vttish.gymapplication.dto.UpdateTraineeDto;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.mapper.TraineeMapper;
import org.vttish.gymapplication.persistence.dao.TraineeDao;
import org.vttish.gymapplication.service.ProfileGenerationService;
import org.vttish.gymapplication.service.TraineeService;

@Service
public class DefaultTraineeService implements TraineeService {

  @Autowired
  private TraineeDao traineeDao;

  private ProfileGenerationService profileGenerationService;
  private TraineeMapper mapper;

  @Autowired
  public void setProfileGenerationService(ProfileGenerationService profileGenerationService) {
    this.profileGenerationService = profileGenerationService;
  }

  @Autowired
  public void setMapper(TraineeMapper mapper) {
    this.mapper = mapper;
  }

  @Override
  public TraineeDetailsDto create(CreateTraineeDto createTraineeDto) {
    Assert.notNull(createTraineeDto, "The create DTO must not be null");
    Assert.noNullElements(
        List.of(
            createTraineeDto.getFirstName(),
            createTraineeDto.getLastName(),
            createTraineeDto.getAddress(),
            createTraineeDto.getDateOfBirth()
        ),
        "One or more fields are null"
    );
    Assert.isTrue(!createTraineeDto.getFirstName().isBlank(), "First name must not be blank");
    Assert.isTrue(!createTraineeDto.getLastName().isBlank(), "Last name must not be blank");
    Assert.isTrue(!createTraineeDto.getAddress().isBlank(), "Address must not be blank");
    Assert.isTrue(
        createTraineeDto.getDateOfBirth().isBefore(LocalDate.now()),
        "Date of birth must be in the past"
    );

    String username = profileGenerationService.generateUniqueUsername(
        createTraineeDto.getFirstName(), createTraineeDto.getLastName()
    );
    String password = profileGenerationService.generateRandomPassword();
    Trainee trainee = mapper.create(createTraineeDto);

    trainee.setUsername(username);
    trainee.setPassword(password);

    return mapper.toTraineeDetailsDto(traineeDao.create(trainee));
  }

  @Override
  public TraineeDetailsDto getById(UUID id) {
    return mapper.toTraineeDetailsDto(getEntityById(id));
  }

  private Trainee getEntityById(UUID id) {
    Assert.notNull(id, "Trainee ID must not be null");

    return traineeDao.selectById(id).orElseThrow(
        () -> new IllegalArgumentException("Trainee ID does not exist")
    );
  }

  @Override
  public List<TraineeCardDto> getAll() {
    return traineeDao.selectAll().stream()
        .map(mapper::toTraineeCardDto).toList();
  }

  @Override
  public TraineeDetailsDto update(UpdateTraineeDto updateTraineeDto) {
    Assert.notNull(updateTraineeDto, "The update DTO must not be null");
    Assert.noNullElements(
        List.of(
            updateTraineeDto.getFirstName(),
            updateTraineeDto.getLastName(),
            updateTraineeDto.getUsername(),
            updateTraineeDto.getPassword(),
            updateTraineeDto.getAddress(),
            updateTraineeDto.getDateOfBirth(),
            updateTraineeDto.getUserId()
        ),
        "One or more fields are null"
    );
    Assert.isTrue(!updateTraineeDto.getFirstName().isBlank(), "First name must not be blank");
    Assert.isTrue(!updateTraineeDto.getLastName().isBlank(), "Last name must not be blank");
    Assert.isTrue(!updateTraineeDto.getUsername().isBlank(), "Username must not be blank");
    Assert.isTrue(!updateTraineeDto.getPassword().isBlank(), "Password must not be blank");
    Assert.isTrue(
        updateTraineeDto.getPassword().length() >= 10,
        "Password must be at least 10 characters long"
    );
    Assert.isTrue(!updateTraineeDto.getAddress().isBlank(), "Address must not be blank");
    Assert.isTrue(
        updateTraineeDto.getDateOfBirth().isBefore(LocalDate.now()),
        "Date of birth must be in the past"
    );
    Assert.isTrue(
        profileGenerationService.existsByUsername(updateTraineeDto.getUsername()),
        "Username already exists"
    );

    Trainee trainee = getEntityById(updateTraineeDto.getUserId());

    mapper.update(trainee, updateTraineeDto);
    return mapper.toTraineeDetailsDto(trainee);
  }

  @Override
  public void delete(UUID id) {
    Assert.notNull(id, "Trainee ID must not be null");

    traineeDao.delete(id);
  }
}
