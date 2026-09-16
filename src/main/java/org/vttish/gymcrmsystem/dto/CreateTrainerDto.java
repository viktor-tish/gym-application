package org.vttish.gymcrmsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vttish.gymcrmsystem.entity.TrainingType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CreateTrainerDto {
    private String firstName;
    private String lastName;
    private boolean isActive;
    private TrainingType specialization;
}
