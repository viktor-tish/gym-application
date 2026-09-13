package org.vttish.gymcrmsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.vttish.gymcrmsystem.dto.TrainingDto;
import org.vttish.gymcrmsystem.entity.Training;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingMapper {
    Training toTraining(TrainingDto dto);
    TrainingDto toTrainingDto(Training training);
}
