package org.vttish.gymapplication.workloadreport.service;

import org.vttish.gymapplication.workloadreport.dto.AddTrainerWorkloadRequestDto;
import org.vttish.gymapplication.workloadreport.dto.TrainerWorkloadResponseDto;

public interface TrainerWorkloadService {

  void addWorkload(AddTrainerWorkloadRequestDto request);

  TrainerWorkloadResponseDto getWorkload(String username);
}
