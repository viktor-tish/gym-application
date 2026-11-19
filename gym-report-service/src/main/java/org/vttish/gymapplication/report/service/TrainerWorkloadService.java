package org.vttish.gymapplication.report.service;

import org.vttish.gymapplication.report.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.report.dto.UpdateTrainerWorkloadRequestDto;

public interface TrainerWorkloadService {

  void updateWorkload(UpdateTrainerWorkloadRequestDto request);

  TrainerWorkloadResponseDto getWorkload(String username);
}
