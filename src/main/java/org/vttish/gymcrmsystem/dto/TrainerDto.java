package org.vttish.gymcrmsystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.vttish.gymcrmsystem.entity.TrainingType;

import java.util.UUID;

@Getter
@Setter
public class TrainerDto {
    private String firstName;
    private String lastName;
    private String username;
    private String password;
    private boolean isActive;
    private UUID userId;
    private TrainingType specialization;
}
