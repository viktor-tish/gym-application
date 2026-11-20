package org.vttish.gymapplication.report.listener;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Collections;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jms.support.converter.MessageConversionException;
import org.springframework.jms.support.converter.MessageConverter;

@ExtendWith(MockitoExtension.class)
class MessagePayloadExtractorTest {

  @Mock
  private MessageConverter messageConverter;

  @Mock
  private Validator validator;

  @Mock
  private Message message;

  private MessagePayloadExtractor extractor;

  record TestPayload(String name, int hours) {}

  @BeforeEach
  void setUp() {
    extractor = new MessagePayloadExtractor(messageConverter, validator);
  }

  @Test
  void extract_whenValidPayload_returnsConvertedAndValidatedObject() throws JMSException {
    TestPayload expectedPayload = new TestPayload("alice.smith", 5);
    when(messageConverter.fromMessage(message)).thenReturn(expectedPayload);
    when(validator.validate(expectedPayload)).thenReturn(Collections.emptySet());

    TestPayload actualPayload = extractor.extract(message, TestPayload.class);

    assertSame(expectedPayload, actualPayload);
    verify(messageConverter).fromMessage(message);
    verify(validator).validate(expectedPayload);
  }

  @Test
  void extract_whenValidationFails_throwsConstraintViolationException() throws JMSException {
    TestPayload payload = new TestPayload("", -1);
    @SuppressWarnings("unchecked")
    ConstraintViolation<TestPayload> violation = mock(ConstraintViolation.class);
    Set<ConstraintViolation<TestPayload>> violations = Set.of(violation);

    when(messageConverter.fromMessage(message)).thenReturn(payload);
    when(validator.validate(payload)).thenReturn(violations);

    ConstraintViolationException thrown = assertThrows(
        ConstraintViolationException.class,
        () -> extractor.extract(message, TestPayload.class)
    );

    assertEquals(violations, thrown.getConstraintViolations());
  }

  @Test
  void extract_whenConversionFails_throwsMessageConversionException() throws JMSException {
    MessageConversionException conversionException = new MessageConversionException("Invalid JSON");
    when(messageConverter.fromMessage(message)).thenThrow(conversionException);

    MessageConversionException thrown = assertThrows(
        MessageConversionException.class,
        () -> extractor.extract(message, TestPayload.class)
    );

    assertSame(conversionException, thrown);
    verifyNoInteractions(validator);
  }

  @Test
  void extract_whenConvertedObjectHasIncompatibleType_throwsClassCastException() throws JMSException {
    when(messageConverter.fromMessage(message)).thenReturn("unexpected string payload");

    assertThrows(
        ClassCastException.class,
        () -> extractor.extract(message, TestPayload.class)
    );

    verifyNoInteractions(validator);
  }

  @Test
  void extract_whenMessageConverterThrowsJmsException_propagatesJmsException() throws JMSException {
    JMSException jmsException = new JMSException("Broker connection lost");
    when(messageConverter.fromMessage(message)).thenThrow(jmsException);

    JMSException thrown = assertThrows(
        JMSException.class,
        () -> extractor.extract(message, TestPayload.class)
    );

    assertSame(jmsException, thrown);
    verifyNoInteractions(validator);
  }
}
