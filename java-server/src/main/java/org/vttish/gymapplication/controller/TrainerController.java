package org.vttish.gymapplication.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileResponseDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.facade.GymFacade;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {
  private final GymFacade gymFacade;

  @PostMapping
  public ResponseEntity<CredentialsResponseDto> register(
      @Valid @RequestBody RegisterTrainerRequestDto request
  ) {
    CredentialsResponseDto credentials = gymFacade.registerTrainer(request);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{username}")
        .buildAndExpand(credentials.username())
        .toUri();

    return ResponseEntity.created(location).body(credentials);
  }

  @GetMapping("/{username}")
  @PreAuthorize("hasRole('TRAINER') and #username == authentication.principal.username")
  public ResponseEntity<TrainerProfileResponseDto> getProfile(
      @PathVariable("username") String username
  ) {
    return ResponseEntity.ok(gymFacade.getTrainerProfile(username));
  }

  @PutMapping("/{username}")
  @PreAuthorize("hasRole('TRAINER') and #username == authentication.principal.username")
  public ResponseEntity<UpdateTrainerProfileResponseDto> updateProfile(
      @PathVariable("username") String username,
      @Valid @RequestBody UpdateTrainerProfileRequestDto request
  ) {
    return ResponseEntity.ok(gymFacade.updateTrainerProfile(username, request));
  }

  @GetMapping("/{username}/trainings")
  @PreAuthorize("hasRole('TRAINER') and #username == authentication.principal.username")
  public ResponseEntity<List<TrainerTrainingItemResponseDto>> getTrainings(
      @PathVariable("username") String username,
      @Valid @ModelAttribute GetTrainerTrainingsRequestDto request
  ) {
    return ResponseEntity.ok(gymFacade.getTrainerTrainings(username, request));
  }

  @PatchMapping("/{username}/status")
  @PreAuthorize("hasRole('TRAINER') and #username == authentication.principal.username")
  public ResponseEntity<Void> updateStatus(
      @PathVariable("username") String username,
      @Valid @RequestBody UpdateUserStatusRequestDto request
  ) {
    gymFacade.updateStatus(username, request);

    return ResponseEntity.ok().build();
  }
}
