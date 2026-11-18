package org.vttish.gymapplication.core.client.impl;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessagePostProcessor;
import org.vttish.gymapplication.core.client.dto.ActionType;
import org.vttish.gymapplication.core.client.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.core.client.mapper.TrainerWorkloadMapper;
import org.vttish.gymapplication.core.event.TrainingCreatedEvent;
import org.vttish.gymapplication.core.event.TrainingDeletedEvent;
import org.vttish.gymapplication.core.logging.TracingHeadersPostprocessor;

@ExtendWith(MockitoExtension.class)
class JmsTrainerWorkloadClientTest {

  @Mock
  private JmsTemplate jmsTemplate;

  @Mock
  private TrainerWorkloadMapper trainerWorkloadMapper;

  @Mock
  private TracingHeadersPostprocessor tracingHeadersPostprocessor;

  private JmsTrainerWorkloadClient jmsTrainerWorkloadClient;

  private static final String QUEUE_NAME = "gym.trainer-workload.updates";

  @BeforeEach
  void setUp() {
    jmsTrainerWorkloadClient = new JmsTrainerWorkloadClient(
        jmsTemplate,
        trainerWorkloadMapper,
        QUEUE_NAME,
        tracingHeadersPostprocessor
    );
  }

  @Test
  void submitWorkload_ShouldMapAndPublishMessageWithTracingAndGroupId() throws JMSException {
    TrainingCreatedEvent event = mock(TrainingCreatedEvent.class);
    UpdateTrainerWorkloadRequestDto expectedDto = new UpdateTrainerWorkloadRequestDto(
        "john.doe",
        "John",
        "Doe",
        true,
        LocalDate.of(2026, 11, 20),
        60,
        ActionType.ADD
    );

    when(trainerWorkloadMapper.toUpdateTrainerWorkloadRequestDto(event, ActionType.ADD))
        .thenReturn(expectedDto);

    jmsTrainerWorkloadClient.submitWorkload(event);

    verify(trainerWorkloadMapper).toUpdateTrainerWorkloadRequestDto(event, ActionType.ADD);

    ArgumentCaptor<MessagePostProcessor> postProcessorCaptor =
        ArgumentCaptor.forClass(MessagePostProcessor.class);

    verify(jmsTemplate).convertAndSend(
        eq(QUEUE_NAME),
        eq(expectedDto),
        postProcessorCaptor.capture()
    );

    Message mockMessage = mock(Message.class);
    Message processedMessage = postProcessorCaptor.getValue().postProcessMessage(mockMessage);

    assertNotNull(processedMessage);
    verify(tracingHeadersPostprocessor).postProcessMessage(mockMessage);
    verify(mockMessage).setStringProperty("JMSXGroupID", "john.doe");
  }

  @Test
  void cancelWorkload_ShouldMapAndPublishMessageWithTracingAndGroupId() throws JMSException {
    TrainingDeletedEvent event = mock(TrainingDeletedEvent.class);
    UpdateTrainerWorkloadRequestDto expectedDto = new UpdateTrainerWorkloadRequestDto(
        "jane.smith",
        "Jane",
        "Smith",
        true,
        LocalDate.of(2026, 11, 21),
        90,
        ActionType.DELETE
    );

    when(trainerWorkloadMapper.toUpdateTrainerWorkloadRequestDto(event, ActionType.DELETE))
        .thenReturn(expectedDto);

    jmsTrainerWorkloadClient.cancelWorkload(event);

    verify(trainerWorkloadMapper).toUpdateTrainerWorkloadRequestDto(event, ActionType.DELETE);

    ArgumentCaptor<MessagePostProcessor> postProcessorCaptor =
        ArgumentCaptor.forClass(MessagePostProcessor.class);

    verify(jmsTemplate).convertAndSend(
        eq(QUEUE_NAME),
        eq(expectedDto),
        postProcessorCaptor.capture()
    );

    Message mockMessage = mock(Message.class);
    Message processedMessage = postProcessorCaptor.getValue().postProcessMessage(mockMessage);

    assertNotNull(processedMessage);
    verify(tracingHeadersPostprocessor).postProcessMessage(mockMessage);
    verify(mockMessage).setStringProperty("JMSXGroupID", "jane.smith");
  }
}
