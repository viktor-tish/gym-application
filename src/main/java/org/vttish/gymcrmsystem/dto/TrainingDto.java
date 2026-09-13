package org.vttish.gymcrmsystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.vttish.gymcrmsystem.entity.TrainingType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TrainingDto {
    private UUID traineeId;
    private UUID trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Duration trainingDuration;
}
