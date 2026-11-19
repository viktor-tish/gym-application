package org.vttish.gymapplication.report.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.report.dto.TrainerWorkloadResponseDto;
import org.vttish.gymapplication.report.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.report.exception.BadRequestException;
import org.vttish.gymapplication.report.handler.TrainerWorkloadHandler;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerWorkloadController {

  private final TrainerWorkloadHandler trainerWorkloadHandler;

  @PostMapping("/{username}/workloads")
  @PreAuthorize("""
      hasRole('TRAINER')
      and hasAuthority('SCOPE_report:trainer:write')
      and #username == authentication.principal.username
      """)
  public ResponseEntity<Void> updateWorkload(
      @PathVariable String username,
      @Valid @RequestBody UpdateTrainerWorkloadRequestDto request
  ) {
    if (!username.equals(request.username())) {
      throw new BadRequestException(
          "Username in path does not match body: %s, %s"
              .formatted(username, request.username())
      );
    }

    trainerWorkloadHandler.handleUpdateWorkload(request);

    return ResponseEntity.noContent().build();
  }

  @GetMapping("/{username}/workload")
  @PreAuthorize("""
      hasRole('TRAINER')
      and hasAuthority('SCOPE_report:trainer:read')
      and #username == authentication.principal.username
      """)
  public ResponseEntity<TrainerWorkloadResponseDto> getWorkload(@PathVariable String username) {
    return ResponseEntity.ok(trainerWorkloadHandler.handleGetWorkload(username));
  }
}
