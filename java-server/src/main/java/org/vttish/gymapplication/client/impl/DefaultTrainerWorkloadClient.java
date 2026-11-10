package org.vttish.gymapplication.client.impl;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.vttish.gymapplication.client.TrainerWorkloadClient;
import org.vttish.gymapplication.client.dto.ActionType;
import org.vttish.gymapplication.client.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.client.mapper.TrainerWorkloadMapper;
import org.vttish.gymapplication.event.TrainingCreatedEvent;
import org.vttish.gymapplication.event.TrainingDeletedEvent;
import org.vttish.gymapplication.logging.Loggable;

@Component
@Slf4j
@Loggable
public class DefaultTrainerWorkloadClient implements TrainerWorkloadClient {

  private final RestClient restClient;
  private final TrainerWorkloadMapper trainerWorkloadMapper;

  public DefaultTrainerWorkloadClient(
      @Qualifier("workloadReportClient") RestClient restClient,
      TrainerWorkloadMapper trainerWorkloadMapper
  ) {
    this.restClient = restClient;
    this.trainerWorkloadMapper = trainerWorkloadMapper;
  }

  @Override
  @CircuitBreaker(name = "trainerWorkloadClient", fallbackMethod = "submitWorkloadFallback")
  public void submitWorkload(TrainingCreatedEvent event) {
    send(
        event.trainerUsername(),
        trainerWorkloadMapper.toUpdateTrainerWorkloadRequestDto(event, ActionType.ADD)
    );
  }

  @Override
  @CircuitBreaker(name = "trainerWorkloadClient", fallbackMethod = "cancelWorkloadFallback")
  public void cancelWorkload(TrainingDeletedEvent event) {
    send(
        event.trainerUsername(),
        trainerWorkloadMapper.toUpdateTrainerWorkloadRequestDto(event, ActionType.DELETE)
    );
  }

  @SuppressWarnings("java:S1172")
  private void submitWorkloadFallback(TrainingCreatedEvent event, Exception ex) {
    log.warn("Falling back for submitWorkload: {}", ex.toString());
  }

  @SuppressWarnings("java:S1172")
  private void cancelWorkloadFallback(TrainingDeletedEvent event, Exception ex) {
    log.warn("Falling back for cancelWorkload: {}", ex.toString());
  }

  private void send(String username, UpdateTrainerWorkloadRequestDto payload) {
    restClient.post()
        .uri("/api/v1/trainers/{username}/workloads", username)
        .body(payload)
        .retrieve()
        .toBodilessEntity();
  }
}
