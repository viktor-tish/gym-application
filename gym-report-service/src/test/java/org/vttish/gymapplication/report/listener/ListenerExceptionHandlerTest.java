package org.vttish.gymapplication.report.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import jakarta.jms.Message;
import jakarta.jms.Session;
import java.io.IOException;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.messaging.handler.invocation.MethodArgumentResolutionException;

@ExtendWith(MockitoExtension.class)
class ListenerExceptionHandlerTest {

  private static final String DLQ_NAME = "app.trainer-workload.dlq";

  @Mock
  private JmsTemplate jmsTemplate;

  @Mock
  private Message message;

  private ListenerExceptionHandler handler;

  @BeforeEach
  void setUp() {
    Set<Class<? extends Exception>> permanentExceptions = Set.of(
        MessageConversionException.class,
        MethodArgumentResolutionException.class
    );

    handler = new ListenerExceptionHandler(permanentExceptions, DLQ_NAME, jmsTemplate);
  }

  @Test
  void handle_whenDirectPermanentException_sendsMessageToDlqWithoutThrowing() throws Exception {
    MessageConversionException ex = new MessageConversionException("Malformed JSON");

    doAnswer(invocation -> {
      MessageCreator creator = invocation.getArgument(1);
      Session session = mock(Session.class);
      assertSame(message, creator.createMessage(session));
      return null;
    }).when(jmsTemplate).send(eq(DLQ_NAME), any(MessageCreator.class));

    handler.handle(ex, message);

    verify(jmsTemplate).send(eq(DLQ_NAME), any(MessageCreator.class));
  }

  @Test
  void handle_whenNestedCauseIsPermanentException_sendsMessageToDlqWithoutThrowing() throws Exception {
    MethodArgumentResolutionException rootCause = mock(MethodArgumentResolutionException.class);
    RuntimeException wrapper = new RuntimeException("Listener invocation failed", rootCause);

    doAnswer(invocation -> {
      MessageCreator creator = invocation.getArgument(1);
      Session session = mock(Session.class);
      assertSame(message, creator.createMessage(session));
      return null;
    }).when(jmsTemplate).send(eq(DLQ_NAME), any(MessageCreator.class));

    handler.handle(wrapper, message);

    verify(jmsTemplate).send(eq(DLQ_NAME), any(MessageCreator.class));
  }

  @Test
  void handle_whenTransientRuntimeException_rethrowsOriginalWithoutSendingToDlq() {
    IllegalStateException transientEx = new IllegalStateException("DB connection pool exhausted");

    IllegalStateException thrown = assertThrows(
        IllegalStateException.class,
        () -> handler.handle(transientEx, message)
    );

    assertSame(transientEx, thrown);
    verifyNoInteractions(jmsTemplate);
  }

  @Test
  void handle_whenTransientCheckedException_wrapsInRuntimeExceptionWithoutSendingToDlq() {
    IOException checkedEx = new IOException("Temporary I/O failure");

    RuntimeException thrown = assertThrows(
        RuntimeException.class,
        () -> handler.handle(checkedEx, message)
    );

    assertSame(checkedEx, thrown.getCause());
    verifyNoInteractions(jmsTemplate);
  }

  @Test
  void handle_whenPermanentExceptionAndDlqSendFails_throwsRuntimeException() {
    MessageConversionException permanentEx = new MessageConversionException("Bad payload");
    JmsException brokerFailure = mock(JmsException.class);

    doThrow(brokerFailure).when(jmsTemplate).send(eq(DLQ_NAME), any(MessageCreator.class));

    RuntimeException thrown = assertThrows(
        RuntimeException.class,
        () -> handler.handle(permanentEx, message)
    );

    assertEquals("Failed to route message to DLQ", thrown.getMessage());
    assertSame(brokerFailure, thrown.getCause());
  }
}
