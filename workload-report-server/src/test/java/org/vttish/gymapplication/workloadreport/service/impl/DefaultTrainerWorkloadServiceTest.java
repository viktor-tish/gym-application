package org.vttish.gymapplication.workloadreport.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymapplication.workloadreport.dto.ActionType;
import org.vttish.gymapplication.workloadreport.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.workloadreport.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.workloadreport.entity.TrainerProfile;
import org.vttish.gymapplication.workloadreport.exception.ResourceNotFoundException;
import org.vttish.gymapplication.workloadreport.mapper.TrainerWorkloadMapper;
import org.vttish.gymapplication.workloadreport.repository.TrainerProfileRepository;

@ExtendWith(MockitoExtension.class)
class DefaultTrainerWorkloadServiceTest {

  @Mock
  private TrainerProfileRepository trainerProfileRepository;

  @Mock
  private TrainerWorkloadMapper trainerWorkloadMapper;

  @InjectMocks
  private DefaultTrainerWorkloadService trainerWorkloadService;

  @Captor
  private ArgumentCaptor<TrainerProfile> trainerProfileCaptor;

  @Test
  void shouldUpdateExistingProfileAndAddTraining_whenActionIsAdd() {
    String username = "firstName.lastName";
    LocalDate trainingDate = LocalDate.of(2026, 5, 10);
    int duration = 60;

    UpdateTrainerWorkloadRequestDto request = new UpdateTrainerWorkloadRequestDto(
        username, "firstName", "lastName", true, trainingDate, duration, ActionType.ADD
    );

    TrainerProfile existingProfile = mock(TrainerProfile.class);
    when(trainerProfileRepository.findAndLockByUsername(username))
        .thenReturn(Optional.of(existingProfile));

    trainerWorkloadService.updateWorkload(request);

    verify(trainerProfileRepository).findAndLockByUsername(username);
    verify(trainerProfileRepository, never()).save(any());
    verify(existingProfile).syncProfile("firstName", "lastName", true);
    verify(existingProfile).recordTraining(trainingDate, duration);
    verify(existingProfile, never()).cancelTraining(any(), anyInt());
  }

  @Test
  void shouldUpdateExistingProfileAndCancelTraining_whenActionIsDelete() {
    String username = "firstName.lastName";
    LocalDate trainingDate = LocalDate.of(2026, 5, 10);
    int duration = 90;

    UpdateTrainerWorkloadRequestDto request = new UpdateTrainerWorkloadRequestDto(
        username, "firstName", "lastName", false, trainingDate, duration, ActionType.DELETE
    );

    TrainerProfile existingProfile = mock(TrainerProfile.class);
    when(trainerProfileRepository.findAndLockByUsername(username))
        .thenReturn(Optional.of(existingProfile));

    trainerWorkloadService.updateWorkload(request);

    verify(trainerProfileRepository).findAndLockByUsername(username);
    verify(trainerProfileRepository, never()).save(any());
    verify(existingProfile).syncProfile("firstName", "lastName", false);
    verify(existingProfile).cancelTraining(trainingDate, duration);
    verify(existingProfile, never()).recordTraining(any(), anyInt());
  }

  @Test
  void shouldCreateNewProfileAndRecordTraining_whenProfileNotFound() {
    String username = "firstName.lastName1";
    LocalDate trainingDate = LocalDate.of(2026, 6, 15);
    int duration = 120;

    UpdateTrainerWorkloadRequestDto request = new UpdateTrainerWorkloadRequestDto(
        username, "firstName", "lastName", true, trainingDate, duration, ActionType.ADD
    );

    when(trainerProfileRepository.findAndLockByUsername(username))
        .thenReturn(Optional.empty());

    TrainerProfile savedProfile = mock(TrainerProfile.class);
    when(trainerProfileRepository.save(any(TrainerProfile.class)))
        .thenReturn(savedProfile);

    trainerWorkloadService.updateWorkload(request);

    verify(trainerProfileRepository).findAndLockByUsername(username);
    verify(trainerProfileRepository).save(trainerProfileCaptor.capture());

    TrainerProfile capturedProfile = trainerProfileCaptor.getValue();
    assertNotNull(capturedProfile);

    verify(savedProfile).recordTraining(trainingDate, duration);
  }

  @Test
  void shouldReturnWorkloadResponseDto_whenProfileExists() {
    String username = "firstName.lastName";
    TrainerProfile profile = mock(TrainerProfile.class);
    TrainerWorkloadResponseDto expectedResponse = new TrainerWorkloadResponseDto(
        username, "firstName", "lastName", true, Collections.emptyList()
    );

    when(trainerProfileRepository.findWithWorkloadsByUsername(username))
        .thenReturn(Optional.of(profile));
    when(trainerWorkloadMapper.toTrainerWorkloadResponseDto(profile))
        .thenReturn(expectedResponse);

    TrainerWorkloadResponseDto actualResponse = trainerWorkloadService.getWorkload(username);

    assertNotNull(actualResponse);
    assertEquals(username, actualResponse.username());
    assertEquals("firstName", actualResponse.firstName());
    assertEquals("lastName", actualResponse.lastName());

    verify(trainerProfileRepository).findWithWorkloadsByUsername(username);
    verify(trainerWorkloadMapper).toTrainerWorkloadResponseDto(profile);
  }

  @Test
  void shouldThrowResourceNotFoundException_whenProfileNotFound() {
    String username = "firstName.lastName";
    when(trainerProfileRepository.findWithWorkloadsByUsername(username))
        .thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> trainerWorkloadService.getWorkload(username)
    );

    verify(trainerProfileRepository).findWithWorkloadsByUsername(username);
    verifyNoInteractions(trainerWorkloadMapper);
  }
}
