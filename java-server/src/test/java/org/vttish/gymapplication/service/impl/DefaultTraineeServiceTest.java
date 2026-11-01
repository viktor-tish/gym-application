package org.vttish.gymapplication.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.entity.Trainee;
import org.vttish.gymapplication.entity.Trainer;
import org.vttish.gymapplication.entity.User;
import org.vttish.gymapplication.exception.ResourceNotFoundException;
import org.vttish.gymapplication.mapper.TraineeMapper;
import org.vttish.gymapplication.mapper.TrainerItemMapper;
import org.vttish.gymapplication.repository.TraineeRepository;
import org.vttish.gymapplication.repository.TrainerRepository;
import org.vttish.gymapplication.service.ProfileGenerationService;

@ExtendWith(MockitoExtension.class)
class DefaultTraineeServiceTest {

  @Mock
  private TraineeRepository traineeRepository;

  @Mock
  private TrainerRepository trainerRepository;

  @Mock
  private ProfileGenerationService profileGenerationService;

  @Mock
  private TraineeMapper traineeMapper;

  @Mock
  private TrainerItemMapper trainerItemMapper;

  @Mock
  private PasswordEncoder passwordEncoder;

  @InjectMocks
  private DefaultTraineeService traineeService;

  private Trainee testTrainee;

  @BeforeEach
  void setUp() {
    User testUser = new User();
    testUser.setUsername("firstName.lastName");
    testUser.setIsActive(true);

    testTrainee = new Trainee();
    testTrainee.setUser(testUser);
    testTrainee.setTrainers(new HashSet<>());
  }

  @Test
  void register_ShouldGenerateCredentialsAndSave() {
    RegisterTraineeRequestDto request = new RegisterTraineeRequestDto(
        "firstName", "lastName", LocalDate.of(2000, 1, 1), "Address"
    );

    when(traineeMapper.create(request)).thenReturn(testTrainee);
    when(profileGenerationService.generateUniqueUsername("firstName", "lastName"))
        .thenReturn("firstName.lastName");
    when(profileGenerationService.generateRandomPassword()).thenReturn("plainPassword");
    when(passwordEncoder.encode("plainPassword")).thenReturn("hashedPassword");

    CredentialsResponseDto result = traineeService.register(request);

    assertEquals("firstName.lastName", result.username());
    assertEquals("plainPassword", result.password());
    assertEquals("hashedPassword", testTrainee.getUser().getPassword());
    verify(traineeRepository).save(testTrainee);
  }

  @Test
  void getProfile_ShouldReturnProfile_WhenTraineeExists() {
    TraineeProfileResponseDto profileDto = new TraineeProfileResponseDto(
        "firstName", "lastName", LocalDate.of(2000, 1, 1), "Address", true, List.of()
    );

    when(traineeRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainee));
    when(traineeMapper.toTraineeProfileResponseDto(testTrainee)).thenReturn(profileDto);

    TraineeProfileResponseDto result = traineeService.getProfile("firstName.lastName");

    assertNotNull(result);
    assertEquals("firstName", result.firstName());
  }

  @Test
  void getProfile_ShouldThrowException_WhenTraineeNotFound() {
    when(traineeRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> traineeService.getProfile("nonexistent.username")
    );
  }

  @Test
  void updateProfile_ShouldUpdateAndReturnProfile_WhenTraineeExists() {
    UpdateTraineeProfileRequestDto request = new UpdateTraineeProfileRequestDto(
        "firstName", "lastName", LocalDate.of(2000, 1, 1), "Address", true
    );
    UpdateTraineeProfileResponseDto responseDto = new UpdateTraineeProfileResponseDto(
        "firstName.lastName", "firstName", "lastName", LocalDate.of(2000, 1, 1),
        "Address", true, List.of()
    );

    when(traineeRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainee));
    when(traineeMapper.toUpdateTraineeProfileResponseDto(testTrainee)).thenReturn(responseDto);

    UpdateTraineeProfileResponseDto result = traineeService.updateProfile(
        "firstName.lastName", request
    );

    assertNotNull(result);
    assertEquals("firstName.lastName", result.username());
    verify(traineeMapper).update(testTrainee, request);
  }

  @Test
  void updateProfile_ShouldThrowException_WhenTraineeNotFound() {
    UpdateTraineeProfileRequestDto request = new UpdateTraineeProfileRequestDto(
        "firstName", "lastName", LocalDate.of(2000, 1, 1), "Address", true
    );

    when(traineeRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> traineeService.updateProfile("nonexistent.username", request)
    );
  }

  @Test
  void delete_ShouldCallRepositoryDelete() {
    traineeService.delete("firstName.lastName");

    verify(traineeRepository).deleteByUsername("firstName.lastName");
  }

  @Test
  void updateTrainers_ShouldUpdateTrainersAndReturnList_WhenValid() {
    Trainer trainer = new Trainer();
    User trainerUser = new User();

    trainerUser.setUsername("trainer.username");
    trainer.setUser(trainerUser);

    TrainerItemResponseDto itemDto = new TrainerItemResponseDto(
        "trainer.username", "trainerFirst", "trainerLast", null
    );
    UpdateTraineeTrainersRequestDto request = new UpdateTraineeTrainersRequestDto(
        List.of("trainer.username")
    );

    when(traineeRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainee));
    when(trainerRepository.findAllByUsernameIn(List.of("trainer.username")))
        .thenReturn(List.of(trainer));
    when(trainerItemMapper.toTrainerItemResponseDto(trainer)).thenReturn(itemDto);

    List<TrainerItemResponseDto> result = traineeService.updateTrainers(
        "firstName.lastName", request
    );

    assertEquals(1, result.size());
    assertEquals("trainer.username", result.get(0).username());
  }

  @Test
  void updateTrainers_ShouldThrowException_WhenTrainerNotFound() {
    UpdateTraineeTrainersRequestDto request = new UpdateTraineeTrainersRequestDto(
        List.of("nonexistent.trainer")
    );

    when(traineeRepository.findByUsername("firstName.lastName"))
        .thenReturn(Optional.of(testTrainee));
    when(trainerRepository.findAllByUsernameIn(List.of("nonexistent.trainer")))
        .thenReturn(List.of());

    assertThrows(
        ResourceNotFoundException.class,
        () -> traineeService.updateTrainers("firstName.lastName", request)
    );
  }

  @Test
  void updateTrainers_ShouldThrowException_WhenTraineeNotFound() {
    UpdateTraineeTrainersRequestDto request = new UpdateTraineeTrainersRequestDto(
        List.of("trainer.username")
    );

    when(traineeRepository.findByUsername("nonexistent.username")).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> traineeService.updateTrainers("nonexistent.username", request)
    );
  }
}
