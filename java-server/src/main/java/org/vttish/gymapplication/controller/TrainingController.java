package org.vttish.gymapplication.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.facade.GymFacade;

@RestController
@RequestMapping("/api/v1/trainings")
@RequiredArgsConstructor
public class TrainingController {
  private final GymFacade gymFacade;

  @PostMapping
  public ResponseEntity<Void> create(
      @Valid @RequestBody CreateTrainingRequestDto request
  ) {
    gymFacade.createTraining(request);

    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/types")
  public ResponseEntity<List<TrainingTypeResponseDto>> getTypes() {
    return ResponseEntity.ok(gymFacade.getTrainingTypes());
  }
}
