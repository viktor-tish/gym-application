package org.vttish.gymcrmsystem.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.vttish.gymcrmsystem.dto.*;
import org.vttish.gymcrmsystem.service.TraineeService;
import org.vttish.gymcrmsystem.service.TrainerService;
import org.vttish.gymcrmsystem.service.TrainingService;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Component
public class GymCrmFacade {
    private final TraineeService traineeService;
    private final TrainerService trainerService;
    private final TrainingService trainingService;

    public TraineeDetailsDto createTrainee(CreateTraineeDto createTraineeDto) {
        return traineeService.create(createTraineeDto);
    }

    public TraineeDetailsDto updateTrainee(UpdateTraineeDto updateTraineeDto) {
        return traineeService.update(updateTraineeDto);
    }

    public void deleteTrainee(UUID id) {
        traineeService.delete(id);
    }

    public TraineeDetailsDto getTraineeById(UUID id) {
        return traineeService.getById(id);
    }

    public List<TraineeCardDto> getAllTrainees() {
        return traineeService.getAll();
    }


    public TrainerDetailsDto createTrainer(CreateTrainerDto createTrainerDto) {
        return trainerService.create(createTrainerDto);
    }

    public TrainerDetailsDto updateTrainer(UpdateTrainerDto updateTrainerDto) {
        return trainerService.update(updateTrainerDto);
    }

    public TrainerDetailsDto getTrainerById(UUID id) {
        return trainerService.getById(id);
    }

    public List<TrainerCardDto> getAllTrainers() {
        return trainerService.getAll();
    }


    public TrainingDetailsDto createTraining(CreateTrainingDto createTrainingDto) {
        return trainingService.create(createTrainingDto);
    }

    public TrainingDetailsDto getTrainingById(UUID id) {
        return trainingService.getById(id);
    }

    public List<TrainingCardDto> getAllTrainings() {
        return trainingService.getAll();
    }
}
