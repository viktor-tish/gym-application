package org.vttish.gymapplication.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.vttish.gymapplication.core.dto.training.TrainingTypeResponseDto;
import org.vttish.gymapplication.core.entity.TrainingType;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingTypeMapper {
  TrainingTypeResponseDto toTrainingTypeResponseDto(TrainingType trainingType);
}
