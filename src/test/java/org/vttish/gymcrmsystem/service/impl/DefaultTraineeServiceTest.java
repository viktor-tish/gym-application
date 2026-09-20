package org.vttish.gymcrmsystem.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymcrmsystem.dto.CreateTraineeDto;
import org.vttish.gymcrmsystem.dto.TraineeCardDto;
import org.vttish.gymcrmsystem.dto.TraineeDetailsDto;
import org.vttish.gymcrmsystem.dto.UpdateTraineeDto;
import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.mapper.TraineeMapper;
import org.vttish.gymcrmsystem.persistence.dao.TraineeDao;
import org.vttish.gymcrmsystem.service.ProfileGenerationService;

@ExtendWith(MockitoExtension.class)
class DefaultTraineeServiceTest {

  @Mock
  private TraineeDao traineeDao;

  @Mock
  private ProfileGenerationService profileGenerationService;

  @Mock
  private TraineeMapper mapper;

  @InjectMocks
  private DefaultTraineeService traineeService;

  @Test
  public void create_shouldReturnTraineeDetailsDto_whenValidDtoProvided() {
    CreateTraineeDto dto = new CreateTraineeDto(
        "firstName",
        "lastName",
        true,
        LocalDate.of(2000, 1, 1),
        "address"
    );
    Trainee trainee = new Trainee();
    TraineeDetailsDto expectedResult = new TraineeDetailsDto();

    when(profileGenerationService.generateUniqueUsername("firstName", "lastName"))
        .thenReturn("firstName.lastName");
    when(profileGenerationService.generateRandomPassword()).thenReturn("password12");
    when(mapper.create(dto)).thenReturn(trainee);
    when(traineeDao.create(trainee)).thenReturn(trainee);
    when(mapper.toTraineeDetailsDto(trainee)).thenReturn(expectedResult);

    TraineeDetailsDto result = traineeService.create(dto);

    assertEquals(expectedResult, result);
    assertEquals("firstName.lastName", trainee.getUsername());
    assertEquals("password12", trainee.getPassword());
    verify(traineeDao).create(trainee);
  }

  @Test
  public void create_shouldThrowException_whenDtoIsNull() {
    assertThrows(IllegalArgumentException.class, () -> traineeService.create(null));
  }

  @Test
  public void getById_shouldReturnTraineeDetailsDto_whenIdExists() {
    UUID id = UUID.randomUUID();
    Trainee trainee = new Trainee();
    TraineeDetailsDto expectedResult = new TraineeDetailsDto();

    when(traineeDao.selectById(id)).thenReturn(Optional.of(trainee));
    when(mapper.toTraineeDetailsDto(trainee)).thenReturn(expectedResult);

    TraineeDetailsDto result = traineeService.getById(id);

    assertEquals(expectedResult, result);
  }

  @Test
  public void getById_shouldThrowException_whenIdDoesNotExist() {
    UUID id = UUID.randomUUID();
    when(traineeDao.selectById(id)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> traineeService.getById(id));
  }

  @Test
  public void getAll_shouldReturnListOfTraineeCardDtos() {
    Trainee trainee = new Trainee();
    TraineeCardDto cardDto = new TraineeCardDto();

    when(traineeDao.selectAll()).thenReturn(List.of(trainee));
    when(mapper.toTraineeCardDto(trainee)).thenReturn(cardDto);

    List<TraineeCardDto> result = traineeService.getAll();

    assertEquals(1, result.size());
    assertEquals(cardDto, result.get(0));
  }

  @Test
  public void update_shouldReturnUpdatedTraineeDetailsDto_whenValidDtoProvided() {
    UUID id = UUID.randomUUID();
    UpdateTraineeDto dto = new UpdateTraineeDto(
        id,
        "firstName",
        "lastName",
        "firstName.lastName",
        "password12",
        true,
        LocalDate.of(2000, 1, 1),
        "address"
    );
    Trainee existingTrainee = new Trainee();
    TraineeDetailsDto expectedResult = new TraineeDetailsDto();

    when(profileGenerationService.existsByUsername("firstName.lastName")).thenReturn(true);
    when(traineeDao.selectById(id)).thenReturn(Optional.of(existingTrainee));
    when(mapper.toTraineeDetailsDto(existingTrainee)).thenReturn(expectedResult);

    TraineeDetailsDto result = traineeService.update(dto);

    assertEquals(expectedResult, result);
    verify(mapper).update(existingTrainee, dto);
  }

  @Test
  public void delete_shouldCallDaoDelete_whenIdIsProvided() {
    UUID id = UUID.randomUUID();

    traineeService.delete(id);

    verify(traineeDao).delete(id);
  }
}
