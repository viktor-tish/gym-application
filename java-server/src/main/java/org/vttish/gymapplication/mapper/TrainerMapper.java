package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.vttish.gymapplication.dto.trainer.RegisterTrainerRequestDto;
import org.vttish.gymapplication.dto.trainer.TrainerProfileResponseDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileRequestDto;
import org.vttish.gymapplication.dto.trainer.UpdateTrainerProfileResponseDto;
import org.vttish.gymapplication.entity.Trainer;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = { TraineeItemMapper.class, TrainingTypeMapper.class }
)
public interface TrainerMapper {

  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.isActive", target = "isActive")
  @Mapping(source = "specialization", target = "specialization")
  @Mapping(source = "trainees", target = "trainees")
  TrainerProfileResponseDto toTrainerProfileResponseDto(Trainer trainer);

  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.isActive", target = "isActive")
  @Mapping(source = "specialization", target = "specialization")
  @Mapping(source = "trainees", target = "trainees")
  UpdateTrainerProfileResponseDto toUpdateTrainerProfileResponseDto(Trainer trainer);

  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "user.isActive", ignore = true)
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "trainees", ignore = true)
  Trainer create(RegisterTrainerRequestDto dto);

  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "user.isActive", source = "isActive")
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "trainees", ignore = true)
  void update(@MappingTarget Trainer trainer, UpdateTrainerProfileRequestDto dto);
}
