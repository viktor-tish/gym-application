package org.vttish.gymapplication.workloadreport.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.workloadreport.dto.AddTrainerWorkloadRequestDto;
import org.vttish.gymapplication.workloadreport.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.workloadreport.exception.BadRequestException;
import org.vttish.gymapplication.workloadreport.handler.TrainerWorkloadHandler;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerWorkloadController {

  private final TrainerWorkloadHandler trainerWorkloadHandler;

  @PostMapping("/{username}/workloads")
  public ResponseEntity<Void> addWorkload(
      @PathVariable String username,
      @Valid @RequestBody AddTrainerWorkloadRequestDto request
  ) {
    if (!username.equals(request.username())) {
      throw new BadRequestException(
          "Username in path does not match body: %s, %s"
              .formatted(username, request.username())
      );
    }

    trainerWorkloadHandler.handleAddWorkload(request);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{username}/workload")
  public ResponseEntity<TrainerWorkloadResponseDto> getWorkload(@PathVariable String username) {
    return ResponseEntity.ok(trainerWorkloadHandler.handleGetWorkload(username));
  }
}
