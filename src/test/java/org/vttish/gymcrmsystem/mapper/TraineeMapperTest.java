package org.vttish.gymcrmsystem.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.vttish.gymcrmsystem.dto.TraineeDto;
import org.vttish.gymcrmsystem.entity.Trainee;

class TraineeMapperTest {
  private final TraineeMapper mapper = Mappers.getMapper(TraineeMapper.class);

  @Test
  public void toTrainee_shouldMapAllFieldsAndUseObjectFactoryForId() {
    UUID expectedId = UUID.randomUUID();
    TraineeDto dto = new TraineeDto();
    dto.setUserId(expectedId);
    dto.setUsername("user.name");
    dto.setPassword("password");
    dto.setFirstName("firstName");
    dto.setLastName("lastName");
    dto.setActive(true);
    dto.setDateOfBirth(LocalDate.of(2000, 1, 1));
    dto.setAddress("address");

    Trainee entity = mapper.toTrainee(dto);

    assertNotNull(entity, "The mapped entity should not be null");
    assertEquals(expectedId, entity.getUserId(), "ID should be populated via the @ObjectFactory");
    assertEquals("user.name", entity.getUsername());
    assertEquals("password", entity.getPassword());
    assertEquals("firstName", entity.getFirstName());
    assertEquals("lastName", entity.getLastName());
    assertTrue(entity.isActive());
    assertEquals(LocalDate.of(2000, 1, 1), entity.getDateOfBirth());
    assertEquals("address", entity.getAddress());
  }

  @Test
  void toTraineeDto_shouldMapAllFields() {
    UUID expectedId = UUID.randomUUID();
    Trainee entity = new Trainee(expectedId);
    entity.setUsername("user.name");
    entity.setPassword("password");
    entity.setFirstName("firstName");
    entity.setLastName("lastName");
    entity.setActive(true);
    entity.setDateOfBirth(LocalDate.of(2000, 1, 1));
    entity.setAddress("address");

    TraineeDto dto = mapper.toTraineeDto(entity);

    assertNotNull(dto, "The mapped DTO should not be null");
    assertEquals(expectedId, dto.getUserId());
    assertEquals("user.name", dto.getUsername());
    assertEquals("password", dto.getPassword());
    assertEquals("firstName", dto.getFirstName());
    assertEquals("lastName", dto.getLastName());
    assertTrue(dto.isActive());
    assertEquals(LocalDate.of(2000, 1, 1), dto.getDateOfBirth());
    assertEquals("address", dto.getAddress());
  }

  @Test
  public void shouldReturnNull_whenSourceIsNull() {
    assertNull(mapper.toTrainee(null), "Mapping a null DTO should return null");
    assertNull(mapper.toTraineeDto(null), "Mapping a null entity should return null");
  }
}