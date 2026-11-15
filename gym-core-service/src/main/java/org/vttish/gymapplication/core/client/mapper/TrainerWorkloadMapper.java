package org.vttish.gymapplication.core.client.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import org.vttish.gymapplication.core.client.dto.ActionType;
import org.vttish.gymapplication.core.client.dto.UpdateTrainerWorkloadRequestDto;
import org.vttish.gymapplication.core.event.TrainingCreatedEvent;
import org.vttish.gymapplication.core.event.TrainingDeletedEvent;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface TrainerWorkloadMapper {

  @Mapping(target = "username", source = "event.trainerUsername")
  @Mapping(target = "firstName", source = "event.trainerFirstName")
  @Mapping(target = "lastName", source = "event.trainerLastName")
  @Mapping(target = "isActive", source = "event.trainerIsActive")
  @Mapping(target = "trainingDate", source = "event.date")
  @Mapping(target = "trainingDuration", source = "event.duration")
  @Mapping(target = "actionType", source = "actionType")
  UpdateTrainerWorkloadRequestDto toUpdateTrainerWorkloadRequestDto(
      TrainingCreatedEvent event,
      ActionType actionType
  );

  @Mapping(target = "username", source = "event.trainerUsername")
  @Mapping(target = "firstName", source = "event.trainerFirstName")
  @Mapping(target = "lastName", source = "event.trainerLastName")
  @Mapping(target = "isActive", source = "event.trainerIsActive")
  @Mapping(target = "trainingDate", source = "event.date")
  @Mapping(target = "trainingDuration", source = "event.duration")
  @Mapping(target = "actionType", source = "actionType")
  UpdateTrainerWorkloadRequestDto toUpdateTrainerWorkloadRequestDto(
      TrainingDeletedEvent event,
      ActionType actionType
  );
}
