package org.vttish.gymcrmsystem.dto;

import lombok.*;
import org.vttish.gymcrmsystem.entity.TrainingType;

import java.time.Duration;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateTrainingDto {
    private UUID traineeId;
    private UUID trainerId;
    private String trainingName;
    private TrainingType trainingType;
    private LocalDate trainingDate;
    private Duration trainingDuration;
}
