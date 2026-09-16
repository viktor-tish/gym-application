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
public class CreateTrainingDto {
    private UUID traineeId;
    private UUID trainerId;
    private String trainingName;
    private String trainingType;
    private String trainingDate;
    private String trainingDuration;
}
