package org.vttish.gymcrmsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ObjectFactory;
import org.vttish.gymcrmsystem.dto.CreateTrainingDto;
import org.vttish.gymcrmsystem.dto.TrainingCardDto;
import org.vttish.gymcrmsystem.dto.TrainingDetailsDto;
import org.vttish.gymcrmsystem.dto.TrainingDto;
import org.vttish.gymcrmsystem.entity.Training;

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
