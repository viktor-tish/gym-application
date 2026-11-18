package org.vttish.gymapplication.report.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.jms.ConnectionFactory;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.jms.autoconfigure.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.vttish.gymapplication.report.dto.UpdateTrainerWorkloadRequestDto;
import tools.jackson.databind.json.JsonMapper;

@Configuration
@Slf4j
public class JmsConfig {

  private static final String UPDATE_TRAINER_WORKLOAD_TYPE = "update-trainer-workload";

  @Bean
  public MessageConverter jacksonJmsMessageConverter(JsonMapper jsonMapper) {
    JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter(jsonMapper);

    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    converter.setTypeIdMappings(Map.of(
        UPDATE_TRAINER_WORKLOAD_TYPE, UpdateTrainerWorkloadRequestDto.class
    ));

    return converter;
  }

  @Bean
  public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
      ConnectionFactory connectionFactory,
      DefaultJmsListenerContainerFactoryConfigurer configurer,
      MessageConverter messageConverter
  ) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();

    configurer.configure(factory, connectionFactory);
    factory.setMessageConverter(messageConverter);
    factory.setErrorHandler(t ->
        log.error("JMS listener failed, message will be redelivered or sent to DLQ", t)
    );

    return factory;
  }
}
