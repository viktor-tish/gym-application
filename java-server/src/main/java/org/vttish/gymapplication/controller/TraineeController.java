package org.vttish.gymapplication.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingItemResponseDto;

@RestController
@RequestMapping("/api/v1/trainees")
public class TraineeController {

  @PostMapping
  public ResponseEntity<CredentialsResponseDto> register(
      @Valid @RequestBody RegisterTraineeRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/{username}")
  public ResponseEntity<TraineeProfileResponseDto> getProfile(@PathVariable String username) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @PutMapping("/{username}")
  public ResponseEntity<UpdateTraineeProfileResponseDto> updateProfile(
      @PathVariable String username,
      @Valid @RequestBody UpdateTraineeProfileRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @DeleteMapping("/{username}")
  public ResponseEntity<Void> delete(@PathVariable String username) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/{username}/trainers/not-assigned")
  public ResponseEntity<List<TrainerItemResponseDto>> getUnassignedActiveTrainers(
      @PathVariable String username
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @PutMapping("/{username}/trainers")
  public ResponseEntity<List<TrainerItemResponseDto>> updateTrainers(
      @PathVariable String username,
      @Valid @RequestBody UpdateTraineeTrainersRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/{username}/trainings")
  public ResponseEntity<List<TraineeTrainingItemResponseDto>> getTrainings(
      @PathVariable String username,
      @Valid @ModelAttribute GetTraineeTrainingsRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @PatchMapping("/{username}/status")
  public ResponseEntity<Void> updateStatus(
      @PathVariable String username,
      @Valid @RequestBody UpdateUserStatusRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
}
