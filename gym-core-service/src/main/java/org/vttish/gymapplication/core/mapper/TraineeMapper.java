package org.vttish.gymapplication.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.vttish.gymapplication.core.dto.trainee.RegisterTraineeRequestDto;
import org.vttish.gymapplication.core.dto.trainee.TraineeProfileResponseDto;
import org.vttish.gymapplication.core.dto.trainee.UpdateTraineeProfileRequestDto;
import org.vttish.gymapplication.core.dto.trainee.UpdateTraineeProfileResponseDto;
import org.vttish.gymapplication.core.entity.Trainee;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = { TrainerItemMapper.class }
)
public interface TraineeMapper {

  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.isActive", target = "isActive")
  @Mapping(source = "trainers", target = "trainers")
  TraineeProfileResponseDto toTraineeProfileResponseDto(Trainee trainee);

  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.isActive", target = "isActive")
  @Mapping(source = "trainers", target = "trainers")
  UpdateTraineeProfileResponseDto toUpdateTraineeProfileResponseDto(Trainee trainee);

  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "user.isActive", ignore = true)
  @Mapping(target = "trainings", ignore = true)
  @Mapping(target = "trainers", ignore = true)
  Trainee create(RegisterTraineeRequestDto dto);

  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "user.isActive", source = "isActive")
  @Mapping(target = "trainings", ignore = true)
  @Mapping(target = "trainers", ignore = true)
  void update(@MappingTarget Trainee trainee, UpdateTraineeProfileRequestDto dto);
}
