package org.vttish.gymcrmsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ObjectFactory;
import org.vttish.gymcrmsystem.dto.TraineeDto;
import org.vttish.gymcrmsystem.entity.Trainee;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TraineeMapper {
    Trainee toTrainee(TraineeDto traineeDto);
    TraineeDto toTraineeDto(Trainee trainee);

    @ObjectFactory
    default Trainee objectFactory(TraineeDto traineeDto) {
        if (traineeDto == null) {
            return null;
        }

        return new Trainee(traineeDto.getUserId());
    }
}
