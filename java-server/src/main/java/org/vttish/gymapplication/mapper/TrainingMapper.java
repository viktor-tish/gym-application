package org.vttish.gymapplication.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.vttish.gymapplication.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.entity.Training;
import org.vttish.gymapplication.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.repository.filter.TrainerTrainingsFilter;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = { TrainingTypeMapper.class }
)
public interface TrainingMapper {

  @Mapping(target = "trainee", ignore = true)
  @Mapping(target = "trainer", ignore = true)
  @Mapping(target = "type", ignore = true)
  Training create(CreateTrainingRequestDto dto);

  TrainerTrainingsFilter toTrainerTrainingsFilter(GetTrainerTrainingsRequestDto request);

  TraineeTrainingsFilter toTraineeTrainingsFilter(GetTraineeTrainingsRequestDto request);

  @Mapping(source = "trainee.user.username", target = "traineeUsername")
  TrainerTrainingItemResponseDto toTrainerTrainingItemResponseDto(Training training);

  @Mapping(source = "trainer.user.username", target = "trainerUsername")
  TraineeTrainingItemResponseDto toTraineeTrainingItemResponseDto(Training training);
}
