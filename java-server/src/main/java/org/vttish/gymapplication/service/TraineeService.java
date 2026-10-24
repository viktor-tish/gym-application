package org.vttish.gymapplication.service;

import java.util.List;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;

public interface TraineeService {
  CredentialsResponseDto register(RegisterTraineeRequestDto request);

  TraineeProfileResponseDto getProfile(String username);

  UpdateTraineeProfileResponseDto updateProfile(
      String username,
      UpdateTraineeProfileRequestDto request
  );

  void delete(String username);

  List<TrainerItemResponseDto> updateTrainers(
      String username,
      UpdateTraineeTrainersRequestDto request
  );
}
