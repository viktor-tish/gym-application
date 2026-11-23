package org.vttish.gymapplication.report.listener;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.jms.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.support.converter.MessageConversionException;
import org.vttish.gymapplication.report.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.report.handler.TrainerWorkloadHandler;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadListenerTest {

  @Mock
  private TrainerWorkloadHandler trainerWorkloadHandler;

  @Mock
  private ListenerExceptionHandler exceptionHandler;

  @Mock
  private MessagePayloadExtractor extractor;

  @Mock
  private Message message;

  private TrainerWorkloadListener listener;

  @BeforeEach
  void setUp() {
    listener = new TrainerWorkloadListener(trainerWorkloadHandler, exceptionHandler, extractor);
  }

  @Test
  void onUpdateTrainerWorkload_whenValidMessage_extractsAndHandlesPayloadWithoutInvokingExceptionHandler()
      throws Exception {
    UpdateTrainerWorkloadRequestDto payload = mock(UpdateTrainerWorkloadRequestDto.class);
    when(extractor.extract(message, UpdateTrainerWorkloadRequestDto.class)).thenReturn(payload);

    listener.onUpdateTrainerWorkload(message);

    InOrder order = inOrder(extractor, trainerWorkloadHandler);
    order.verify(extractor).extract(message, UpdateTrainerWorkloadRequestDto.class);
    order.verify(trainerWorkloadHandler).handleUpdateWorkload(payload);
    verifyNoInteractions(exceptionHandler);
  }

  @Test
  void onUpdateTrainerWorkload_whenExtractionFails_delegatesToExceptionHandlerAndSkipsHandler()
      throws Exception {
    MessageConversionException ex = new MessageConversionException("Bad JSON");
    when(extractor.extract(message, UpdateTrainerWorkloadRequestDto.class)).thenThrow(ex);

    listener.onUpdateTrainerWorkload(message);

    verify(exceptionHandler).handle(ex, message);
    verifyNoInteractions(trainerWorkloadHandler);
  }

  @Test
  void onUpdateTrainerWorkload_whenHandlerThrowsException_delegatesToExceptionHandler()
      throws Exception {
    UpdateTrainerWorkloadRequestDto payload = mock(UpdateTrainerWorkloadRequestDto.class);
    RuntimeException ex = new RuntimeException("DB unavailable");
    when(extractor.extract(message, UpdateTrainerWorkloadRequestDto.class)).thenReturn(payload);
    doThrow(ex).when(trainerWorkloadHandler).handleUpdateWorkload(payload);

    listener.onUpdateTrainerWorkload(message);

    verify(exceptionHandler).handle(ex, message);
  }

  @Test
  void onUpdateTrainerWorkload_whenExceptionHandlerRethrows_propagatesException()
      throws Exception {
    UpdateTrainerWorkloadRequestDto payload = mock(UpdateTrainerWorkloadRequestDto.class);
    IllegalStateException transientEx = new IllegalStateException("Transient failure");
    when(extractor.extract(message, UpdateTrainerWorkloadRequestDto.class)).thenReturn(payload);
    doThrow(transientEx).when(trainerWorkloadHandler).handleUpdateWorkload(payload);
    doThrow(transientEx).when(exceptionHandler).handle(transientEx, message);

    IllegalStateException thrown = assertThrows(
        IllegalStateException.class,
        () -> listener.onUpdateTrainerWorkload(message)
    );

    assertSame(transientEx, thrown);
  }
}
