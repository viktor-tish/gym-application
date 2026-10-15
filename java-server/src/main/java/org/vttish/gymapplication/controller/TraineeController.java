package org.vttish.gymapplication.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.vttish.gymapplication.dto.auth.CredentialsResponseDto;
import org.vttish.gymapplication.dto.auth.UpdateUserStatusRequestDto;
import org.vttish.gymapplication.dto.error.ErrorResponseDto;
import org.vttish.gymapplication.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeTrainersRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.facade.GymFacade;

@Tag(
    name = "Trainee Management",
    description = "Operations for managing trainee profiles and their trainings"
)
@RestController
@RequestMapping("/api/v1/trainees")
@RequiredArgsConstructor
public class TraineeController {
  private final GymFacade gymFacade;

  @Operation(
      summary = "Register Trainee",
      description = "Creates a new trainee profile and returns auto-generated credentials"
  )
  @ApiResponse(
      responseCode = "201",
      description = "Trainee registered successfully"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Validation error or invalid request payload",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @PostMapping
  public ResponseEntity<CredentialsResponseDto> register(
      @Valid @RequestBody RegisterTraineeRequestDto request
  ) {
    CredentialsResponseDto credentials = gymFacade.registerTrainee(request);

    URI location = ServletUriComponentsBuilder.fromCurrentRequest()
        .path("/{username}")
        .buildAndExpand(credentials.username())
        .toUri();

    return ResponseEntity.created(location).body(credentials);
  }

  @Operation(
      summary = "Get Trainee Profile",
      description = "Returns full trainee profile by username including assigned trainers list"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Trainee profile returned successfully"
  )
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - authentication required or invalid credentials",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "Trainee not found",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @GetMapping("/{username}")
  public ResponseEntity<TraineeProfileResponseDto> getProfile(
      @Parameter(description = "Trainee username", required = true)
      @PathVariable("username") String username
  ) {
    return ResponseEntity.ok(gymFacade.getTraineeProfile(username));
  }

  @Operation(
      summary = "Update Trainee Profile",
      description = "Updates trainee profile and returns updated data with trainer list"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Trainee profile updated successfully"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Validation error or invalid request payload",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - authentication required or invalid credentials",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "Trainee not found",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @PutMapping("/{username}")
  public ResponseEntity<UpdateTraineeProfileResponseDto> updateProfile(
      @Parameter(description = "Trainee username", required = true)
      @PathVariable("username") String username,
      @Valid @RequestBody UpdateTraineeProfileRequestDto request
  ) {
    return ResponseEntity.ok(gymFacade.updateTraineeProfile(username, request));
  }

  @Operation(
      summary = "Delete Trainee Profile",
      description = "Hard deletes a trainee profile and cascades deletion to all related trainings"
  )
  @ApiResponse(
      responseCode = "204",
      description = "Trainee deleted successfully"
  )
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - authentication required or invalid credentials",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @DeleteMapping("/{username}")
  public ResponseEntity<Void> delete(
      @Parameter(description = "Trainee username", required = true)
      @PathVariable("username") String username
  ) {
    gymFacade.deleteTrainee(username);

    return ResponseEntity.noContent().build();
  }

  @Operation(
      summary = "Get Unassigned Active Trainers",
      description = "Returns a list of active trainers not currently assigned to the trainee"
  )
  @ApiResponse(
      responseCode = "200",
      description = "List of unassigned trainers"
  )
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - authentication required or invalid credentials",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "Trainee not found",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @GetMapping("/{username}/trainers/not-assigned")
  public ResponseEntity<List<TrainerItemResponseDto>> getUnassignedActiveTrainers(
      @Parameter(description = "Trainee username", required = true)
      @PathVariable("username") String username
  ) {
    return ResponseEntity.ok(gymFacade.getUnassignedActiveTrainers(username));
  }

  @Operation(
      summary = "Update Trainee's Trainer List",
      description = "Replaces the full list of trainers assigned to the trainee"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Trainer list updated successfully"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Validation error or invalid request payload",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - authentication required or invalid credentials",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "Trainee not found",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @PutMapping("/{username}/trainers")
  public ResponseEntity<List<TrainerItemResponseDto>> updateTrainers(
      @Parameter(description = "Trainee username", required = true)
      @PathVariable("username") String username,
      @Valid @RequestBody UpdateTraineeTrainersRequestDto request
  ) {
    return ResponseEntity.ok(gymFacade.updateTraineeTrainers(username, request));
  }

  @Operation(
      summary = "Get Trainee Trainings List",
      description = "Returns trainings filtered by date range, trainer name, or training type"
  )
  @ApiResponse(
      responseCode = "200",
      description = "List of trainee trainings"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Validation error or invalid request payload",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - authentication required or invalid credentials",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "Trainee not found",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @GetMapping("/{username}/trainings")
  public ResponseEntity<List<TraineeTrainingItemResponseDto>> getTrainings(
      @Parameter(description = "Trainee username", required = true)
      @PathVariable("username") String username,
      @Valid @ModelAttribute GetTraineeTrainingsRequestDto request
  ) {
    return ResponseEntity.ok(gymFacade.getTraineeTrainings(username, request));
  }

  @Operation(
      summary = "Activate or Deactivate Trainee",
      description = "Toggles the active status of a trainee profile. This action is non-idempotent"
  )
  @ApiResponse(
      responseCode = "200",
      description = "Trainee status updated successfully"
  )
  @ApiResponse(
      responseCode = "400",
      description = "Validation error or invalid request payload",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "401",
      description = "Unauthorized - authentication required or invalid credentials",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "404",
      description = "Trainee not found",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @ApiResponse(
      responseCode = "500",
      description = "Internal server error",
      content = @Content(schema = @Schema(implementation = ErrorResponseDto.class))
  )
  @PatchMapping("/{username}/status")
  public ResponseEntity<Void> updateStatus(
      @Parameter(description = "Trainee username", required = true)
      @PathVariable("username") String username,
      @Valid @RequestBody UpdateUserStatusRequestDto request
  ) {
    gymFacade.updateStatus(username, request);

    return ResponseEntity.ok().build();
  }
}
