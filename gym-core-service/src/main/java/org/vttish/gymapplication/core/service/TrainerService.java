package org.vttish.gymapplication.core.service;

import java.util.List;
import org.vttish.gymapplication.core.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.core.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.core.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.core.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.core.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.core.dto.trainer.UpdateTrainerProfileResponseDto;

public interface TrainerService {
  CredentialsResponseDto register(RegisterTrainerRequestDto request);

  TrainerProfileResponseDto getProfile(String username);

  UpdateTrainerProfileResponseDto updateProfile(
      String username,
      UpdateTrainerProfileRequestDto request
  );

  List<TrainerItemResponseDto> getUnassignedActiveTrainers(String traineeUsername);
}
