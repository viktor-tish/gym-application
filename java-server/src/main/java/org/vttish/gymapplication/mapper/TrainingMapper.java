package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.vttish.gymapplication.dto.CreateTrainingDto;
import org.vttish.gymapplication.dto.TrainingDetailsDto;
import org.vttish.gymapplication.entity.Training;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainingMapper {

  @Mapping(source = "type.name", target = "type")
  @Mapping(source = "type.id", target = "typeId")
  @Mapping(source = "trainer.user.username", target = "trainerUsername")
  @Mapping(source = "trainee.user.username", target = "traineeUsername")
  TrainingDetailsDto toTrainingDetailsDto(Training training);

  @Mapping(target = "trainee", ignore = true)
  @Mapping(target = "trainer", ignore = true)
  @Mapping(target = "type", ignore = true)
  Training create(CreateTrainingDto dto);
}
