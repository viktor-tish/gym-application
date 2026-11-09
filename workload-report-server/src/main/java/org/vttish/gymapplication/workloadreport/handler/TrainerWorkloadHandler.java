package org.vttish.gymapplication.workloadreport.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.workloadreport.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.workloadreport.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.workloadreport.service.TrainerWorkloadService;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadHandler {

  private final TrainerWorkloadService trainerWorkloadService;

  @Retryable(
      includes = DataIntegrityViolationException.class,
      maxRetries = 2
  )
  public void handleAddWorkload(UpdateTrainerWorkloadRequestDto request) {
    trainerWorkloadService.addWorkload(request);
  }

  public TrainerWorkloadResponseDto handleGetWorkload(String username) {
    return trainerWorkloadService.getWorkload(username);
  }
}
