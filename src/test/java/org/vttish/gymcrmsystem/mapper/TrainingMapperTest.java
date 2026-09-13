package org.vttish.gymcrmsystem.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.vttish.gymcrmsystem.dto.TrainingDto;
import org.vttish.gymcrmsystem.entity.Training;
import org.vttish.gymcrmsystem.entity.TrainingType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class TrainingMapperTest {
    private final TrainingMapper mapper = Mappers.getMapper(TrainingMapper.class);

    @Test
    public void toTraining_shouldMapAllFields() {
        UUID expectedTraineeId = UUID.randomUUID();
        UUID expectedTrainerId = UUID.randomUUID();
        TrainingDto dto = new TrainingDto();
        dto.setTraineeId(expectedTraineeId);
        dto.setTrainerId(expectedTrainerId);
        dto.setTrainingName("trainingName");
        dto.setTrainingType(TrainingType.STRENGTH);
        dto.setTrainingDate(LocalDate.of(2000, 1, 1));
        dto.setTrainingDuration(Duration.ofMinutes(60));

        Training entity = mapper.toTraining(dto);

        assertNotNull(entity, "The mapped entity should not be null");
        assertEquals(expectedTraineeId, entity.getTraineeId());
        assertEquals(expectedTrainerId, entity.getTrainerId());
        assertEquals("trainingName", entity.getTrainingName());
        assertEquals(TrainingType.STRENGTH, entity.getTrainingType());
        assertEquals(LocalDate.of(2000, 1, 1), entity.getTrainingDate());
        assertEquals(Duration.ofMinutes(60), entity.getTrainingDuration());
    }

    @Test
    public void toTrainingDto_shouldMapAllFields() {
        UUID expectedTraineeId = UUID.randomUUID();
        UUID expectedTrainerId = UUID.randomUUID();
        Training entity = new Training();
        entity.setTraineeId(expectedTraineeId);
        entity.setTrainerId(expectedTrainerId);
        entity.setTrainingName("trainingName");
        entity.setTrainingType(TrainingType.STRENGTH);
        entity.setTrainingDate(LocalDate.of(2000, 1, 1));
        entity.setTrainingDuration(Duration.ofMinutes(90));

        TrainingDto dto = mapper.toTrainingDto(entity);

        assertNotNull(dto, "The mapped DTO should not be null");
        assertEquals(expectedTraineeId, dto.getTraineeId());
        assertEquals(expectedTrainerId, dto.getTrainerId());
        assertEquals("trainingName", dto.getTrainingName());
        assertEquals(TrainingType.STRENGTH, dto.getTrainingType());
        assertEquals(LocalDate.of(2000, 1, 1), dto.getTrainingDate());
        assertEquals(Duration.ofMinutes(90), dto.getTrainingDuration());
    }

    @Test
    public void shouldReturnNull_whenSourceIsNull() {
        assertNull(mapper.toTraining(null), "Mapping a null DTO should return null");
        assertNull(mapper.toTrainingDto(null), "Mapping a null entity should return null");
    }
}
