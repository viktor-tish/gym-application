package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ObjectFactory;
import org.vttish.gymapplication.dto.CreateTrainingDto;
import org.vttish.gymapplication.dto.TrainingCardDto;
import org.vttish.gymapplication.dto.TrainingDetailsDto;
import org.vttish.gymapplication.dto.TrainingDto;
import org.vttish.gymapplication.entity.Training;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingMapper {
  Training toTraining(TrainingDto dto);

  TrainingDto toTrainingDto(Training training);

  TrainingCardDto toTrainingCardDto(Training training);

  TrainingDetailsDto toTrainingDetailsDto(Training training);

  Training create(CreateTrainingDto createTrainingDto);

  @ObjectFactory
  default Training objectFactory(TrainingDto trainingDto) {
    if (trainingDto == null) {
      return null;
    }
    return new Training(trainingDto.getId());
  }
}
