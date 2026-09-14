package org.vttish.gymcrmsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ObjectFactory;
import org.vttish.gymcrmsystem.dto.TrainerDto;
import org.vttish.gymcrmsystem.entity.Trainer;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper {
    Trainer toTrainer(TrainerDto trainerDto);
    TrainerDto toTrainerDto(Trainer trainer);

    @ObjectFactory
    default Trainer objectFactory(TrainerDto trainerDto) {
        if  (trainerDto == null) {
            return null;
        }

        return new Trainer(trainerDto.getUserId());
    }
}
