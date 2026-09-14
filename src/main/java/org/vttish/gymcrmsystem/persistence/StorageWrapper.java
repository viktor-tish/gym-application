package org.vttish.gymcrmsystem.persistence;

import org.vttish.gymcrmsystem.entity.Trainee;
import org.vttish.gymcrmsystem.entity.Trainer;
import org.vttish.gymcrmsystem.entity.Training;

import java.util.Map;
import java.util.UUID;

public record StorageWrapper(
        Map<UUID, Trainee> trainees,
        Map<UUID, Trainer> trainers,
        Map<UUID, Training> trainings
) {}
