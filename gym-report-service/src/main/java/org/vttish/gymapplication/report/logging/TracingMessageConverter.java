package org.vttish.gymapplication.report.logging;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.Session;
import java.util.UUID;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

public class TracingMessageConverter implements MessageConverter {

  private static final String TRANSACTION_ID_HEADER = "transactionId";
  private static final String PARENT_ID_HEADER = "parentId";

  private static final String TRANSACTION_ID_KEY = "transactionId";
  private static final String PARENT_ID_KEY = "parentId";
  private static final String SPAN_ID_KEY = "spanId";

  private static final String ROOT_PARENT = "root";

  private final MessageConverter converter;

  public TracingMessageConverter(MessageConverter converter) {
    this.converter = converter;
  }

  @Override
  public Message toMessage(
      Object object,
      Session session
  ) throws JMSException, MessageConversionException {
    return converter.toMessage(object, session);
  }

  @Override
  public Object fromMessage(
      Message message
  ) throws JMSException, MessageConversionException {
    MDC.remove(TRANSACTION_ID_KEY);
    MDC.remove(PARENT_ID_KEY);
    MDC.remove(SPAN_ID_KEY);

    String transactionId = message.getStringProperty(TRANSACTION_ID_HEADER);
    String parentId = message.getStringProperty(PARENT_ID_HEADER);

    transactionId = transactionId == null || transactionId.isBlank() ? newId() : transactionId;
    parentId = parentId == null || parentId.isBlank() ? ROOT_PARENT : parentId;

    String spanId = newId();

    MDC.put(TRANSACTION_ID_KEY, transactionId);
    MDC.put(SPAN_ID_KEY, spanId);
    MDC.put(PARENT_ID_KEY, parentId);

    return converter.fromMessage(message);
  }

  public static TaskDecorator mdcCleanupDecorator() {
    return runnable -> () -> {
      try {
        runnable.run();
      } finally {
        MDC.remove(TRANSACTION_ID_KEY);
        MDC.remove(PARENT_ID_KEY);
        MDC.remove(SPAN_ID_KEY);
      }
    };
  }

  private String newId() {
    return UUID.randomUUID().toString();
  }
}
