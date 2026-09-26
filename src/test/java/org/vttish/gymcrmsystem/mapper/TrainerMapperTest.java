package org.vttish.gymcrmsystem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.vttish.gymcrmsystem.dto.TrainerDto;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.entity.TrainingType;

class TrainerMapperTest {
  private final TrainerMapper mapper = Mappers.getMapper(TrainerMapper.class);

  @Test
  public void toTrainer_shouldMapAllFieldsAndUseObjectFactoryForId() {
    UUID expectedId = UUID.randomUUID();
    TrainerDto dto = new TrainerDto();
    dto.setUserId(expectedId);
    dto.setUsername("user.name");
    dto.setPassword("password");
    dto.setFirstName("firstName");
    dto.setLastName("lastName");
    dto.setActive(true);
    dto.setSpecialization(TrainingType.BOXING);

    Trainer entity = mapper.toTrainer(dto);

    assertNotNull(entity, "The mapped entity should not be null");
    assertEquals(expectedId, entity.getUserId(), "ID should be populated via the @ObjectFactory");
    assertEquals("user.name", entity.getUsername());
    assertEquals("password", entity.getPassword());
    assertEquals("firstName", entity.getFirstName());
    assertEquals("lastName", entity.getLastName());
    assertTrue(entity.isActive());
    assertEquals(TrainingType.BOXING, entity.getSpecialization());
  }

  @Test
  void toTrainerDto_shouldMapAllFields() {
    UUID expectedId = UUID.randomUUID();
    Trainer entity = new Trainer(expectedId);
    entity.setUsername("user.name");
    entity.setPassword("password");
    entity.setFirstName("firstName");
    entity.setLastName("lastName");
    entity.setActive(true);
    entity.setSpecialization(TrainingType.BOXING);

    TrainerDto dto = mapper.toTrainerDto(entity);

    assertNotNull(dto, "The mapped DTO should not be null");
    assertEquals(expectedId, dto.getUserId());
    assertEquals("user.name", dto.getUsername());
    assertEquals("password", dto.getPassword());
    assertEquals("firstName", dto.getFirstName());
    assertEquals("lastName", dto.getLastName());
    assertTrue(dto.isActive());
    assertEquals(TrainingType.BOXING, dto.getSpecialization());
  }

  @Test
  public void shouldReturnNull_whenSourceIsNull() {
    assertNull(mapper.toTrainer(null), "Mapping a null DTO should return null");
    assertNull(mapper.toTrainerDto(null), "Mapping a null entity should return null");
  }
}
