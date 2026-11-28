package org.vttish.gymapplication.core.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.vttish.gymapplication.core.dto.trainee.GetTraineeTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.trainer.GetTrainerTrainingsRequestDto;
import org.vttish.gymapplication.core.dto.training.CreateTrainingRequestDto;
import org.vttish.gymapplication.core.dto.training.TraineeTrainingItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainerTrainingItemResponseDto;
import org.vttish.gymapplication.core.dto.training.TrainingDetailsResponseDto;
import org.vttish.gymapplication.core.entity.Training;
import org.vttish.gymapplication.core.repository.filter.TraineeTrainingsFilter;
import org.vttish.gymapplication.core.repository.filter.TrainerTrainingsFilter;

@Mapper(
    componentModel = MappingConstants.ComponentModel.SPRING,
    uses = { TrainingTypeMapper.class, TraineeItemMapper.class, TrainerItemMapper.class }
)
public interface TrainingMapper {

  @Mapping(target = "trainee", ignore = true)
  @Mapping(target = "trainer", ignore = true)
  @Mapping(target = "type", ignore = true)
  Training create(CreateTrainingRequestDto dto);

  TrainerTrainingsFilter toTrainerTrainingsFilter(GetTrainerTrainingsRequestDto request);

  TraineeTrainingsFilter toTraineeTrainingsFilter(GetTraineeTrainingsRequestDto request);

  TrainingDetailsResponseDto toTrainingDetailsResponseDto(Training training);

  @Mapping(source = "trainee.user.username", target = "traineeUsername")
  TrainerTrainingItemResponseDto toTrainerTrainingItemResponseDto(Training training);

  @Mapping(source = "trainer.user.username", target = "trainerUsername")
  TraineeTrainingItemResponseDto toTraineeTrainingItemResponseDto(Training training);
}
