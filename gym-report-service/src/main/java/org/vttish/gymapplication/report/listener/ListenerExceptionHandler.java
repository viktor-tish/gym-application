package org.vttish.gymapplication.report.listener;

import jakarta.jms.Message;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ListenerExceptionHandler {

  private final Set<Class<? extends Exception>> permanentExceptions;
  private final JmsTemplate jmsTemplate;
  private final String dlqName;

  public ListenerExceptionHandler(
      @Qualifier("permanentExceptions") Set<Class<? extends Exception>> permanentExceptions,
      @Value("${app.trainer-workload.dlq}") String dlqName,
      JmsTemplate jmsTemplate
  ) {
    this.permanentExceptions = permanentExceptions;
    this.dlqName = dlqName;
    this.jmsTemplate = jmsTemplate;
  }

  public void handle(Exception ex, Message message) {
    if (isPermanent(ex)) {
      log.warn("Permanent exception during message processing: {}", ex.toString());
      sendToDlq(message);
      return;
    }

    log.error("Transient exception during message processing: {}", ex.toString());

    if (ex instanceof RuntimeException e) {
      throw e;
    }

    throw new RuntimeException(ex);
  }

  private boolean isPermanent(Exception ex) {
    Throwable current = ex;

    while (current != null) {

      for (Class<? extends Throwable> clazz : permanentExceptions) {
        if (clazz.isInstance(current)) {
          return true;
        }
      }

      current = current.getCause();
    }

    return false;
  }

  private void sendToDlq(Message message) {
    try {
      jmsTemplate.send(dlqName, session -> message);
    } catch (Exception e) {
      log.error("Failed to send message to DLQ '{}'", dlqName, e);

      throw new RuntimeException("Failed to route message to DLQ", e);
    }
  }
}
