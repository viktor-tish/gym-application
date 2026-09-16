package org.vttish.gymcrmsystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.vttish.gymcrmsystem.entity.TrainingType;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TrainerDetailsDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
    private UUID userId;
    private TrainingType specialization;
}
