package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.vttish.gymapplication.dto.CreateTrainerDto;
import org.vttish.gymapplication.dto.TrainerCardDto;
import org.vttish.gymapplication.dto.TrainerDetailsDto;
import org.vttish.gymapplication.dto.UpdateTrainerDto;
import org.vttish.gymapplication.entity.Trainer;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper {

  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "user.isActive", target = "active")
  @Mapping(source = "specialization.name", target = "specialization")
  @Mapping(source = "specialization.id", target = "specializationId")
  TrainerDetailsDto toTrainerDetailsDto(Trainer trainer);

  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "specialization.name", target = "specialization")
  @Mapping(source = "specialization.id", target = "specializationId")
  TrainerCardDto toTrainerCardDto(Trainer trainer);

  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "trainees", ignore = true)
  Trainer create(CreateTrainerDto dto);

  @Mapping(target = "user.firstName", source = "firstName")
  @Mapping(target = "user.lastName", source = "lastName")
  @Mapping(target = "user.isActive", ignore = true)
  @Mapping(target = "specialization", ignore = true)
  @Mapping(target = "trainees", ignore = true)
  void update(@MappingTarget Trainer trainer, UpdateTrainerDto dto);
}
