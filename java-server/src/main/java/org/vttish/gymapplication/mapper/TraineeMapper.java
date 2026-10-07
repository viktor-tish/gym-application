package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.vttish.gymapplication.dto.trainee.CreateTraineeDto;
import org.vttish.gymapplication.dto.trainee.TraineeDetailsDto;
import org.vttish.gymapplication.dto.trainee.UpdateTraineeDto;
import org.vttish.gymapplication.entity.Trainee;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TraineeMapper {

  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "user.isActive", target = "active")
  TraineeDetailsDto toTraineeDetailsDto(Trainee trainee);

  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "trainings", ignore = true)
  @Mapping(target = "trainers", ignore = true)
  Trainee create(CreateTraineeDto dto);

  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "user.isActive", ignore = true)
  @Mapping(target = "trainings", ignore = true)
  @Mapping(target = "trainers", ignore = true)
  void update(@MappingTarget Trainee trainee, UpdateTraineeDto dto);
}
