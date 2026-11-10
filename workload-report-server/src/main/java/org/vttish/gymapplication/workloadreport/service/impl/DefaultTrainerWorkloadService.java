package org.vttish.gymapplication.workloadreport.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.vttish.gymapplication.workloadreport.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.workloadreport.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.workloadreport.entity.TrainerProfile;
import org.vttish.gymapplication.workloadreport.exception.ResourceNotFoundException;
import org.vttish.gymapplication.workloadreport.logging.Loggable;
import org.vttish.gymapplication.workloadreport.mapper.TrainerWorkloadMapper;
import org.vttish.gymapplication.workloadreport.repository.TrainerProfileRepository;
import org.vttish.gymapplication.workloadreport.service.TrainerWorkloadService;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Loggable
public class DefaultTrainerWorkloadService implements TrainerWorkloadService {

  private final TrainerProfileRepository trainerProfileRepository;
  private final TrainerWorkloadMapper trainerWorkloadMapper;

  @Override
  @Transactional
  @SuppressWarnings("checkstyle:MissingSwitchDefault")
  public void updateWorkload(UpdateTrainerWorkloadRequestDto request) {
    TrainerProfile profile = trainerProfileRepository.findAndLockByUsername(request.username())
        .map(found -> {
          found.syncProfile(request.firstName(), request.lastName(), request.isActive());

          return found;
        })
        .orElseGet(() -> trainerProfileRepository.save(
                new TrainerProfile(
                    request.username(), request.firstName(), request.lastName(), request.isActive())
            )
        );

    switch (request.actionType()) {
      case ADD -> profile.recordTraining(request.trainingDate(), request.trainingDuration());
      case DELETE -> profile.cancelTraining(request.trainingDate(), request.trainingDuration());
    }
  }

  @Override
  public TrainerWorkloadResponseDto getWorkload(String username) {
    return trainerProfileRepository.findWithWorkloadsByUsername(username)
        .map(trainerWorkloadMapper::toTrainerWorkloadResponseDto)
        .orElseThrow(() -> new ResourceNotFoundException(
            "Trainer profile is not found for username: " + username)
        );
  }
}
