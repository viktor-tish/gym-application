package org.vttish.gymapplication.core.controller;

import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vttish.gymapplication.core.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.core.dto.training.TrainingDetailsResponseDto;
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
  public ResponseEntity<TrainingDetailsResponseDto> create(
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

    TrainingDetailsResponseDto detailsResponseDto = gymFacade.createTraining(request);
    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{id}")
        .buildAndExpand(detailsResponseDto.id())
        .toUri();

    return ResponseEntity.created(location).body(detailsResponseDto);
  }

  @GetMapping("/{id}")
  @PreAuthorize("""
      hasAnyRole('TRAINEE', 'TRAINER')
      and hasAuthority('SCOPE_gym:read')
      """)
  public ResponseEntity<TrainingDetailsResponseDto> getDetails(
      @PathVariable UUID id,
      @Parameter(hidden = true) UserPrincipal userPrincipal
  ) {
    return ResponseEntity.ok(gymFacade.getDetails(id, userPrincipal.username()));
  }

  @GetMapping("/types")
  public ResponseEntity<List<TrainingTypeResponseDto>> getTypes() {
    return ResponseEntity.ok(gymFacade.getTrainingTypes());
  }
}
