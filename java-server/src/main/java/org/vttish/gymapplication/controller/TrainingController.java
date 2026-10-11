package org.vttish.gymapplication.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vttish.gymapplication.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;

@RestController
@RequestMapping("/api/v1/trainings")
public class TrainingController {

  @PostMapping
  public ResponseEntity<Void> create(
      @Valid @RequestBody CreateTrainingRequestDto request
  ) {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }

  @GetMapping("/types")
  public ResponseEntity<List<TrainingTypeResponseDto>> getTypes() {
    return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
  }
}
