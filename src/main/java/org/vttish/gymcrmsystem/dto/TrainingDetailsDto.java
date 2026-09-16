package org.vttish.gymcrmsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainingDetailsDto {
    private UUID id;
    private String trainingName;
    private String trainingType;
    private String trainingDate;
    private String trainingDuration;
    private UUID trainerId;
    private String trainerUsername;
    private UUID traineeId;
    private String traineeUsername;
}
