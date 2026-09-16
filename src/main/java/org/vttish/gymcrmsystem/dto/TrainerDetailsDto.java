package org.vttish.gymcrmsystem.dto;

import lombok.*;
import org.vttish.gymcrmsystem.entity.TrainingType;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TrainerDetailsDto {
    private String firstName;
    private String lastName;
    private String username;

    @ToString.Exclude
    private String password;

    private boolean isActive;
    private UUID userId;
    private TrainingType specialization;
}
