package org.vttish.gymapplication.core.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.core.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.core.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.core.exception.ForbiddenException;
import org.vttish.gymapplication.core.facade.GymFacade;
import org.vttish.gymapplication.core.security.model.Role;
import org.vttish.gymapplication.core.security.model.UserPrincipal;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {
  private final GymFacade gymFacade;

  @PostMapping
  @PreAuthorize("""
      hasAnyRole('TRAINEE', 'TRAINER')
      and hasAuthority('SCOPE_gym:training:write')
      """)
  public ResponseEntity<Void> create(
      @Valid @RequestBody CreateTrainingRequestDto request,
      @Parameter(hidden = true) UserPrincipal userPrincipal
  ) {
    if (userPrincipal == null) {
      throw new ForbiddenException("Access denied");
    }

    if (userPrincipal.role() == Role.TRAINER
        && !userPrincipal.username().equals(request.trainerUsername())) {
      throw new ForbiddenException("Access denied");
    }

    if (userPrincipal.role() == Role.TRAINEE
        && !userPrincipal.username().equals(request.traineeUsername())) {
      throw new ForbiddenException("Access denied");
    }

    gymFacade.createTraining(request);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/types")
  public ResponseEntity<List<TrainingTypeResponseDto>> getTypes() {
    return ResponseEntity.ok(gymFacade.getTrainingTypes());
  }
}
