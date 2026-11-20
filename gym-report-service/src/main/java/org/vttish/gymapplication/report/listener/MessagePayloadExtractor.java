package org.vttish.gymapplication.report.listener;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MessagePayloadExtractor {

  private final MessageConverter messageConverter;
  private final Validator validator;

  public <T> T extract(Message message, Class<T> clazz) throws JMSException {
    T payload = clazz.cast(messageConverter.fromMessage(message));

    Set<ConstraintViolation<T>> violations = validator.validate(payload);

    if (!violations.isEmpty()) {
      throw new ConstraintViolationException(violations);
    }

    return payload;
  }
}
