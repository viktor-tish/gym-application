package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.vttish.gymapplication.dto.trainer.TrainerItemResponseDto;
import org.vttish.gymapplication.entity.Trainer;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = { TrainingTypeMapper.class }
)
public interface TrainerItemMapper {

  @Mapping(source = "user.username", target = "username")
  @Mapping(source = "user.firstName", target = "firstName")
  @Mapping(source = "user.lastName", target = "lastName")
  @Mapping(source = "specialization", target = "specialization")
  TrainerItemResponseDto toTrainerItemResponseDto(Trainer trainer);
}
