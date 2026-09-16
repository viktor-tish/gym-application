package org.vttish.gymcrmsystem.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.vttish.gymcrmsystem.dto.*;
import org.vttish.gymcrmsystem.entity.Trainer;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TrainerMapper {
    Trainer toTrainer(TrainerDto trainerDto);
    TrainerDto toTrainerDto(Trainer trainer);
    TrainerCardDto toTrainerCardDto(Trainer trainer);
    TrainerDetailsDto toTrainerDetailsDto(Trainer trainer);

    Trainer create(CreateTrainerDto createTrainerDto);
    void update(@MappingTarget Trainer trainer, UpdateTrainerDto updateTrainerDto);

    @ObjectFactory
    default Trainer objectFactory(TrainerDto trainerDto) {
        if  (trainerDto == null) {
            return null;
        }

        return new Trainer(trainerDto.getUserId());
    }
}
