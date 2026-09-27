package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.vttish.gymapplication.dto.CreateTraineeDto;
import org.vttish.gymapplication.dto.TraineeCardDto;
import org.vttish.gymapplication.dto.TraineeDetailsDto;
import org.vttish.gymapplication.dto.TraineeDto;
import org.vttish.gymapplication.dto.UpdateTraineeDto;
import org.vttish.gymapplication.entity.Trainee;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TraineeMapper {
  Trainee toTrainee(TraineeDto traineeDto);

  TraineeDto toTraineeDto(Trainee trainee);

  TraineeCardDto toTraineeCardDto(Trainee trainee);

  TraineeDetailsDto toTraineeDetailsDto(Trainee trainee);

  Trainee create(CreateTraineeDto updateTraineeDto);

  void update(@MappingTarget Trainee trainee, UpdateTraineeDto updateTraineeDto);

  @ObjectFactory
  default Trainee objectFactory(TraineeDto traineeDto) {
    if (traineeDto == null) {
      return null;
    }

    return new Trainee(traineeDto.getUserId());
  }
}
