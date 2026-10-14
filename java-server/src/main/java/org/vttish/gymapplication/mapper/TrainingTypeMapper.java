package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.vttish.gymapplication.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.entity.TrainingType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingTypeMapper {
  TrainingTypeResponseDto toTrainingTypeResponseDto(TrainingType trainingType);
}
