package org.vttish.gymcrmsystem.mapper;

import org.mapstruct.*;
import org.vttish.gymcrmsystem.dto.*;
import org.vttish.gymcrmsystem.entity.Trainee;

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
