package org.vttish.gymapplication.report.listener;

import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.report.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.report.handler.TrainerWorkloadHandler;

@Component
@Slf4j
@RequiredArgsConstructor
public class TrainerWorkloadListener {

  private final TrainerWorkloadHandler trainerWorkloadHandler;
  private final ListenerExceptionHandler exceptionHandler;
  private final MessagePayloadExtractor extractor;

  @JmsListener(destination = "${app.trainer-workload.queue}")
  public void onUpdateTrainerWorkload(Message message) {
    try {
      UpdateTrainerWorkloadRequestDto payload =
          extractor.extract(message, UpdateTrainerWorkloadRequestDto.class);

      trainerWorkloadHandler.handleUpdateWorkload(payload);
    } catch (Exception ex) {
      exceptionHandler.handle(ex, message);
    }
  }
}
