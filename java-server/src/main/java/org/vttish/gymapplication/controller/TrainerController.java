package org.vttish.gymapplication.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.vttish.gymapplication.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileResponseDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingItemResponseDto;

@RestController
@RequestMapping("/api/v1/trainers")
public class TrainerController {

  @PostMapping
  public ResponseEntity<CredentialsResponseDto> register(
      @Valid @RequestBody RegisterTrainerRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/{username}")
  public ResponseEntity<TrainerProfileResponseDto> getProfile(
      @PathVariable String username
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @PutMapping("/{username}")
  public ResponseEntity<UpdateTrainerProfileResponseDto> updateProfile(
      @PathVariable String username,
      @Valid @RequestBody UpdateTrainerProfileRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/{username}/trainings")
  public ResponseEntity<List<TrainerTrainingItemResponseDto>> getTrainings(
      @PathVariable String username,
      @Valid @ModelAttribute GetTrainerTrainingsRequestDto request
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
