package org.vttish.gymapplication.report.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.report.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.report.handler.TrainerWorkloadHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainerWorkloadListener {

  private final TrainerWorkloadHandler trainerWorkloadHandler;

  @JmsListener(destination = "${app.trainer-workload.queue}")
  public void onUpdateTrainerWorkload(@Payload UpdateTrainerWorkloadRequestDto payload) {
    trainerWorkloadHandler.handleUpdateWorkload(payload);
  }
}
