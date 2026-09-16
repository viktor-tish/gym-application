package org.vttish.gymcrmsystem.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vttish.gymcrmsystem.dto.CreateTrainerDto;
import org.vttish.gymcrmsystem.dto.TrainerCardDto;
import org.vttish.gymcrmsystem.dto.TrainerDetailsDto;
import org.vttish.gymcrmsystem.dto.TrainerDto;
import org.vttish.gymcrmsystem.dto.UpdateTrainerDto;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.entity.TrainingType;
import org.vttish.gymcrmsystem.mapper.TrainerMapper;
import org.vttish.gymcrmsystem.persistence.dao.TrainerDao;
import org.vttish.gymcrmsystem.service.ProfileGenerationService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DefaultTrainerServiceTest {

    @Mock
    private TrainerDao trainerDao;

    @Mock
    private ProfileGenerationService profileGenerationService;

    @Mock
    private TrainerMapper mapper;

    @InjectMocks
    private DefaultTrainerService trainerService;

    @Test
    public void create_shouldReturnTrainerDto_whenValidDtoProvided() {
        CreateTrainerDto dto = new CreateTrainerDto(
                "firstName",
                "lastName",
                true,
                TrainingType.YOGA
        );
        Trainer trainer = new Trainer();
        TrainerDto expectedResult = new TrainerDto();

        when(profileGenerationService.generateUniqueUsername("firstName", "lastName")).thenReturn("firstName.lastName");
        when(profileGenerationService.generateRandomPassword()).thenReturn("password12");
        when(mapper.create(dto)).thenReturn(trainer);
        when(trainerDao.create(trainer)).thenReturn(trainer);
        when(mapper.toTrainerDto(trainer)).thenReturn(expectedResult);

        TrainerDto result = trainerService.create(dto);

        assertEquals(expectedResult, result);
        assertEquals("firstName.lastName", trainer.getUsername());
        assertEquals("password12", trainer.getPassword());
        verify(trainerDao).create(trainer);
    }

    @Test
    public void create_shouldThrowException_whenDtoIsNull() {
        assertThrows(IllegalArgumentException.class, () -> trainerService.create(null));
    }

    @Test
    public void getById_shouldReturnTrainerDetailsDto_whenIdExists() {
        UUID id = UUID.randomUUID();
        Trainer trainer = new Trainer();
        TrainerDetailsDto expectedResult = new TrainerDetailsDto();

        when(trainerDao.selectById(id)).thenReturn(Optional.of(trainer));
        when(mapper.toTrainerDetailsDto(trainer)).thenReturn(expectedResult);

        TrainerDetailsDto result = trainerService.getById(id);

        assertEquals(expectedResult, result);
    }

    @Test
    public void getById_shouldThrowException_whenIdDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(trainerDao.selectById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> trainerService.getById(id));
    }

    @Test
    public void getAll_shouldReturnListOfTrainerCardDtos() {
        Trainer trainer = new Trainer();
        TrainerCardDto cardDto = new TrainerCardDto();

        when(trainerDao.selectAll()).thenReturn(List.of(trainer));
        when(mapper.toTrainerCardDto(trainer)).thenReturn(cardDto);

        List<TrainerCardDto> result = trainerService.getAll();

        assertEquals(1, result.size());
        assertEquals(cardDto, result.get(0));
    }

    @Test
    public void update_shouldReturnUpdatedTrainerDetailsDto_whenValidDtoProvided() {
        UUID id = UUID.randomUUID();
        UpdateTrainerDto dto = new UpdateTrainerDto(
                id,
                "firstName",
                "lastName",
                "firstName.lastName",
                "password12",
                true,
                TrainingType.YOGA
        );
        Trainer existingTrainer = new Trainer();
        TrainerDetailsDto expectedResult = new TrainerDetailsDto();

        when(profileGenerationService.existsByUsername("firstName.lastName")).thenReturn(true);
        when(trainerDao.selectById(id)).thenReturn(Optional.of(existingTrainer));
        when(mapper.toTrainerDetailsDto(existingTrainer)).thenReturn(expectedResult);

        TrainerDetailsDto result = trainerService.update(dto);

        assertEquals(expectedResult, result);
        verify(mapper).update(existingTrainer, dto);
    }
}
