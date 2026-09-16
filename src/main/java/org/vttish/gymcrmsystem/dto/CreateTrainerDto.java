package org.vttish.gymcrmsystem.dto;

import lombok.*;
import org.vttish.gymcrmsystem.entity.TrainingType;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CreateTrainerDto {
    private String firstName;
    private String lastName;
    private boolean isActive;
    private TrainingType specialization;
}
