package org.vttish.gymapplication.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.vttish.gymapplication.core.client.dto.UpdateTrainerWorkloadRequestDto;

@Configuration
public class JmsConfig {

  private static final String UPDATE_TRAINER_WORKLOAD_TYPE = "update-trainer-workload";

  @Bean
  public MessageConverter jacksonJmsMessageConverter(ObjectMapper objectMapper) {
    MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();

    converter.setObjectMapper(objectMapper);
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    converter.setTypeIdMappings(Map.of(
        UPDATE_TRAINER_WORKLOAD_TYPE, UpdateTrainerWorkloadRequestDto.class
    ));

    return converter;
  }
}
