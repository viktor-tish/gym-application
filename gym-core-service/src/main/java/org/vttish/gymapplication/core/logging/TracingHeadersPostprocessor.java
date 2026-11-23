package org.vttish.gymapplication.core.logging;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import org.slf4j.MDC;
import org.springframework.jms.core.MessagePostProcessor;
import org.springframework.stereotype.Component;

@Component
public class TracingHeadersPostprocessor implements MessagePostProcessor {

  private static final String TRANSACTION_ID_HEADER = "transactionId";
  private static final String PARENT_ID_HEADER = "parentId";

  private static final String TRANSACTION_ID_KEY = "transactionId";
  private static final String SPAN_ID_KEY = "spanId";

  @Override
  public Message postProcessMessage(Message message) throws JMSException {
    String transactionId = MDC.get(TRANSACTION_ID_KEY);
    String spanId = MDC.get(SPAN_ID_KEY);

    if (transactionId != null) {
      message.setStringProperty(TRANSACTION_ID_HEADER, transactionId);
      message.setJMSCorrelationID(transactionId);
    }

    if (spanId != null) {
      message.setStringProperty(PARENT_ID_HEADER, spanId);
    }

    return message;
  }
}
