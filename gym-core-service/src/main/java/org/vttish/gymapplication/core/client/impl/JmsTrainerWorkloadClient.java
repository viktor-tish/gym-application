package org.vttish.gymapplication.core.client.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.vttish.gymapplication.core.client.TrainerWorkloadClient;
import org.vttish.gymapplication.core.client.dto.ActionType;
import org.vttish.gymapplication.core.client.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.core.client.mapper.TrainerWorkloadMapper;
import org.vttish.gymapplication.core.event.TrainingCreatedEvent;
import org.vttish.gymapplication.core.event.TrainingDeletedEvent;
import org.vttish.gymapplication.core.logging.Loggable;
import org.vttish.gymapplication.core.logging.TracingHeadersPostprocessor;

@Component
@Slf4j
@Loggable
@ConditionalOnProperty(
    name = "app.trainer-workload.transport",
    havingValue = "jms"
)
public class JmsTrainerWorkloadClient implements TrainerWorkloadClient {


  private final JmsTemplate jmsTemplate;
  private final TrainerWorkloadMapper trainerWorkloadMapper;
  private final String jmsQueue;
  private final TracingHeadersPostprocessor tracingHeadersPostprocessor;

  public JmsTrainerWorkloadClient(
      JmsTemplate jmsTemplate,
      TrainerWorkloadMapper trainerWorkloadMapper,
      @Value("${app.trainer-workload.queue}") String jmsQueue,
      TracingHeadersPostprocessor tracingHeadersPostprocessor
  ) {
    this.jmsTemplate = jmsTemplate;
    this.trainerWorkloadMapper = trainerWorkloadMapper;
    this.jmsQueue = jmsQueue;
    this.tracingHeadersPostprocessor = tracingHeadersPostprocessor;
  }

  @Override
  public void submitWorkload(TrainingCreatedEvent event) {
    publish(trainerWorkloadMapper.toUpdateTrainerWorkloadRequestDto(event, ActionType.ADD));
  }

  @Override
  public void cancelWorkload(TrainingDeletedEvent event) {
    publish(trainerWorkloadMapper.toUpdateTrainerWorkloadRequestDto(event, ActionType.DELETE));
  }

  private void publish(UpdateTrainerWorkloadRequestDto payload) {
    jmsTemplate.convertAndSend(jmsQueue, payload, message -> {
      tracingHeadersPostprocessor.postProcessMessage(message);
      message.setStringProperty("JMSXGroupID", payload.username());

      return message;
    });

    log.debug("Published trainer workload update {}", payload);
  }
}
