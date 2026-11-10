package org.vttish.gymapplication.workloadreport.service;

import org.vttish.gymapplication.workloadreport.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.workloadreport.dto.UpdateTrainerWorkloadRequestDto;

public interface TrainerWorkloadService {

  void updateWorkload(UpdateTrainerWorkloadRequestDto request);

  TrainerWorkloadResponseDto getWorkload(String username);
}
