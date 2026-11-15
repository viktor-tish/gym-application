package org.vttish.gymapplication.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.vttish.gymapplication.core.dto.trainee.TraineeItemResponseDto;
import org.vttish.gymapplication.core.entity.Trainee;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TraineeItemMapper {

  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  TraineeItemResponseDto toTraineeItemResponseDto(Trainee trainee);
}
