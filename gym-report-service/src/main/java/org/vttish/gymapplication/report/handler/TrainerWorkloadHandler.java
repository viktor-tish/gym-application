package org.vttish.gymapplication.report.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.report.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.report.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.report.service.TrainerWorkloadService;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadHandler {

  private final TrainerWorkloadService trainerWorkloadService;

  @Retryable(
      includes = DataIntegrityViolationException.class,
      maxRetries = 2
  )
  public void handleUpdateWorkload(UpdateTrainerWorkloadRequestDto request) {
    trainerWorkloadService.updateWorkload(request);
  }

  public TrainerWorkloadResponseDto handleGetWorkload(String username) {
    return trainerWorkloadService.getWorkload(username);
  }
}
